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

package de.cornelsen.nep.nmshd.service;

import de.cornelsen.nep.model.dto.UserDetails;
import de.cornelsen.nep.model.entity.User;
import de.cornelsen.nep.model.entity.enums.RelationshipStatus;
import de.cornelsen.nep.nmshd.api.AccountClient;
import de.cornelsen.nep.nmshd.api.AttributesClient;
import de.cornelsen.nep.nmshd.api.RelationshipTemplatesClient;
import de.cornelsen.nep.nmshd.api.RelationshipsClient;
import de.cornelsen.nep.nmshd.model.Request;
import de.cornelsen.nep.nmshd.model.Response;
import de.cornelsen.nep.nmshd.model.account.IdentityInfoRsp;
import de.cornelsen.nep.nmshd.model.account.SyncRsp;
import de.cornelsen.nep.nmshd.model.attribute.CreateAttributeReq;
import de.cornelsen.nep.nmshd.model.attribute.CreateAttributeRsp;
import de.cornelsen.nep.nmshd.model.attribute.CreateQueryReq;
import de.cornelsen.nep.nmshd.model.callback.OutgoingRequestCreatedAndCompleted;
import de.cornelsen.nep.nmshd.model.relationships.RelationshipRsp;
import de.cornelsen.nep.nmshd.model.relationshiptemplates.CreateOwnRelationshipTemplateReq;
import de.cornelsen.nep.nmshd.model.relationshiptemplates.CreateOwnRelationshipTemplateRsp;
import de.cornelsen.nep.repository.UserRepository;
import io.micrometer.common.util.StringUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static de.cornelsen.nep.nmshd.model.attribute.CreateQueryReq.AttributeType.IDENTITY;

@Slf4j
@Service
@RequiredArgsConstructor
@ConditionalOnProperty(prefix = "application.nmshd", name = "enabled")
public class DataWalletService {
	private final AccountClient accountClient;
	private final UserRepository userRepository;
	private final AttributesClient attributesClient;
	private final RelationshipsClient relationshipsClient;
	private final RelationshipTemplatesClient relationshipTemplatesClient;

	@Transactional
	public byte[] getCode(String currentUserId) {

		String relationshipTemplateId = setupRelationship(currentUserId);
		log.info("Generating qr code for relationshipTemplateId: {}", relationshipTemplateId);
		return relationshipTemplatesClient.getQrCode(relationshipTemplateId);
	}

	@Transactional(readOnly = true)
	public RelationshipStatus getUserStatus(String currentUserId) {
		Optional<User> userOptional = userRepository.findByOidcId(currentUserId);
		return userOptional.isPresent() ? userOptional.get().getStatus() : RelationshipStatus.UNAVAILABLE;
	}

	@Transactional
	public String createRelationship(String currentUserId) {
		return setupRelationship(currentUserId);
	}

	private String setupRelationship(String currentUserId) {

		//Verify if a current user already has a Relationship
		User user = userRepository.findByOidcId(currentUserId).orElseThrow();

		if (user.getRelationshipId() != null) {
			log.info("Relationship template already exists: {}", user);
			return user.getRelationshipId();
		}

		CreateOwnRelationshipTemplateReq createOwnRelationshipTemplate = CreateOwnRelationshipTemplateReq.builder()
			.maxNumberOfAllocations(1)
			.expiresAt(LocalDateTime.now().plusYears(100))
			.content(
				CreateOwnRelationshipTemplateReq.NewRelationship.builder()
					.onNewRelationship(
						CreateOwnRelationshipTemplateReq.NewRelationship.NewRelationshipItem.builder()
							.items(List.of(
								CreateOwnRelationshipTemplateReq.NewRelationship.RequestItemGroup.builder()
									.type("RequestItemGroup")
									.mustBeAccepted(false)
									.title("Read Personal Info Attributes")
									.items(List.of(
										new CreateOwnRelationshipTemplateReq.NewRelationship.RequestItemGroup.RequestItem(false,
											CreateQueryReq.builder().type(IDENTITY).valueType(CreateQueryReq.ValueType.GIVEN_NAME).build()
										),
										new CreateOwnRelationshipTemplateReq.NewRelationship.RequestItemGroup.RequestItem(false,
											CreateQueryReq.builder().type(IDENTITY).valueType(CreateQueryReq.ValueType.SURNAME).build()
										),
										new CreateOwnRelationshipTemplateReq.NewRelationship.RequestItemGroup.RequestItem(false,
											CreateQueryReq.builder().type(IDENTITY).valueType(CreateQueryReq.ValueType.EMAIL).build()
										)
									))
									.build()
							))
							.build()
					)
					.build()
			).build();

		//RelationshipTemplate has a status Pending
		Response<CreateOwnRelationshipTemplateRsp> relationshipTemplateRsp = relationshipTemplatesClient.createOwnRelationshipTemplate(createOwnRelationshipTemplate);
		log.info("Created new relationship, user: {}, template: {}", currentUserId, relationshipTemplateRsp);
		String relationshipTemplateId = relationshipTemplateRsp.getResult().getId();

		user.setStatus(RelationshipStatus.PENDING);
		user.setRelationshipId(relationshipTemplateId);

		return relationshipTemplateId;
	}

