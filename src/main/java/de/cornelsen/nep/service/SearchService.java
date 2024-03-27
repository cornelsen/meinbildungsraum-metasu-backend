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
import de.cornelsen.nep.model.dto.UserDetails;
import de.cornelsen.nep.model.dto.publisher.PublisherRsp;
import de.cornelsen.nep.model.dto.rating.RatingRsp;
import de.cornelsen.nep.model.dto.request.SearchReq;
import de.cornelsen.nep.model.dto.search.ResourceDetailsDto;
import de.cornelsen.nep.model.dto.search.ResourceTypeDto;
import de.cornelsen.nep.model.dto.search.SearchResultWrapper;
import de.cornelsen.nep.model.entity.Favorite;
import de.cornelsen.nep.repository.FavoriteRepository;
import de.cornelsen.nep.repository.RatingRepository;
import de.cornelsen.nep.service.client.SearchClientService;
import de.cornelsen.nep.util.CryptoUtils;
import de.cornelsen.nep.util.SecurityUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

@Slf4j
@Service
public class SearchService extends CommonSearchService {

	private final ResourceTypeMapper resourceTypeMapper;
	private final RatingRepository ratingRepository;
	private final FavoriteRepository favoriteRepository;

	public SearchService(PublisherService publisherService, SearchClientService resourceClientService, ResourceTypeMapper resourceTypeMapper, RatingRepository ratingRepository, FavoriteRepository favoriteRepository) {
		super(publisherService, resourceClientService);
		this.resourceTypeMapper = resourceTypeMapper;
		this.ratingRepository = ratingRepository;
		this.favoriteRepository = favoriteRepository;
	}

	public Page<ResourceTypeDto> searchAndAggregate(SearchReq request, Pageable pageable) {
		List<PublisherRsp> activePublishers = publisherService.getActivePublishers();
		if (request.getPublisher() != null) {
			activePublishers = activePublishers.stream().filter(p -> p.getId().equals(request.getPublisher())).toList();
		}

		AtomicLong totalElementsCount = new AtomicLong(0);
		List<SearchResultWrapper> searchResult = resourceClientService.search(activePublishers, request, pageable);

		List<ResourceTypeDto> paginatedResultList = searchResult
			.stream()
			.flatMap(rspWrapper -> {
				totalElementsCount.getAndAdd(rspWrapper.getTotalElements());
				return resourceTypeMapper.toDto(rspWrapper).stream();
			})
			.sorted(SEARCH_COMPARE.apply(SearchReq.getSorted(pageable)))
			.skip(pageable.getOffset())
			.limit(pageable.getPageSize())
			.toList();

		return new PageImpl<>(paginatedResultList, pageable, totalElementsCount.get());
	}

	public ResourceDetailsDto getDetails(String encryptedPublisherId, String itemId) {
		Long publisherId = CryptoUtils.decrypt(encryptedPublisherId);
		Optional<UserDetails> userDetailsOptional = SecurityUtils.currentUser();
		PublisherRsp publisher = publisherService.getPublisher(publisherId);

		ResourceDetailsDto resourceDetails = resourceClientService.details(publisher, itemId);

		long totalRatings = ratingRepository.countTotalRatings(publisherId, itemId);
		resourceDetails.setTotalRatingsCount(totalRatings);

		var ratingHelperObject = new RatingRsp();
		ratingRepository.countItemRatings(publisherId, itemId).forEach(rate -> ratingHelperObject.getVotes().put(Integer.valueOf(rate.getRate()), rate.getTotal()));
		ratingHelperObject.calculateRating();
		resourceDetails.setCurrentRating(ratingHelperObject.getRating());

		if (userDetailsOptional.isPresent()) {
			Optional<Favorite> favorite = favoriteRepository.findByPublisherIdAndItemIdAndUserId(publisherId, itemId, userDetailsOptional.get().getSub());
			resourceDetails.setFavorite(favorite.isPresent());

			boolean isRatedByMyself = ratingRepository.findByPublisherIdAndItemIdAndUserId(publisherId, itemId, userDetailsOptional.get().getSub()).isPresent();
			resourceDetails.setRated(isRatedByMyself);
		}

		return resourceDetails;
	}
}
