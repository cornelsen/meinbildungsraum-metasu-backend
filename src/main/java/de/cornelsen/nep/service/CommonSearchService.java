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

import de.cornelsen.nep.model.dto.publisher.PublisherRsp;
import de.cornelsen.nep.model.dto.request.SearchReq;
import de.cornelsen.nep.model.dto.search.ResourceTypeDto;
import de.cornelsen.nep.model.dto.search.SearchResultWrapper;
import de.cornelsen.nep.service.client.SearchClientService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public abstract class CommonSearchService {

	protected final PublisherService publisherService;
	protected final SearchClientService resourceClientService;

	public static final Function<Optional<SearchReq.SortBy>, Comparator<ResourceTypeDto>> SEARCH_COMPARE = sortBy -> {
		if (sortBy.isEmpty()) {
			return Comparator.comparing(ResourceTypeDto::getPublishDate, Comparator.nullsLast(Comparator.reverseOrder()));
		}
		return switch (sortBy.get()) {
			case NEWEST ->
				Comparator.comparing(ResourceTypeDto::getPublishDate, Comparator.nullsLast(Comparator.reverseOrder()));
			case OLDEST ->
				Comparator.comparing(ResourceTypeDto::getPublishDate, Comparator.nullsLast(Comparator.naturalOrder()));
			case ALPHABETIC -> Comparator.comparing(ResourceTypeDto::getName);
			case MOST_VIEWED -> Comparator.comparing(ResourceTypeDto::getRelevance);
		};
	};

	protected List<SearchResultWrapper> searchById(Map<Long, Set<String>> publisherItems) {
		return searchById(publisherItems, searchReq -> {
		}, PageRequest.of(0, 20));
	}

	protected List<SearchResultWrapper> searchById(Map<Long, Set<String>> publisherItems, Consumer<SearchReq> requestFilter, Pageable pageable) {

		Function<PublisherRsp, SearchReq> searchReqFunction = publisher -> {
			var searchRequest = new SearchReq();
			searchRequest.setId(publisherItems.get(publisher.getDecryptedId()));
			requestFilter.accept(searchRequest);
			return searchRequest;
		};

		Map<PublisherRsp, SearchReq> searchRequestsMap = publisherService.getById(publisherItems.keySet())
			.stream()
			.collect(Collectors.toMap(Function.identity(), searchReqFunction));

		return resourceClientService.search(searchRequestsMap, pageable);
	}
}
