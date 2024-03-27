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

package de.cornelsen.nep.service.client;

import com.fasterxml.jackson.core.type.TypeReference;
import de.cornelsen.nep.configuration.ApplicationConfiguration;
import de.cornelsen.nep.model.dto.lti.ResourceSetType;
import de.cornelsen.nep.model.dto.publisher.PublisherRsp;
import de.cornelsen.nep.model.dto.request.SearchReq;
import de.cornelsen.nep.model.dto.search.ProviderType;
import de.cornelsen.nep.model.dto.search.ResourceDetailsDto;
import de.cornelsen.nep.model.dto.search.SearchResultWrapper;
import de.cornelsen.nep.serializer.AsOrSerializer;
import de.cornelsen.nep.util.StringUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.web.util.UriBuilder;
import org.springframework.web.util.UriComponentsBuilder;
import org.springframework.web.util.UriUtils;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.lang.reflect.Field;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class SearchClientService extends ResourceClient {

	private final ApplicationConfiguration applicationConfiguration;

	public List<SearchResultWrapper> search(Map<PublisherRsp, SearchReq> request, Pageable pageable) {
		return Flux.fromIterable(request.keySet())
			.flatMap(resource -> callAPI(
				applyUri(resource, request.get(resource), pageable),
				ResourceSetType.class,
				resource
			).mapNotNull(applyResultAdditionalValues(resource)))
			.collectList()
			.block();
	}

	public List<SearchResultWrapper> search(List<PublisherRsp> publishers, SearchReq request, Pageable pageable) {
		return Flux.fromIterable(publishers)
			.flatMap(resource -> callAPI(
				applyUri(resource, request, pageable),
				ResourceSetType.class, resource).mapNotNull(applyResultAdditionalValues(resource)))
			.collectList()
			.block();
	}

	public ResourceDetailsDto details(PublisherRsp publisher, String id) {
		return callAPI(
			uri -> UriComponentsBuilder.fromHttpUrl(publisher.getSearchUrl() + applicationConfiguration.getDetailsSuffix()).build(id),
			ResourceDetailsDto.class, publisher
		).onErrorResume(WebClientResponseException.class, ex -> ex.getStatusCode().is4xxClientError() ? Mono.error(() -> new NoSuchElementException("Resource %s not found".formatted(id))) : Mono.error(ex))
			.mapNotNull(detail -> {
				ResourceDetailsDto result = detail.getBody();
				result.setPublisher(publisher.getName());
				result.setPublisherId(publisher.getId());
				return result;
			}).block();
	}

	private Function<UriBuilder, URI> applyUri(PublisherRsp publisher, SearchReq request, Pageable pageable) {
		Optional<SearchReq.SortBy> sorted = SearchReq.getSorted(pageable);
		return uri -> UriComponentsBuilder.fromHttpUrl(publisher.getSearchUrl())
			.path(applicationConfiguration.getSearchSuffix())
			.queryParam("limit", (pageable.getPageNumber() + 1) * pageable.getPageSize())
			.queryParam("filters", getFilterParams(publisher, request))
			.queryParam("offset", 0)
			.queryParam("sort", sorted.map(SearchReq.SortBy::getSort).orElse(null))
			.queryParam("orderBy", sorted.map(SearchReq.SortBy::getOrderBy).orElse(null))
			.queryParam("fields", "")
			.build().toUri();
	}

	private Function<ResponseEntity<ResourceSetType>, SearchResultWrapper> applyResultAdditionalValues(PublisherRsp resource) {
		return rsp -> {
			var result = new SearchResultWrapper();
			result.setResourceSetType(rsp.getBody());

			Optional<String> totalElementsHeaderOptional = Optional.ofNullable(rsp.getHeaders().getFirst(ResourceClient.TOTAL_ELEMENTS_PAGINATION_HEADER));
			result.setTotalElements(totalElementsHeaderOptional.map(Long::valueOf).orElse(0L));

			result.setPublisher(resource);
			return result;
		};
	}

	/**
	 * e.g. subject='geometry' AND publishDate>'2017-01-01'
	 * ?filter=subject%3D%27geometry%27%20AND%20publishDate%3E%272017%3D01%3D01%27
	 *
	 * @param filterObject
	 * @return
	 */
	private String getFilterParams(PublisherRsp publisher, SearchReq filterObject) {
		Set<String> processAsOrFields = Arrays.stream(filterObject.getClass().getDeclaredFields())
			.filter(f -> Arrays.stream(f.getAnnotations()).anyMatch(AsOrSerializer.class::isInstance))
			.map(Field::getName)
			.collect(Collectors.toSet());

		if (publisher.getName().equals(applicationConfiguration.getDufProviderName())) {
			filterObject.setProviderType(ProviderType.DUF);
		}
		Map<String, ?> paramsMap = mapper.convertValue(filterObject, new TypeReference<Map<String, Object>>() {
		});
		String filterParam = paramsMap.entrySet()
			.stream()
			.map(e -> e.getKey().concat(processAsOrFields.contains(e.getKey()) ? "~'" : "='").concat(StringUtil.toIndentedString(e.getValue())).concat("'"))
			.collect(Collectors.joining(" AND "));
		String encodedFilterParams = UriUtils.encode(filterParam, StandardCharsets.UTF_8);
		log.debug("{}: filter params: {} encoded: {}", publisher.getName(), filterParam, encodedFilterParams);
		return encodedFilterParams;
	}
}
