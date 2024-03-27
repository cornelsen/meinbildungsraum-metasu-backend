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

import de.cornelsen.nep.mapper.RatingMapper;
import de.cornelsen.nep.model.dto.UserDetails;
import de.cornelsen.nep.model.dto.rating.RatingCreateReq;
import de.cornelsen.nep.model.dto.rating.RatingEditReq;
import de.cornelsen.nep.model.dto.rating.RatingOwnRsp;
import de.cornelsen.nep.model.dto.rating.RatingRsp;
import de.cornelsen.nep.model.dto.request.SearchReq;
import de.cornelsen.nep.model.dto.search.ResourceTypeDto;
import de.cornelsen.nep.model.entity.Rating;
import de.cornelsen.nep.repository.RatingRepository;
import de.cornelsen.nep.service.client.SearchClientService;
import de.cornelsen.nep.util.CryptoUtils;
import de.cornelsen.nep.util.SecurityUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Service
public class RatingService extends CommonSearchService {

	private final RatingMapper ratingMapper;
	private final RatingRepository ratingRepository;

	public RatingService(PublisherService publisherService, SearchClientService resourceClientService, RatingMapper ratingMapper, RatingRepository ratingRepository) {
		super(publisherService, resourceClientService);
		this.ratingMapper = ratingMapper;
		this.ratingRepository = ratingRepository;
	}

	@Transactional(readOnly = true)
	public RatingRsp getItemRating(String encryptedPublisherId, String itemId, short[] rating, Pageable pageable) {
		Long publisherId = CryptoUtils.decrypt(encryptedPublisherId);

		var result = new RatingRsp();

		ratingRepository.countItemRatings(publisherId, itemId).forEach(rate -> result.getVotes().put(Integer.valueOf(rate.getRate()), rate.getTotal()));

		long totalRatings = ratingRepository.countTotalRatings(publisherId, itemId);
		result.setTotalRatingsCount(totalRatings);

		long emptyRatings = ratingRepository.countEmptyRatings(publisherId, itemId);
		result.setEmptyRatings(emptyRatings);

		boolean all = rating.length == 0 || rating[0] == 0;
		Page<RatingRsp.Comment> comments = ratingRepository.findAllByPublisherIdAndItemIdAndRating(publisherId, itemId, rating, all, pageable).map(ratingMapper::toDto);
		result.setComments(comments);

		Optional<UserDetails> userDetailsOptional = SecurityUtils.currentUser();
		userDetailsOptional.ifPresent(x -> {
			boolean isRatedByMyself = ratingRepository.findByPublisherIdAndItemIdAndUserId(publisherId, itemId, x.getSub()).isPresent();
			result.setRated(isRatedByMyself);
		});

		result.calculateRating();
		return result;
	}

	@Transactional
	public void rateItem(RatingCreateReq req) {
		String currentUserId = SecurityUtils.currentUser().orElseThrow().getSub();
		Rating rating = ratingMapper.toEntity(req);
		if (ratingRepository.findByPublisherIdAndItemIdAndUserId(rating.getPublisherId(), rating.getItemId(), currentUserId).isPresent()) {
			log.warn("Item already rated, publisherId {} itemID {}, currentUser {}", req.getPublisherId(), req.getItemId(), currentUserId);
			throw new IllegalArgumentException("errors.alreadyRated");
		}
		rating.setCreatedAt(LocalDateTime.now());
		rating.setModifiedAt(rating.getCreatedAt());
		rating.setUserId(currentUserId);
		ratingRepository.save(rating);
	}

	@Transactional(readOnly = true)
	public Page<RatingOwnRsp> getOwnRatings(Pageable pageable) {

		String currentUserId = SecurityUtils.currentUser().orElseThrow().getSub();

		Pageable internalPageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize());
		Page<Rating> ratings = ratingRepository.findAllByUserIdOrderByModifiedAtDesc(currentUserId, internalPageable);
		if (ratings.isEmpty()) {
			return new PageImpl<>(Collections.emptyList());
		}
		Map<Long, Set<String>> publisherItems = ratings.stream().collect(Collectors.groupingBy(Rating::getPublisherId, Collectors.mapping(Rating::getItemId, Collectors.toSet())));

		Map<String, Rating> ratingsMap = ratings.stream().collect(Collectors.toMap(Rating::getItemId, Function.identity()));
		List<String> sortedRatingsList = ratings.stream().map(Rating::getItemId).toList();

		//if rating paginated by publishDate, then should be paginated by createdAt internally only, otherwise pageable applied externally
		boolean sortInternally = pageable.getSort().isSorted() && SearchReq.getSorted(pageable).orElseThrow(IllegalAccessError::new).equals(SearchReq.SortBy.NEWEST);
		Comparator<ResourceTypeDto> compareInternal = Comparator.comparing(result -> sortedRatingsList.indexOf(result.getId()));
		Comparator<ResourceTypeDto> compareExternal = SEARCH_COMPARE.apply(SearchReq.getSorted(pageable));

		AtomicLong totalElementsCount = new AtomicLong(0);

		List<RatingOwnRsp> sortedResult = searchById(publisherItems)
			.stream()
			.flatMap(item -> {
				totalElementsCount.getAndAdd(item.getTotalElements());
				return ratingMapper.toDto(item).stream();
			})
			.peek(rate -> ratingsMap.computeIfPresent(rate.getId(), (s, rating) -> {
				RatingOwnRsp.RatingRsp myRating = ratingMapper.toDtoOwn(rating);
				rate.setMyRating(myRating);
				return rating;
			}))
			.sorted(sortInternally ? compareInternal : compareExternal)
			.toList();

		return new PageImpl<>(sortedResult, pageable, totalElementsCount.get());
	}

	@Transactional
	public void editItemRate(RatingEditReq req) {
		Long decryptedId = CryptoUtils.decrypt(req.getId());
		Rating rating = ratingRepository.findById(decryptedId).orElseThrow();
		Optional<UserDetails> currentUser = SecurityUtils.currentUser();
		if (currentUser.isPresent() && !currentUser.get().isAdmin() && !currentUser.get().getSub().equals(rating.getUserId())) {
			log.warn("Resource cannot be edited, id: {}, user: {}", decryptedId, currentUser);
			throw new AccessDeniedException("cannotEdit");
		}
		rating.setText(StringUtils.isBlank(req.getText()) ? null : req.getText());
		rating.setRating(req.getRating());
		rating.setModifiedAt(LocalDateTime.now());
	}

	@Transactional
	public void deleteItemRate(String id) {
		Long decryptedId = CryptoUtils.decrypt(id);
		Rating rating = ratingRepository.findById(decryptedId).orElseThrow();
		Optional<UserDetails> currentUser = SecurityUtils.currentUser();
		if (currentUser.isPresent() && !currentUser.get().isAdmin() && !currentUser.get().getSub().equals(rating.getUserId())) {
			log.warn("Resource cannot be edited, id: {}, user: {}", decryptedId, currentUser);
			throw new AccessDeniedException("cannotEdit");
		}
		log.debug("Delete item's rate: {}", rating);
		ratingRepository.delete(rating);
	}
}
