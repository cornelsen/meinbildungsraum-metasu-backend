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

package de.cornelsen.nep.api;

import de.cornelsen.nep.configuration.ApplicationConst;
import de.cornelsen.nep.configuration.CaffeineConfiguration;
import de.cornelsen.nep.model.dto.filters.FiltersRsp;
import de.cornelsen.nep.model.dto.request.SearchReq;
import de.cornelsen.nep.model.dto.search.ResourceDetailsDto;
import de.cornelsen.nep.model.dto.search.ResourceTypeDto;
import de.cornelsen.nep.serializer.validate.ValidateSort;
import de.cornelsen.nep.service.FilterService;
import de.cornelsen.nep.service.SearchService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
public class SearchResource {

	private final SearchService searchService;
	private final FilterService filterService;

	@GetMapping("/api/filters")
	@Cacheable(cacheNames = CaffeineConfiguration.CACHE_FILTER_NAME)
	@Operation(summary = "The REST read request message for the filtersList() API call.", description = "To obtain, read, the list of filters that are supported by a LOR. ", tags = {"SearchApi"})
	public FiltersRsp filtersList() {
		log.info("[REST] filtersList");
		return filterService.getFiltersList();
	}

	@GetMapping("/api/search/most-relevant")
	@Operation(summary = "The REST read request message for the searchForResources() API call.", description = "This is the search request. The criteria for the search are passed as query parameters and the set of identified resources are returned in the payload for the response message. ", tags = {"SearchApi"})
	public Page<ResourceTypeDto> mostRelevant(@Valid SearchReq request, @ValidateSort(properties = {"name", "relevance", "publishDate"}) @PageableDefault(size = ApplicationConst.DEFAULT_PAGE_SIZE) Pageable pageable) {
		log.info("[REST] mostRelevant, request: {}", request);
		return searchService.searchAndAggregate(request, pageable);
	}

	@GetMapping("/api/search/related")
	@Operation(summary = "Get related items.", tags = {"SearchApi",})
	public Page<ResourceTypeDto> related(@Valid SearchReq request, @ValidateSort(properties = {"name", "relevance", "publishDate"}) @PageableDefault(size = ApplicationConst.DEFAULT_PAGE_SIZE) Pageable pageable) {
		log.info("[REST] related, request");
		return searchService.searchAndAggregate(request, pageable);
	}

	@GetMapping("/api/search/details/{publisherId}/{itemId}")
	@Operation(summary = "Get product details.", tags = {"SearchApi",})
	public ResourceDetailsDto details(@PathVariable String publisherId, @PathVariable String itemId) {
		log.info("[REST] item details request, publisherId {}, itemId: {}", publisherId, itemId);
		return searchService.getDetails(publisherId, itemId);
	}
}