	@Transactional
	public void storeData(Object data) {
		//Request IdentityInfo for an Address
		Response<IdentityInfoRsp> identityInfo = accountClient.getIdentityInfo();
		log.info("Identity info: {}", identityInfo);

		//Create Attribute with the given Address from 1. and a custom Value (type/value)
		CreateAttributeReq createAttribute = CreateAttributeReq.builder()
			.type(CreateAttributeReq.AttributeType.IDENTITY)
			.owner(identityInfo.getResult().getAddress())
			.value(CreateAttributeReq.AttributeValue.builder()
				.type(CreateAttributeReq.AttributeValue.AttributeValueType.DISPLAY_NAME)
				.value("Attribute ".concat(UUID.randomUUID().toString()))
				.build())
			.build();

		Request<CreateAttributeReq> createAttributeRequest = Request.<CreateAttributeReq>builder().content(createAttribute).build();

		Response<CreateAttributeRsp> rsp = attributesClient.createAttributes(createAttributeRequest);
		log.info("Attribute created: {}", rsp);

		//Bind attribute to a relationship (yet to be implemented by Enmeshed)

		//Synchronize data between the connector and the backbone
		Response<SyncRsp> syncRsp = accountClient.sync();

		//Accept the synced change (should be done by callbacks)
		syncRsp.getResult().getRelationships().forEach(relationship ->
			relationship.getChanges().forEach(change ->
				relationshipsClient.accept(relationship.getId(), change.getId(), Request.builder().build())
			)
		);
	}

	@Transactional
	public void outgoingRequestCreatedAndCompletedCallback(OutgoingRequestCreatedAndCompleted request) {
		String relationshipTemplateId = request.getData().getSource().getReference();
		log.info("[CALLBACK] Connector callback: {}, templateId: {}", request.getTrigger(), relationshipTemplateId);

		userRepository.findByRelationshipId(relationshipTemplateId).ifPresent(user -> {
			log.info("[CALLBACK] Activating user: {}.", user);
			user.setStatus(RelationshipStatus.ACTIVE);
		});

		//call relationship api to get remaining relationship changes
		List<RelationshipRsp> relationshipRsp = relationshipsClient.getRelationships(relationshipTemplateId).getResult();

		//request body required by api
		Request<?> emptyReq = Request.builder().content(Map.of("prop1", "value")).build();

		//accept changes
		relationshipRsp.forEach(relationship -> relationship.getChanges().forEach(change -> relationshipsClient.accept(relationship.getId(), change.getId(), emptyReq)));

		//Sync changes needed
		Response<SyncRsp> syncRsp = accountClient.sync();
		syncRsp.getResult().getRelationships().forEach(relationship ->
			relationship.getChanges().forEach(change ->
				relationshipsClient.accept(relationship.getId(), change.getId(), emptyReq)
			)
		);
	}

	public void addPersonalData(UserDetails userDetails) {

		String enmeshedAddress = userDetails.getEnmeshedAddress();
		log.info("About to retrieve owner attributes by enmeshedAddress: {}", enmeshedAddress);
		if (StringUtils.isEmpty(enmeshedAddress)) {
			log.info("User has no DW relation yet, cant retrieve personal data: {}", userDetails);
			return;
		}

		Response<List<CreateAttributeRsp>> ownerAttributes = attributesClient.getOwnerAttributes(enmeshedAddress);
		List<CreateAttributeReq.AttributeValue> userAttributes = ownerAttributes.getResult().stream().map(attribute -> attribute.getContent().getValue()).toList();
		log.info("Retrieved user attributes by enmeshedAddress: {} from DW: {}", enmeshedAddress, userAttributes);

		userAttributes.forEach(att -> {
			switch (att.getType()) {
				case GIVEN_NAME -> userDetails.setFirstName(att.getValue());
				case SURNAME -> userDetails.setLastName(att.getValue());
				case EMAIL -> userDetails.setEmail(att.getValue());
				case PERSON_NAME -> userDetails.setName(att.getValue());
				default -> log.debug("Unknown attribute type: {}", att.getType());
			}
		});
		log.debug("Setup additional userDetails: {}", userDetails);
	}

}
