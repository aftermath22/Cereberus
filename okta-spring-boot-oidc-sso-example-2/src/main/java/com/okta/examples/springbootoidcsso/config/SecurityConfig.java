package com.okta.examples.springbootoidcsso.config;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private final OAuth2AuthorizedClientService clientService;

    public SecurityConfig(OAuth2AuthorizedClientService clientService) {
        this.clientService = clientService;
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .authorizeRequests()
                .antMatchers("/", "/css/**", "/js/**").permitAll()
                .anyRequest().authenticated()
                .and()
                .oauth2Login()
                .and()
                .logout()
                .logoutSuccessHandler(customOidcLogoutHandler());
    }

    private LogoutSuccessHandler customOidcLogoutHandler() {
        return (HttpServletRequest request, HttpServletResponse response, Authentication authentication) -> {

            String idToken = null;
            String registrationId = null;

            if (authentication instanceof OAuth2AuthenticationToken) {
                OAuth2AuthenticationToken oauthToken = (OAuth2AuthenticationToken) authentication;
                registrationId = oauthToken.getAuthorizedClientRegistrationId();

                if (oauthToken.getPrincipal() instanceof DefaultOidcUser) {
                    idToken = ((DefaultOidcUser) oauthToken.getPrincipal()).getIdToken().getTokenValue();
                }
            }

            // Build logout based on the AS issuer for your OIDC App
            String issuer = "https://integrator-1697993.okta.com/oauth2/ausuiwcrc1sjc2osL697";
            String logoutUrl = issuer + "/v1/logout" +
                    "?id_token_hint=" + URLEncoder.encode(idToken, StandardCharsets.UTF_8.name()) +
                    "&post_logout_redirect_uri=" + URLEncoder.encode("http://localhost:8081/", StandardCharsets.UTF_8.name());

            // Optionally revoke tokens here via /revoke endpoint

            response.sendRedirect(logoutUrl);
        };
    }
}

