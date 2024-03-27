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

import de.cornelsen.nep.model.dto.article.ArticleCreateReq;
import de.cornelsen.nep.model.dto.article.ArticleRsp;
import de.cornelsen.nep.model.dto.article.ArticleUpdateReq;
import de.cornelsen.nep.service.ArticleService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
public class ArticleResource {

	private final ArticleService articleService;

	@GetMapping("/api/articles")
	@Operation(summary = "The REST request to get paged articles", tags = {"ArticleResource"})
	public List<ArticleRsp> getArticles(Sort sort) {
		log.info("[REST] getArticles, sort: {}", sort);
		return articleService.getArticles(sort);
	}

	@GetMapping("/api/articles/{id}")
	@Operation(summary = "The REST request to get article details", tags = {"ArticleResource"})
	public ArticleRsp getArticleDetails(@PathVariable String id) {
		log.info("[REST] getArticleDetails, id: {}", id);
		return articleService.getArticleDetails(id);
	}

	@PostMapping("/api/articles")
	@Operation(summary = "The REST request to add article", tags = {"ArticleResource"})
	public void createArticle(@Valid @RequestBody ArticleCreateReq req) {
		log.info("[REST] createArticle, ArticleCreateReq: {}", req);
		articleService.addArticle(req);
	}

	@PutMapping("/api/articles")
	@Operation(summary = "The REST request to update article", tags = {"ArticleResource"})
	public void updateArticle(@Valid @RequestBody ArticleUpdateReq req) {
		log.info("[REST] updateArticle, ArticleUpdateReq: {}", req);
		articleService.updateArticle(req);
	}

	@DeleteMapping("/api/articles/{id}")
	@Operation(summary = "The REST request to delete article", tags = {"ArticleResource"})
	public void deleteArticle(@PathVariable String id) {
		log.info("[REST] deleteArticle, id: {}", id);
		articleService.deleteArticle(id);
	}
}
