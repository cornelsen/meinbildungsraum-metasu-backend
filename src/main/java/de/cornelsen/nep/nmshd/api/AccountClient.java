package de.cornelsen.nep.nmshd.api;

import de.cornelsen.nep.nmshd.model.Response;
import de.cornelsen.nep.nmshd.model.account.IdentityInfoRsp;
import de.cornelsen.nep.nmshd.model.account.SyncInfoRsp;
import de.cornelsen.nep.nmshd.model.account.SyncRsp;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;


@FeignClient(name = "accountClient", url = "${application.nmshd.connector-api-path}")
public interface AccountClient {

	@GetMapping(value = "/Account/IdentityInfo")
	Response<IdentityInfoRsp> getIdentityInfo();

	@GetMapping(value = "/Account/SyncInfo")
	Response<SyncInfoRsp> getAccountSyncInfo();

	@PostMapping(value = "/Account/Sync")
	Response<SyncRsp> sync();
}
