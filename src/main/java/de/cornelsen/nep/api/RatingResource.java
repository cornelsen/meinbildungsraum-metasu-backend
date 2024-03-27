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
import de.cornelsen.nep.model.dto.rating.RatingCreateReq;
import de.cornelsen.nep.model.dto.rating.RatingEditReq;
import de.cornelsen.nep.model.dto.rating.RatingOwnRsp;
import de.cornelsen.nep.model.dto.rating.RatingRsp;
import de.cornelsen.nep.serializer.validate.ValidateSort;
import de.cornelsen.nep.service.RatingService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
public class RatingResource {

	private final RatingService ratingService;

	@GetMapping("/api/ratings/{publisherId}/{itemId}")
	@Operation(summary = "The REST request to get item's rating by given publisher and item id", tags = {"RatingResource"})
	public ResponseEntity<RatingRsp> getItemRatings(@PathVariable String publisherId, @PathVariable String itemId, @RequestParam short [] rating, @ValidateSort(properties = "createdAt") @PageableDefault(size = ApplicationConst.DEFAULT_PAGE_SIZE, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
		log.info("[REST] getItemRatings, {} {}", publisherId, itemId);
		return ResponseEntity.ok(ratingService.getItemRating(publisherId, itemId, rating, pageable));
	}

	@GetMapping("/api/ratings/own")
	@Operation(summary = "The REST request to get my own ratings", tags = {"RatingResource"})
	public Page<RatingOwnRsp> getOwnRatings(@ValidateSort(properties = {"name", "relevance", "publishDate"}) @PageableDefault(size = ApplicationConst.DEFAULT_PAGE_SIZE) Pageable pageable) {
		log.info("[REST] getOwnRatings");
		return ratingService.getOwnRatings(pageable);
	}

	@PostMapping("/api/ratings")
	@Operation(summary = "The REST request to rate an item", tags = {"RatingResource"})
	public void rateItem(@Valid @RequestBody RatingCreateReq req) {
		log.info("[REST] rateItem, {}", req);
		ratingService.rateItem(req);
	}

	@PutMapping("/api/ratings")
	@Operation(summary = "The REST request to edit item's rate", tags = {"RatingResource"})
	public void editItemRate(@Valid @RequestBody RatingEditReq req) {
		log.info("[REST] editItemRate, {}", req);
		ratingService.editItemRate(req);
	}

	@DeleteMapping("/api/ratings/{id}")
	@Operation(summary = "The REST request to delete item's rate", tags = {"RatingResource"})
	public void deleteItemRate(@PathVariable String id) {
		log.info("[REST] deleteItemRate, {}", id);
		ratingService.deleteItemRate(id);
	}

}
