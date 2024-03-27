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

package de.cornelsen.nep.util;

import de.cornelsen.nep.model.dto.UserDetails;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Slf4j
public class SecurityUtils {

	public static final String ROLE_PREFIX = "ROLE_";

	private SecurityUtils() {
	}

	public static Optional<UserDetails> currentUser() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if (authentication instanceof AnonymousAuthenticationToken) {
			return Optional.empty();
		}

		if (authentication.getPrincipal() instanceof User user) {
			return Optional.of(new UserDetails(user));
		}

		DefaultOidcUser oidcIdToken = (DefaultOidcUser) authentication.getPrincipal();
		return Optional.of(new UserDetails(oidcIdToken));
	}

	public static List<SimpleGrantedAuthority> extractRoles(Map<String, Object> defaultOidcUser) {
		if (!defaultOidcUser.containsKey("realm_access")) {
			return Collections.emptyList();
		}
		Map<String, Object> realmAccess = (Map<String, Object>) defaultOidcUser.get("realm_access");
		List<String> roles = (List<String>) realmAccess.get("roles");
		return roles.stream()
			.map(rn -> new SimpleGrantedAuthority(ROLE_PREFIX + rn))
			.toList();

	}
}
