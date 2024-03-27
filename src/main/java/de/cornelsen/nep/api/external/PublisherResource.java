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

package de.cornelsen.nep.api.external;

import de.cornelsen.nep.model.dto.publisher.PublisherCreateReq;
import de.cornelsen.nep.model.dto.publisher.PublisherRsp;
import de.cornelsen.nep.model.dto.publisher.PublisherUpdateReq;
import de.cornelsen.nep.service.PublisherService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
public class PublisherResource {

	private final PublisherService publisherService;

	@PostMapping("/api/publisher")
	@Operation(summary = "The REST post request to add new publisher API call.", description = "Add new publisher", tags = {"PublisherApi"})
	public ResponseEntity<Long> addPublisher(@Valid @RequestBody PublisherCreateReq req) {
		log.info("[REST][EXTERNAL] addPublisher, PublisherCreateReq: {}", req);
		publisherService.addPendingPublisher(req);
		return ResponseEntity.ok().build();
	}

	@PutMapping("/api/publisher")
	@Operation(summary = "The REST put request to update publisher API call.", description = "Update publisher", tags = {"PublisherApi"})
	public ResponseEntity<PublisherRsp> updatePublisher(@Valid @RequestBody PublisherUpdateReq req) {
		log.info("[REST][EXTERNAL] updatePublisher, PublisherUpdateReq: {}", req);
		return ResponseEntity.ok(publisherService.updatePublisher(req));
	}
}
