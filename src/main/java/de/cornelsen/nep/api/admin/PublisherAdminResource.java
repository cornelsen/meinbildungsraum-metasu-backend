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

package de.cornelsen.nep.api.admin;

import de.cornelsen.nep.configuration.CaffeineConfiguration;
import de.cornelsen.nep.model.dto.publisher.PublisherCreateReq;
import de.cornelsen.nep.model.dto.publisher.PublisherRsp;
import de.cornelsen.nep.model.dto.publisher.PublisherUpdateReq;
import de.cornelsen.nep.service.PublisherService;
import de.cornelsen.nep.util.CryptoUtils;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
public class PublisherAdminResource {

	private final PublisherService publisherService;

	@GetMapping("/api/admin/publisher")
	@Operation(summary = "The REST get request to get publishers list API call.", description = "Get all publishers", tags = {"PublisherApi"})
	public ResponseEntity<List<PublisherRsp>> getPublishers() {
		log.info("[REST][ADMIN] getPublishers");
		return ResponseEntity.ok(publisherService.getPublishers());
	}

	@GetMapping("/api/admin/publisher/{id}")
	@Operation(summary = "The REST get request to get publisher API call.", description = "Get publisher by id", tags = {"PublisherApi"})
	public ResponseEntity<PublisherRsp> getPublisher(@PathVariable String id) {
		log.info("[REST][ADMIN] getPublisher, id: {}", id);
		Long decryptedId = CryptoUtils.decrypt(id);
		return ResponseEntity.ok(publisherService.getPublisher(decryptedId));
	}

	@PostMapping("/api/admin/publisher")
	@CacheEvict(cacheNames = CaffeineConfiguration.CACHE_FILTER_NAME, allEntries = true)
	@Operation(summary = "The REST post request to add new publisher API call.", description = "Add new publisher", tags = {"PublisherApi"})
	public ResponseEntity<Long> addPublisher(@Valid @RequestBody PublisherCreateReq publisherReq) {
		log.info("[REST][ADMIN] addPublisher, publisherReq: {}", publisherReq);
		return ResponseEntity.ok(publisherService.addPublisher(publisherReq));
	}

	@PutMapping("/api/admin/publisher")
	@CacheEvict(cacheNames = CaffeineConfiguration.CACHE_FILTER_NAME, allEntries = true)
	@Operation(summary = "The REST put request to update publisher API call.", description = "Update publisher", tags = {"PublisherApi"})
	public ResponseEntity<PublisherRsp> updatePublisher(@Valid @RequestBody PublisherUpdateReq publisherReq) {
		log.info("[REST][ADMIN] updatePublisher, publisherReq: {}", publisherReq);
		return ResponseEntity.ok(publisherService.updatePublisher(publisherReq));
	}

	@DeleteMapping("/api/admin/publisher/{id}")
	@CacheEvict(cacheNames = CaffeineConfiguration.CACHE_FILTER_NAME, allEntries = true)
	@Operation(summary = "The REST delete request to remove publisher API call.", description = "Remove publisher", tags = {"PublisherApi"})
	public ResponseEntity<Void> deletePublisher(@PathVariable String id) {
		log.info("[REST][ADMIN] deletePublisher, id: {}", id);
		publisherService.deletePublisher(id);
		return ResponseEntity.ok().build();
	}

	@PostMapping("/api/admin/publisher/{id}/accept")
	@CacheEvict(cacheNames = CaffeineConfiguration.CACHE_FILTER_NAME, allEntries = true)
	@Operation(summary = "The REST post request to accept pending publisher API call.", description = "Accept pending publisher", tags = {"PublisherApi"})
	public ResponseEntity<PublisherRsp> acceptPublisher(@PathVariable String id) {
		log.info("[REST][ADMIN] acceptPublisher, id: {}", id);
		return ResponseEntity.ok(publisherService.acceptPublisher(id));
	}
}
