package de.cornelsen.nep.nmshd.api;

import de.cornelsen.nep.nmshd.model.Request;
import de.cornelsen.nep.nmshd.model.Response;
import de.cornelsen.nep.nmshd.model.relationships.RelationshipRsp;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@FeignClient(name = "relationshipsClient", url = "${application.nmshd.connector-api-path}")
public interface RelationshipsClient {

	@GetMapping("/Relationships")
	Response<List<RelationshipRsp>> getRelationships(@RequestParam("template.id") String relationshipTemplateId);

	@PutMapping(value = "/Relationships/{id}/Changes/{changeId}/Accept")
	Response<Object> accept(@PathVariable String id, @PathVariable String changeId, @RequestBody Request<?> empty);

}
