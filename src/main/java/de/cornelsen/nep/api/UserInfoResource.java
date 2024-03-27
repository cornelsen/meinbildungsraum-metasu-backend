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

package de.cornelsen.nep.api;

import de.cornelsen.nep.model.dto.UserDetails;
import de.cornelsen.nep.model.entity.enums.RelationshipStatus;
import de.cornelsen.nep.nmshd.service.DataWalletService;
import de.cornelsen.nep.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
public class UserInfoResource {

	@Value("${application.nmshd.enabled}")
	private boolean nmshdEnabled;

	private final UserService userService;

	private final DataWalletService dataWalletService;

	public UserInfoResource(UserService userService, @Autowired(required = false) DataWalletService dataWalletService) {
		this.userService = userService;
		this.dataWalletService = dataWalletService;
	}

	@GetMapping("/api/user-info")
	@Operation(summary = "The REST request to get current logged user.", tags = {"UserInfo"})
	public ResponseEntity<UserDetails> getUserInfo(Authentication authentication) {
		log.info("[REST] getUserInfo: {}", authentication);

		if (authentication == null || !authentication.isAuthenticated()) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
		}

		if (authentication.getPrincipal() instanceof User user) {
			return ResponseEntity.ok(new UserDetails(user));
		}

		DefaultOidcUser oidcIdToken = (DefaultOidcUser) authentication.getPrincipal();
		UserDetails userDetails = new UserDetails(oidcIdToken);

		userService.findByOidcId(oidcIdToken.getSubject()).ifPresent(user -> {
			userDetails.setDataWalletStatus(user.getStatus());
			userDetails.setFirstLogin(user.getLastLoginAt() == null);
			userDetails.setHasDw(user.getEnmeshedAddress() != null);
			userDetails.setHasDwRelation(user.getStatus().equals(RelationshipStatus.ACTIVE));
			if (nmshdEnabled) {
				dataWalletService.addPersonalData(userDetails);
			}
		});


		if (!nmshdEnabled) {
			log.info("Data Wallet disabled.");
			userDetails.setDataWalletStatus(RelationshipStatus.ACTIVE);

			return ResponseEntity.ok(userDetails);
		}
		return ResponseEntity.ok(userDetails);
	}
}
