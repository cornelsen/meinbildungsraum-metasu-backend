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
import de.cornelsen.nep.model.dto.news.NewsCreateReq;
import de.cornelsen.nep.model.dto.news.NewsRsp;
import de.cornelsen.nep.model.dto.news.NewsUpdateReq;
import de.cornelsen.nep.serializer.validate.ValidateSort;
import de.cornelsen.nep.service.NewsService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
public class NewsResource {

	private final NewsService newsService;

	@GetMapping("/api/news")
	@Operation(summary = "The REST request to get paged news", tags = {"NewsResource"})
	public Page<NewsRsp> getNews(@ValidateSort(properties = {"createdAt", "modifiedAt"}) @PageableDefault(size = ApplicationConst.DEFAULT_PAGE_SIZE) Pageable pageable) {
		log.info("[REST] getNews, pageable: {}", pageable);
		return newsService.getNews(pageable);
	}

	@GetMapping("/api/news/{id}")
	@Operation(summary = "The REST request to get news details", tags = {"NewsResource"})
	public NewsRsp getNewsDetails(@PathVariable String id) {
		log.info("[REST] getNewsDetails, id: {}", id);
		return newsService.getNewsDetails(id);
	}

	@PostMapping("/api/news")
	@Operation(summary = "The REST request to add news", tags = {"NewsResource"})
	public void createNews(@Valid @RequestBody NewsCreateReq req) {
		log.info("[REST] createNews, NewsCreateReq: {}", req);
		newsService.addNews(req);
	}

	@PutMapping("/api/news")
	@Operation(summary = "The REST request to update news", tags = {"NewsResource"})
	public void upDateNews(@Valid @RequestBody NewsUpdateReq req) {
		log.info("[REST] updateNews, NewsUpdateReq: {}", req);
		newsService.updateNews(req);
	}

	@DeleteMapping("/api/news/{id}")
	@Operation(summary = "The REST request to delete news", tags = {"NewsResource"})
	public void deleteNews(@PathVariable String id) {
		log.info("[REST] deleteNews, id: {}", id);
		newsService.deleteNews(id);
	}
}
