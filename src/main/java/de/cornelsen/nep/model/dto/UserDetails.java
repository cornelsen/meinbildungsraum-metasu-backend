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

package de.cornelsen.nep.model.dto;

import de.cornelsen.nep.configuration.SecurityConfiguration;
import de.cornelsen.nep.model.entity.enums.RelationshipStatus;
import de.cornelsen.nep.util.SecurityUtils;
import lombok.Data;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;

import java.util.List;
import java.util.Objects;

@Data
@Slf4j
@ToString(of = {"sub", "enmeshedAddress", "firstLogin", "hasDw", "hasDwRelation", "dataWalletStatus"})
public class UserDetails {
	private String sub;
	private String name;
	private String firstName;
	private String lastName;
	private String email;
	private boolean isAdmin;
	private List<String> roles;
	private String enmeshedAddress;

	private Boolean firstLogin;
	private Boolean hasDw;
	private Boolean hasDwRelation;

	private RelationshipStatus dataWalletStatus = RelationshipStatus.UNAVAILABLE;

	public UserDetails(DefaultOidcUser defaultOidcUser) {
		this.setSub(defaultOidcUser.getSubject());
		this.setName(defaultOidcUser.getClaimAsString("preferred_username"));
		this.setFirstName(defaultOidcUser.getClaimAsString("given_name"));
		this.setLastName(defaultOidcUser.getClaimAsString("family_name"));
		this.setEmail(defaultOidcUser.getClaimAsString("email"));
		log.info("User token: {}", defaultOidcUser.getIdToken().getTokenValue());
		List<SimpleGrantedAuthority> grantedAuthorities = SecurityUtils.extractRoles(defaultOidcUser.getAttributes());
		this.setAdmin(grantedAuthorities.stream().map(Objects::toString).anyMatch(a -> a.equals(SecurityUtils.ROLE_PREFIX + SecurityConfiguration.ROLE_ADMIN)));
		this.setRoles(grantedAuthorities.stream().map(Objects::toString).toList());
		this.setEnmeshedAddress((String) defaultOidcUser.getIdToken().getClaims().getOrDefault("enmeshedAddress", null));
	}

	public UserDetails(User user) {
		this.setSub(user.getUsername());
		this.setName(user.getUsername());
		this.setAdmin(user.getAuthorities().stream().map(Objects::toString).anyMatch(a -> a.equals(SecurityUtils.ROLE_PREFIX + SecurityConfiguration.ROLE_ADMIN)));
		this.setRoles(user.getAuthorities().stream().map(GrantedAuthority::getAuthority).toList());
		this.setFirstName(user.getUsername());
	}

}
