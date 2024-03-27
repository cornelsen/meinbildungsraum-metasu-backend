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


import de.cornelsen.nep.model.dto.attachment.AttachmentRsp;
import de.cornelsen.nep.model.dto.attachment.UploadAttachmentRsp;
import de.cornelsen.nep.service.AttachmentService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;


@Slf4j
@RestController
@RequiredArgsConstructor
public class AttachmentResource {

	private final AttachmentService attachmentService;

	@GetMapping("/api/attachments/{id}")
	@Operation(summary = "The REST request to get attachment by id", tags = {"AttachmentResource"})
	public ResponseEntity<byte[]> getAttachment(@PathVariable String id) {
		log.info("[REST] getAttachment, id: {}", id);
		AttachmentRsp attachmentRsp = attachmentService.getAttachment(id);

		return ResponseEntity.ok()
			.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + attachmentRsp.getName() + "\"")
			.contentType(MediaType.valueOf(attachmentRsp.getType()))
			.contentLength(attachmentRsp.getSize())
			.body(attachmentRsp.getData());
	}

	@PostMapping("/api/attachments")
	@Operation(summary = "The REST request to upload attachment", tags = {"AttachmentResource"})
	public ResponseEntity<UploadAttachmentRsp> uploadAttachment(MultipartFile file) throws IOException {
		log.info("[REST] uploadAttachment");
		return ResponseEntity.ok(attachmentService.uploadAttachment(file));
	}

	@DeleteMapping("/api/attachments")
	@Operation(summary = "The REST request to delete attachment", tags = {"AttachmentResource"})
	public ResponseEntity<Void> deleteAttachment(@RequestParam String id) {
		log.info("[REST] deleteAttachment, id: {}", id);
		attachmentService.deleteAttachment(id);
		return ResponseEntity.ok().build();
	}

}
