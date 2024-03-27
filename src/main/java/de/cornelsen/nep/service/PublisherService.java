/*
 * Copyright (c) 2023 Cornelsen Verlag GmbH
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package de.cornelsen.nep.service;

import de.cornelsen.nep.mapper.PublisherMapper;
import de.cornelsen.nep.model.dto.publisher.PublisherCreateReq;
import de.cornelsen.nep.model.dto.publisher.PublisherRsp;
import de.cornelsen.nep.model.dto.publisher.PublisherUpdateReq;
import de.cornelsen.nep.model.entity.Publisher;
import de.cornelsen.nep.model.entity.enums.PublisherStatus;
import de.cornelsen.nep.repository.PublisherRepository;
import de.cornelsen.nep.util.CryptoUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class PublisherService {

	private final PublisherMapper publisherMapper;
	private final PublisherRepository publisherRepository;

	@Transactional(readOnly = true)
	public List<PublisherRsp> getPublishers() {
		List<Publisher> publishers = publisherRepository.findAllByOrderByTitle();
		log.info("Retrieved publishers list: {}", publishers);
		return publisherMapper.toDto(publishers);
	}

	@Transactional(readOnly = true)
	public PublisherRsp getPublisher(Long id) {
		Publisher publisher = publisherRepository.findById(id).orElseThrow(NoSuchElementException::new);
		log.info("Retrieved publisher: {}", publisher);
		return publisherMapper.toDto(publisher);
	}

	@Transactional(readOnly = true)
	public List<PublisherRsp> getActivePublishers() {
		return publisherRepository.findAllByStatusIsOrderByName(PublisherStatus.ACTIVE).stream().map(publisherMapper::toDto).toList();
	}

	@Transactional(readOnly = true)
	public List<PublisherRsp> getById(Set<Long> id) {
		return publisherRepository.findAllByIdIn(id).stream().map(publisherMapper::toDto).toList();
	}

	@Transactional
	public Long addPublisher(PublisherCreateReq publisherCreateReq) {
		Publisher publisher = publisherMapper.toEntity(publisherCreateReq);
		publisher.setUpdatedAt(LocalDateTime.now());
		publisher.setCreatedAt(LocalDateTime.now());
		publisher.setStatus(PublisherStatus.ACTIVE);
		log.info("Adding new publisher: {}", publisher);
		return publisherRepository.save(publisher).getId();
	}

	@Transactional
	public void addPendingPublisher(PublisherCreateReq publisherCreateReq) {
		Publisher publisher = publisherMapper.toEntity(publisherCreateReq);
		publisher.setUpdatedAt(LocalDateTime.now());
		publisher.setCreatedAt(LocalDateTime.now());
		publisher.setStatus(PublisherStatus.PENDING);
		log.info("Adding new (pending) publisher: {}", publisher);
		publisherRepository.save(publisher);
	}


	@Transactional
	public PublisherRsp updatePublisher(PublisherUpdateReq publisherReq) {
		Publisher publisher = publisherRepository.findById(CryptoUtils.decrypt(publisherReq.getId())).orElseThrow();

		Publisher updatedPublisher = publisherMapper.toEntity(publisherReq, publisher);
		updatedPublisher.setUpdatedAt(LocalDateTime.now());
		publisherRepository.save(updatedPublisher);
		log.info("Updated publisher: {}", updatedPublisher);

		return publisherMapper.toDto(updatedPublisher);
	}

	@Transactional
	public void deletePublisher(String id) {
		Publisher publisher = publisherRepository.findById(CryptoUtils.decrypt(id)).orElseThrow();
		publisherRepository.delete(publisher);
		log.info("Removed publisher: {}", publisher);
	}

	@Transactional
	public PublisherRsp acceptPublisher(String id) {
		Publisher publisher = publisherRepository.findById(CryptoUtils.decrypt(id)).orElseThrow();
		publisher.setStatus(PublisherStatus.ACTIVE);
		log.info("Accepted publisher: {}", publisher);
		return publisherMapper.toDto(publisher);
	}
}
