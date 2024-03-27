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


import de.cornelsen.nep.model.entity.enums.RelationshipStatus;
import de.cornelsen.nep.nmshd.model.callback.OutgoingRequestCreatedAndCompleted;
import de.cornelsen.nep.nmshd.service.DataWalletService;
import de.cornelsen.nep.util.SecurityUtils;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;


@Slf4j
@RestController
@RequiredArgsConstructor
@ConditionalOnProperty(prefix = "application.nmshd", name = "enabled")
public class DataWalletResource {

	private final DataWalletService dataWalletService;

	@GetMapping("/api/code")
	@Operation(summary = "The REST request to get QR Code", tags = {"DataWalletResource"})
	public ResponseEntity<byte[]> getCode() {
		String currentUserId = SecurityUtils.currentUser().orElseThrow().getSub();
		log.info("[REST] getCode, currentUserId: {}", currentUserId);

		byte[] qrCode = dataWalletService.getCode(currentUserId);

		return ResponseEntity.ok()
			.header(HttpHeaders.CONTENT_DISPOSITION, "filename=\"" + UUID.randomUUID().toString().concat(".png") + "\"")
			.contentType(MediaType.IMAGE_PNG)
			.contentLength(qrCode.length)
			.body(qrCode);
	}

	@GetMapping("/api/relationship/status")
	@Operation(summary = "The REST request to get user relationship status", tags = {"DataWalletResource"})
	public ResponseEntity<RelationshipStatus> getRelationshipStatus() {
		String currentUserId = SecurityUtils.currentUser().orElseThrow().getSub();
		log.info("[REST] getRelationshipStatus, currentUserId: {}", currentUserId);
		RelationshipStatus userStatus = dataWalletService.getUserStatus(currentUserId);
		return ResponseEntity.ok(userStatus);
	}

	@PostMapping("/api/relationship")
	@Operation(summary = "The REST request to create user relationship", tags = {"DataWalletResource"})
	public ResponseEntity<String> createRelationship() {
		String currentUserId = SecurityUtils.currentUser().orElseThrow().getSub();
		log.info("[REST] createRelationship, currentUserId: {}", currentUserId);

		String relationshipTemplateId = dataWalletService.createRelationship(currentUserId);
		return ResponseEntity.ok(relationshipTemplateId);
	}

	@PostMapping("/callback/enmeshed/outgoing-request-created-and-completed")
	@Operation(summary = "Internal request to handle connector callback", tags = {"DataWalletResource"}, hidden = true)
	public ResponseEntity<Void> outgoingRequestCreatedAndCompleted(@RequestBody OutgoingRequestCreatedAndCompleted request) {
		log.info("[CALLBACK] outgoingRequestCreatedAndCompleted, data: {}", request);

		dataWalletService.outgoingRequestCreatedAndCompletedCallback(request);
		return ResponseEntity.ok().build();
	}
}
