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

package de.cornelsen.nep.service;

import de.cornelsen.nep.model.dto.attachment.AttachmentRsp;
import de.cornelsen.nep.model.dto.attachment.UploadAttachmentRsp;
import de.cornelsen.nep.model.entity.Attachment;
import de.cornelsen.nep.repository.AttachmentRepository;
import de.cornelsen.nep.util.CryptoUtils;
import de.cornelsen.nep.util.SecurityUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class AttachmentService {

	private final AttachmentRepository attachmentRepository;

	@Transactional(readOnly = true)
	public AttachmentRsp getAttachment(String id) {
		Long decryptedId = CryptoUtils.decrypt(id);
		log.info("Get attachment, id: {}", decryptedId);
		Attachment attachment = attachmentRepository.findById(decryptedId).orElseThrow();

		var attachmentRsp = new AttachmentRsp();
		attachmentRsp.setData(attachment.getData());
		attachmentRsp.setName(attachment.getName());
		attachmentRsp.setType(attachment.getType());
		attachmentRsp.setSize(attachment.getSize());
		return attachmentRsp;
	}

	@Transactional
	public UploadAttachmentRsp uploadAttachment(MultipartFile file) throws IOException {
		Attachment attachment = new Attachment();
		attachment.setType(file.getContentType());
		attachment.setName(file.getOriginalFilename());
		attachment.setData(file.getBytes());
		attachment.setSize(file.getSize());
		attachment.setCreatedAt(LocalDateTime.now());
		attachment.setCreatedBy(SecurityUtils.currentUser().orElseThrow().getSub());
		log.info("Upload attachment: {}", attachment);

		attachmentRepository.save(attachment);
		return new UploadAttachmentRsp(CryptoUtils.encrypt(attachment.getId()), attachment.getSize());
	}

	@Transactional
	public void deleteAttachment(String id) {
		Long decryptedId = CryptoUtils.decrypt(id);
		log.info("Delete attachment, id: {}", decryptedId);
		if (!attachmentRepository.existsById(decryptedId)) {
			throw new IllegalArgumentException("notFound");
		}
		attachmentRepository.deleteById(decryptedId);
	}

	@Transactional
	public void removeUnusedAttachment() {
		Set<Long> attachmentsToRemove = attachmentRepository.selectAttachmentsToRemove();
		log.info("Removing {} attachments", attachmentsToRemove.size());
		attachmentRepository.deleteAllById(attachmentsToRemove);
	}
}
