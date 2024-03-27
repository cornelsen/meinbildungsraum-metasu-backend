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

package de.cornelsen.nep.configuration;

import de.cornelsen.nep.model.dto.UserDetails;
import de.cornelsen.nep.model.entity.enums.RelationshipStatus;
import de.cornelsen.nep.service.UserService;
import de.cornelsen.nep.util.SecurityUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.authority.mapping.GrantedAuthoritiesMapper;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.oidc.user.OidcUserAuthority;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Slf4j
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfiguration {

	public static final String ROLE_ADMIN = "ADMIN";

	private final UserService userService;
	private final KeycloakConfig keycloakConfig;
	private final ApplicationConfiguration applicationConfiguration;

	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {

		//Admin panel authorize by in-memory user
		httpSecurity
			.authorizeHttpRequests(auth ->
				auth.requestMatchers("/api/user-info").permitAll()
					.requestMatchers("/api/admin/**").hasRole(ROLE_ADMIN)

					.requestMatchers(HttpMethod.POST, "/api/news").hasRole(ROLE_ADMIN)
					.requestMatchers(HttpMethod.PUT, "/api/news").hasRole(ROLE_ADMIN)
					.requestMatchers(HttpMethod.DELETE, "/api/news").hasRole(ROLE_ADMIN)

					.requestMatchers(HttpMethod.POST, "/api/articles").hasRole(ROLE_ADMIN)
					.requestMatchers(HttpMethod.PUT, "/api/articles").hasRole(ROLE_ADMIN)
					.requestMatchers(HttpMethod.DELETE, "/api/articles").hasRole(ROLE_ADMIN)
			)
			.csrf(AbstractHttpConfigurer::disable)
			.formLogin(formLogin -> formLogin
				.loginPage("/api/admin/login")
				.loginProcessingUrl("/api/admin/login")
				.successHandler((request, response, authentication) -> response.setStatus(HttpStatus.OK.value()))
				.failureHandler((request, response, authentication) -> response.setStatus(HttpStatus.FORBIDDEN.value()))
			)
			.exceptionHandling(config -> config.authenticationEntryPoint((request, response, authException) -> response.setStatus(HttpServletResponse.SC_UNAUTHORIZED)));

		//External IDP (Enmeshed)
		httpSecurity.authorizeHttpRequests(auth -> {
				auth.requestMatchers("/api/user-info").permitAll()
					.requestMatchers("/api/attachments/*").permitAll()
					.requestMatchers("/api/filters").permitAll()
					.requestMatchers("/api/publisher").permitAll()
					.requestMatchers("/actuator/health").permitAll()
					.requestMatchers("/actuator/prometheus").permitAll()
					.requestMatchers("/actuator/info").permitAll()
					.requestMatchers("/swagger-resources/**").permitAll()
					.requestMatchers("/swagger-ui.html").permitAll()
					.requestMatchers("/swagger-ui/**").permitAll()
					.requestMatchers("/v3/api-docs/**").permitAll()
					.requestMatchers("/logout").permitAll()
					.requestMatchers("/webjars/**").permitAll()
					.requestMatchers("/callback/**").permitAll()

					.requestMatchers(HttpMethod.GET, "/api/news/**").permitAll()
					.requestMatchers(HttpMethod.GET, "/api/articles/**").permitAll()
					.requestMatchers(HttpMethod.GET, "/api/ratings/*/*").permitAll();
				if (applicationConfiguration.isJustLoggedUsers()) {
					auth.requestMatchers("/api/search/**").authenticated();
				} else {
					auth.requestMatchers("/api/search/**").permitAll();
				}
				auth.anyRequest().authenticated();
			})
			.oauth2Login(r -> r.successHandler(this::handleSuccessLogin))
			.csrf(AbstractHttpConfigurer::disable)
			.exceptionHandling(config ->
				config.authenticationEntryPoint((request, response, authException) -> response.setStatus(HttpServletResponse.SC_UNAUTHORIZED)))
			.logout(logout -> logout.logoutUrl("/api/logout")
				.invalidateHttpSession(true)
				.deleteCookies("JSESSIONID")
				.logoutSuccessUrl(keycloakConfig.getLogoutSuccessUrl()));

		return httpSecurity.build();
	}

	@Bean
	public GrantedAuthoritiesMapper userAuthoritiesMapper() {
		return authorities -> {
			Set<GrantedAuthority> mappedAuthorities = new HashSet<>();
			authorities.forEach(authority -> {
				if (authority instanceof OidcUserAuthority oidcUserAuthority) {
					List<SimpleGrantedAuthority> grantedAuthorities = SecurityUtils.extractRoles(oidcUserAuthority.getAttributes());
					mappedAuthorities.addAll(grantedAuthorities);
				}
			});
			return mappedAuthorities;
		};
	}

	/**
	 * Admin panel logging possibility by predefined user
	 */
	@Bean
	public UserDetailsService userDetailsService() {
		var inMemoryUserManager = new InMemoryUserDetailsManager();
		applicationConfiguration.getAdminUsers().forEach(configUser -> inMemoryUserManager.createUser(User
			.withUsername(configUser.getUserName())
			.password(encoder().encode(configUser.getPassword()))
			.roles(SecurityConfiguration.ROLE_ADMIN).build()));
		return inMemoryUserManager;
	}

	@Bean
	public PasswordEncoder encoder() {
		return new BCryptPasswordEncoder();
	}

	private void handleSuccessLogin(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {
		response.sendRedirect(applicationConfiguration.getFrontendUrl());
		UserDetails currentUserDetails = SecurityUtils.currentUser().orElseThrow();

		Optional<de.cornelsen.nep.model.entity.User> dbUserOptional = userService.findByOidcId(currentUserDetails.getSub());

		if (dbUserOptional.isEmpty()) {
			//user logs in for the first time
			de.cornelsen.nep.model.entity.User user = new de.cornelsen.nep.model.entity.User(currentUserDetails.getSub());
			if (currentUserDetails.getEnmeshedAddress() != null) {
				user.setEnmeshedAddress(currentUserDetails.getEnmeshedAddress());
				user.setLastLoginAtHavingDw(LocalDateTime.now());
			}
			userService.save(user);
			log.info("User logged in for the first time, created one: {}", user);
			return;
		}

		de.cornelsen.nep.model.entity.User dbUser = dbUserOptional.get();
		dbUser.setLastLoginAt(LocalDateTime.now());

		if (currentUserDetails.getEnmeshedAddress() != null && dbUser.getEnmeshedAddress() == null) {
			//if user just setup enmeshed dw, then treat it as a first login
			dbUser.setLastLoginAt(null);
			dbUser.setLastLoginAtHavingDw(LocalDateTime.now());
			dbUser.setEnmeshedAddress(currentUserDetails.getEnmeshedAddress());
			log.info("User already exists, although created an account in DW, hence treat it as a first login attempt: {}", dbUser);
		}
		if (dbUser.getStatus().equals(RelationshipStatus.ACTIVE)) {
			dbUser.setLastLoginAtHavingDwRelation(LocalDateTime.now());
		}

		userService.save(dbUser);
	}
}