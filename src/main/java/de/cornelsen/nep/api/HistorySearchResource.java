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
import de.cornelsen.nep.model.dto.history.HistorySearchReq;
import de.cornelsen.nep.model.dto.search.ResourceTypeDto;
import de.cornelsen.nep.service.HistorySearchService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
public class HistorySearchResource {

	private final HistorySearchService historySearchService;

	@GetMapping("/api/history/search/own")
	@Operation(summary = "The REST request to get history search items", tags = {"HistoryResource"})
	public Page<ResourceTypeDto> getHistorySearch(@PageableDefault(size = ApplicationConst.DEFAULT_PAGE_SIZE) Pageable pageable) {
		log.info("[REST] getHistorySearch, pageable: {}", pageable);
		return historySearchService.getHistorySearch(pageable);
	}

	@PostMapping("/api/history/search")
	@Operation(summary = "The REST request to add history search item", tags = {"HistoryResource"})
	public void historySearch(@Valid @RequestBody HistorySearchReq req) {
		log.info("[REST] add historySearch: {}", req);
		historySearchService.addHistorySearch(req);
	}

}
