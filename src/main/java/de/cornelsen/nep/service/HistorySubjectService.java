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

import de.cornelsen.nep.model.dto.history.HistorySubjectReq;
import de.cornelsen.nep.model.dto.search.Subject;
import de.cornelsen.nep.model.entity.HistorySubject;
import de.cornelsen.nep.repository.HistorySubjectRepository;
import de.cornelsen.nep.util.SecurityUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class HistorySubjectService {

	private final HistorySubjectRepository historySubjectRepository;

	@Transactional
	public void addHistorySubject(HistorySubjectReq req) {
		String currentUserId = SecurityUtils.currentUser().orElseThrow().getSub();

		//validate if given key exists
		try {
			Subject.valueOf(req.getKey());
		} catch (IllegalArgumentException e) {
			log.info("Incorrect subject label given: {}", req.getKey());
			throw new IllegalArgumentException("errors.wrongValue");
		}

		HistorySubject historySubject = historySubjectRepository.findByUserIdAndKey(currentUserId, req.getKey()).orElse(new HistorySubject());
		historySubject.setCreatedAt(LocalDateTime.now());
		historySubject.setUserId(currentUserId);
		historySubject.setKey(req.getKey());

		historySubjectRepository.save(historySubject);
		log.info("Subject history added: {}", historySubject);
	}

	@Transactional(readOnly = true)
	public List<Subject> getHistorySubject(Pageable pageable) {
		String currentUserId = SecurityUtils.currentUser().orElseThrow().getSub();

		Page<HistorySubject> historySubjectList = historySubjectRepository.findAllByUserIdOrderByCreatedAtDesc(currentUserId, pageable);
		log.info("Users: {} history subject list: {}", currentUserId, historySubjectList);
		return historySubjectList.stream().map(history -> Subject.valueOf(history.getKey())).toList();
	}
}
