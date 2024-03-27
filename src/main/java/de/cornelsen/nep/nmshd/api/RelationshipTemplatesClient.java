package de.cornelsen.nep.nmshd.api;

import de.cornelsen.nep.nmshd.model.Response;
import de.cornelsen.nep.nmshd.model.relationshiptemplates.CreateOwnRelationshipTemplateReq;
import de.cornelsen.nep.nmshd.model.relationshiptemplates.CreateOwnRelationshipTemplateRsp;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@FeignClient(name = "relationshipTemplatesClient", url = "${application.nmshd.connector-api-path}")
public interface RelationshipTemplatesClient {

    @PostMapping(value = "/RelationshipTemplates/Own")
	Response<CreateOwnRelationshipTemplateRsp> createOwnRelationshipTemplate(@RequestBody CreateOwnRelationshipTemplateReq request);

	@GetMapping(value = "/RelationshipTemplates/{id}", produces = "image/png")
	byte[] getQrCode(@PathVariable String id);

}
