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

import de.cornelsen.nep.mapper.ArticleMapper;
import de.cornelsen.nep.model.dto.UserDetails;
import de.cornelsen.nep.model.dto.article.ArticleCreateReq;
import de.cornelsen.nep.model.dto.article.ArticleRsp;
import de.cornelsen.nep.model.dto.article.ArticleUpdateReq;
import de.cornelsen.nep.model.entity.Article;
import de.cornelsen.nep.repository.ArticleRepository;
import de.cornelsen.nep.util.CryptoUtils;
import de.cornelsen.nep.util.SecurityUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;

@Slf4j
@Service
@RequiredArgsConstructor
public class ArticleService {

	private final ArticleMapper articleMapper;
	private final ArticleRepository articleRepository;

	@Transactional(readOnly = true)
	public List<ArticleRsp> getArticles(Sort sort) {
		List<Article> articles = articleRepository.findAll(sort);
		return articleMapper.toDtoList(articles);
	}

	@Transactional(readOnly = true)
	public ArticleRsp getArticleDetails(String id) {
		Long decryptedId = CryptoUtils.decrypt(id);
		ArticleRsp articleRsp = articleRepository.findById(decryptedId).map(articleMapper::toDto).orElseThrow(NoSuchElementException::new);
		log.info("Retrieved article: {} by id: {}", articleRsp, id);
		return articleRsp;
	}

	@Transactional
	public void addArticle(ArticleCreateReq req) {
		UserDetails currentUser = SecurityUtils.currentUser().orElseThrow();
		if (!currentUser.isAdmin()) {
			throw new AccessDeniedException("cannotAdd");
		}

		var article = articleMapper.toEntity(req);
		article.setCreatedBy(currentUser.getSub());
		article.setCreatedAt(LocalDateTime.now());
		article.setUpdatedAt(article.getCreatedAt());

		articleRepository.save(article);
		log.info("Article added: {}", article);
	}

	@Transactional
	public void updateArticle(ArticleUpdateReq req) {
		if (!SecurityUtils.currentUser().orElseThrow().isAdmin()) {
			throw new AccessDeniedException("cannotEdit");
		}

		Long decryptedId = CryptoUtils.decrypt(req.getId());

		Article existingArticle = articleRepository.findById(decryptedId).orElseThrow(NoSuchElementException::new);
		log.info("Found existing article: {}, by id: {}", existingArticle, decryptedId);

		Article updatedArticle = articleMapper.toEntity(req, existingArticle);
		updatedArticle.setUpdatedAt(LocalDateTime.now());
		log.info("Article updated: {}", updatedArticle);
	}

	@Transactional
	public void deleteArticle(String id) {
		if (!SecurityUtils.currentUser().orElseThrow().isAdmin()) {
			throw new AccessDeniedException("cannotDelete");
		}

		Long decryptedId = CryptoUtils.decrypt(id);

		articleRepository.deleteById(decryptedId);
		log.info("Article deleted: {}", decryptedId);
	}
}
