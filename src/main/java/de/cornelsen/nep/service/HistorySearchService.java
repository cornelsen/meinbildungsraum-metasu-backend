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

import de.cornelsen.nep.mapper.ResourceTypeMapper;
import de.cornelsen.nep.model.dto.history.HistorySearchReq;
import de.cornelsen.nep.model.dto.request.SearchReq;
import de.cornelsen.nep.model.dto.search.ResourceTypeDto;
import de.cornelsen.nep.model.dto.search.SearchResultWrapper;
import de.cornelsen.nep.model.entity.HistorySearch;
import de.cornelsen.nep.repository.HistorySearchRepository;
import de.cornelsen.nep.service.client.SearchClientService;
import de.cornelsen.nep.util.CryptoUtils;
import de.cornelsen.nep.util.SecurityUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

@Slf4j
@Service
public class HistorySearchService extends CommonSearchService {

	private final ResourceTypeMapper resourceTypeMapper;
	private final HistorySearchRepository historySearchRepository;

	public HistorySearchService(PublisherService publisherService, SearchClientService resourceClientService, ResourceTypeMapper resourceTypeMapper, HistorySearchRepository historySearchRepository) {
		super(publisherService, resourceClientService);
		this.resourceTypeMapper = resourceTypeMapper;
		this.historySearchRepository = historySearchRepository;
	}

	@Transactional
	public void addHistorySearch(HistorySearchReq req) {
		String currentUserId = SecurityUtils.currentUser().orElseThrow().getSub();
		Long publisherIdDecrypted = CryptoUtils.decrypt(req.getPublisherId());
		//validate if publisher exists
		publisherService.getPublisher(publisherIdDecrypted);

		HistorySearch historySearch = historySearchRepository.findByUserIdAndItemIdAndPublisherId(currentUserId, req.getId(), publisherIdDecrypted).orElse(new HistorySearch());
		historySearch.setCreatedAt(LocalDateTime.now());
		historySearch.setUserId(currentUserId);
		historySearch.setItemId(req.getId());
		historySearch.setPublisherId(publisherIdDecrypted);

		historySearchRepository.save(historySearch);
		log.info("History search added: {}", historySearch);
	}

	@Transactional(readOnly = true)
	public Page<ResourceTypeDto> getHistorySearch(Pageable pageable) {
		String currentUserId = SecurityUtils.currentUser().orElseThrow().getSub();

		Pageable internalPageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize());
		Page<HistorySearch> historySearch = historySearchRepository.findAllByUserIdOrderByCreatedAtDesc(currentUserId, internalPageable);
		if (historySearch.isEmpty()) {
			return new PageImpl<>(Collections.emptyList());
		}

		Map<Long, Set<String>> historyItemsByPublisher = historySearch.stream().collect(Collectors.groupingBy(HistorySearch::getPublisherId, Collectors.mapping(HistorySearch::getItemId, Collectors.toSet())));
		List<SearchResultWrapper> searchByIdResult = searchById(historyItemsByPublisher);

		List<String> sortList = historySearch.stream().map(HistorySearch::getItemId).toList();

		Comparator<ResourceTypeDto> compareInternal = Comparator.comparing(result -> sortList.indexOf(result.getId()));
		Comparator<ResourceTypeDto> compareExternal = SEARCH_COMPARE.apply(SearchReq.getSorted(pageable));

		AtomicLong totalElementsCount = new AtomicLong(0);

		List<ResourceTypeDto> sortedResultList = searchByIdResult.stream()
			.flatMap(rspWrapper -> {
				totalElementsCount.getAndAdd(rspWrapper.getTotalElements());
				return resourceTypeMapper.toDto(rspWrapper).stream();
			})
			.sorted(compareInternal.thenComparing(compareExternal))
			.toList();

		return new PageImpl<>(sortedResultList, pageable, totalElementsCount.get());
	}
}
