package com.okta.examples.springbootoidcsso.config;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.authority.mapping.GrantedAuthoritiesMapper;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.oidc.OidcIdToken;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.security.oauth2.core.oidc.user.OidcUserAuthority;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtDecoders;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true) // Enable method-level security
public class SecurityConfig {

    @Value("${okta.oauth2.issuer}")
    private String issuer;

    // This configuration is for the API endpoints
    @Configuration
    @Order(1)
    public static class ApiSecurityConfig extends WebSecurityConfigurerAdapter {
        @Override
        protected void configure(HttpSecurity http) throws Exception {
            http
                .antMatcher("/api/**") // Apply this config only to /api/**
                .authorizeRequests()
                    .anyRequest().authenticated()
                .and()
                .oauth2ResourceServer().jwt(); // Use resource server with JWT
        }
    }

    // This configuration is for the user-facing web pages
    @Configuration
    public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

        private LogoutSuccessHandler oidcLogoutSuccessHandler() {
            return new OidcLogoutSuccessHandler();
        }

        @Bean
        public GrantedAuthoritiesMapper userAuthoritiesMapper() {
            return (authorities) -> {
                Set<GrantedAuthority> mappedAuthorities = new HashSet<>();

                authorities.forEach(authority -> {
                    if (authority instanceof OidcUserAuthority) {
                        OidcUserAuthority oidcUserAuthority = (OidcUserAuthority) authority;
                        OidcIdToken idToken = oidcUserAuthority.getIdToken();
                        List<String> groups = idToken.getClaimAsStringList("groups");

                        if (groups != null) {
                            groups.forEach(group ->
                                    mappedAuthorities.add(new SimpleGrantedAuthority(group))
                            );
                        }
                    }
                    mappedAuthorities.add(authority);
                });

                return mappedAuthorities;
            };
        }

        @Override
        protected void configure(HttpSecurity http) throws Exception {
            http
                .authorizeRequests()
                    .antMatchers("/", "/css/**", "/js/**").permitAll()
                    .anyRequest().authenticated()
                .and()
                    .oauth2Login() // Use the standard login flow
                .and()
                    .logout()
                    .logoutSuccessHandler(oidcLogoutSuccessHandler());
        }
    }

    // Inner class to handle Okta logout
    private class OidcLogoutSuccessHandler implements LogoutSuccessHandler {

        @Override
        public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
            String idToken = null;
            if (authentication instanceof OAuth2AuthenticationToken) {
                OAuth2AuthenticationToken oauthToken = (OAuth2AuthenticationToken) authentication;
                if (oauthToken.getPrincipal() instanceof DefaultOidcUser) {
                    idToken = ((DefaultOidcUser) oauthToken.getPrincipal()).getIdToken().getTokenValue();
                }
            }

            // Build the logout URL
            String logoutUrl = issuer + "/v1/logout?id_token_hint=" +
                URLEncoder.encode(idToken, StandardCharsets.UTF_8.name()) +
                "&post_logout_redirect_uri=" +
                URLEncoder.encode("http://localhost:8081/", StandardCharsets.UTF_8.name());

            response.sendRedirect(logoutUrl);
        }
    }

    @Bean
    public JwtDecoder jwtDecoder() {
        return JwtDecoders.fromOidcIssuerLocation(issuer);
    }
}