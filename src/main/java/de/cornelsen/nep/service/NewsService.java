/*
 * Copyright (c) 2023-2024 Cornelsen Verlag GmbH
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

import de.cornelsen.nep.mapper.NewsMapper;
import de.cornelsen.nep.model.dto.UserDetails;
import de.cornelsen.nep.model.dto.news.NewsCreateReq;
import de.cornelsen.nep.model.dto.news.NewsRsp;
import de.cornelsen.nep.model.dto.news.NewsUpdateReq;
import de.cornelsen.nep.model.entity.News;
import de.cornelsen.nep.repository.NewsRepository;
import de.cornelsen.nep.util.CryptoUtils;
import de.cornelsen.nep.util.SecurityUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.NoSuchElementException;

@Slf4j
@Service
@RequiredArgsConstructor
public class NewsService {

	private final NewsMapper newsMapper;
	private final NewsRepository newsRepository;

	@Transactional(readOnly = true)
	public Page<NewsRsp> getNews(Pageable pageable) {
		return newsRepository.findAll(pageable).map(newsMapper::toDto);
	}

	@Transactional(readOnly = true)
	public NewsRsp getNewsDetails(String id) {
		Long decryptedId = CryptoUtils.decrypt(id);
		NewsRsp newsRsp = newsRepository.findById(decryptedId).map(newsMapper::toDto).orElseThrow(NoSuchElementException::new);
		log.info("Retrieved news: {} by id: {}", newsRsp, id);
		return newsRsp;
	}

	@Transactional
	public void addNews(NewsCreateReq req) {
		UserDetails currentUser = SecurityUtils.currentUser().orElseThrow();
		if (!currentUser.isAdmin()) {
			throw new AccessDeniedException("cannotAdd");
		}

		var news = newsMapper.toEntity(req);
		news.setCreatedBy(currentUser.getSub());
		news.setCreatedAt(LocalDateTime.now());
		news.setUpdatedAt(LocalDateTime.now());

		newsRepository.save(news);
		log.info("News added: {}", news);
	}

	@Transactional
	public void updateNews(NewsUpdateReq req) {
		if (!SecurityUtils.currentUser().orElseThrow().isAdmin()) {
			throw new AccessDeniedException("cannotEdit");
		}

		Long decryptedId = CryptoUtils.decrypt(req.getId());

		News existingNews = newsRepository.findById(decryptedId).orElseThrow(NoSuchElementException::new);
		log.info("Found existing news: {}, by id: {}", existingNews, decryptedId);

		News updatedNews = newsMapper.toEntity(req, existingNews);
		updatedNews.setUpdatedAt(LocalDateTime.now());
		log.info("News updated: {}", updatedNews);
	}

	@Transactional
	public void deleteNews(String id) {
		if (!SecurityUtils.currentUser().orElseThrow().isAdmin()) {
			throw new AccessDeniedException("cannotDelete");
		}

		Long decryptedId = CryptoUtils.decrypt(id);

		newsRepository.deleteById(decryptedId);
		log.info("News deleted: {}", decryptedId);
	}
}
