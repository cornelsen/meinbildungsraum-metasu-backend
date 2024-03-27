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
import de.cornelsen.nep.model.dto.favorites.FavoriteReq;
import de.cornelsen.nep.model.dto.favorites.FavoriteMarkReq;
import de.cornelsen.nep.model.dto.favorites.FavoriteUnmarkReq;
import de.cornelsen.nep.model.dto.favorites.FavoriteOwnRsp;
import de.cornelsen.nep.serializer.validate.ValidateSort;
import de.cornelsen.nep.service.FavoriteService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
public class FavoriteResource {

	private final FavoriteService favoriteService;

	@GetMapping("/api/favorites/own")
	@Operation(summary = "The REST request to get logged in user's favorites", tags = {"FavoritesResource"})
	public Page<FavoriteOwnRsp> getFavorites(@Valid FavoriteReq req, @ValidateSort(properties = {"name", "relevance", "publishDate"}) @PageableDefault(size = ApplicationConst.DEFAULT_PAGE_SIZE) Pageable pageable) {
		log.info("[REST] getFavorites, FavoriteReq: {}, pageable: {}", req, pageable);
		return favoriteService.getFavorites(req, pageable);
	}

	@PostMapping("/api/favorites/mark")
	@Operation(summary = "The REST request to add item as favorite", tags = {"FavoritesResource"})
	public void markFavorite(@Valid @RequestBody FavoriteMarkReq req) {
		log.info("[REST] markFavorite, MarkFavoriteReq: {}", req);
		favoriteService.markFavorite(req);
	}

	@PostMapping("/api/favorites/unmark")
	@Operation(summary = "The REST request to unmark item as favorite", tags = {"FavoritesResource"})
	public void unmarkFavorite(@Valid @RequestBody FavoriteUnmarkReq req) {
		log.info("[REST] unmarkFavorite, UnmarkFavoriteReq: {}", req);
		favoriteService.unmarkFavorite(req);
	}

}
