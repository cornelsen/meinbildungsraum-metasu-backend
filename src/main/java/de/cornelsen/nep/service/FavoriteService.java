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

import de.cornelsen.nep.mapper.FavoriteMapper;
import de.cornelsen.nep.model.dto.favorites.FavoriteMarkReq;
import de.cornelsen.nep.model.dto.favorites.FavoriteOwnRsp;
import de.cornelsen.nep.model.dto.favorites.FavoriteReq;
import de.cornelsen.nep.model.dto.favorites.FavoriteUnmarkReq;
import de.cornelsen.nep.model.dto.request.SearchReq;
import de.cornelsen.nep.model.dto.search.ResourceTypeDto;
import de.cornelsen.nep.model.entity.Favorite;
import de.cornelsen.nep.nmshd.service.DataWalletService;
import de.cornelsen.nep.repository.FavoriteRepository;
import de.cornelsen.nep.service.client.SearchClientService;
import de.cornelsen.nep.util.CryptoUtils;
import de.cornelsen.nep.util.SecurityUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

@Slf4j
@Service
public class FavoriteService extends CommonSearchService {

	@Value("${application.nmshd.enabled}")
	private boolean nmshdEnabled;

	private final DataWalletService dataWalletService;
	private final FavoriteMapper favoriteMapper;
	private final FavoriteRepository favoriteRepository;

	public FavoriteService(PublisherService publisherService, SearchClientService resourceClientService, FavoriteMapper favoriteMapper, FavoriteRepository favoriteRepository, @Autowired(required = false) DataWalletService dataWalletService) {
		super(publisherService, resourceClientService);
		this.favoriteMapper = favoriteMapper;
		this.favoriteRepository = favoriteRepository;
		this.dataWalletService = dataWalletService;
	}

	@Transactional(readOnly = true)
	public Page<FavoriteOwnRsp> getFavorites(FavoriteReq req, Pageable pageable) {

		String currentUserId = SecurityUtils.currentUser().orElseThrow().getSub();

		Pageable internalPageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize());
		Page<Favorite> favorites = favoriteRepository.findAllByUserIdOrderByCreatedAtDesc(currentUserId, internalPageable);
		if (favorites.isEmpty()) {
			return Page.empty();
		}

		Map<Long, Set<String>> publisherItems = favorites.stream().collect(Collectors.groupingBy(Favorite::getPublisherId, Collectors.mapping(Favorite::getItemId, Collectors.toSet())));
		List<String> publisherItemIds = favorites.stream().map(Favorite::getItemId).toList();

		//if favorites paginated by publishDate, then should be paginated by createdAt internally only, otherwise pageable applied externally
		boolean sortInternally = pageable.getSort().isSorted() && SearchReq.getSorted(pageable).orElseThrow(IllegalAccessError::new).equals(SearchReq.SortBy.NEWEST);
		Comparator<ResourceTypeDto> compareInternal = Comparator.comparing(result -> publisherItemIds.indexOf(result.getId()));
		Comparator<ResourceTypeDto> compareExternal = SEARCH_COMPARE.apply(SearchReq.getSorted(pageable));

		AtomicLong totalElementsCount = new AtomicLong(0);

		List<FavoriteOwnRsp> sortedResult = searchById(publisherItems, searchReq -> searchReq.setSearch(req.getSearch()), pageable)
			.stream()
			.flatMap(rspWrapper -> {
				totalElementsCount.getAndAdd(rspWrapper.getTotalElements());
				return favoriteMapper.toDto(rspWrapper).stream();
			})
			.sorted(sortInternally ? compareInternal : compareExternal)
			.toList();

		return new PageImpl<>(sortedResult, pageable, totalElementsCount.get());
	}

	@Transactional
	public void markFavorite(FavoriteMarkReq req) {
		String currentUserId = SecurityUtils.currentUser().orElseThrow().getSub();
		Long publisherId = CryptoUtils.decrypt(req.getPublisherId());

		if (favoriteRepository.findByPublisherIdAndItemIdAndUserId(publisherId, req.getId(), currentUserId).isPresent()) {
			log.info("Item already marked as favourite. PublisherId: {}, itemId: {}, userId: {}", publisherId, req.getId(), currentUserId);
			return;
		}

		var favorite = new Favorite();
		favorite.setPublisherId(publisherId);
		favorite.setItemId(req.getId());
		favorite.setUserId(currentUserId);
		favorite.setCreatedAt(LocalDateTime.now());

		if (nmshdEnabled) {
			dataWalletService.storeData(favorite);
			return;
		}

		favoriteRepository.save(favorite);
		log.info("Item marked as favourite: {}", favorite);

	}

	@Transactional
	public void unmarkFavorite(FavoriteUnmarkReq req) {
		String currentUserId = SecurityUtils.currentUser().orElseThrow().getSub();
		Long publisherId = CryptoUtils.decrypt(req.getPublisherId());

		Favorite favorite = favoriteRepository.findByPublisherIdAndItemIdAndUserId(publisherId, req.getId(), currentUserId).orElseThrow();
		log.info("Removed item from favorites: {}", favorite);
		favoriteRepository.delete(favorite);
	}
}
