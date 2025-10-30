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
import org.springframework.web.filter.ForwardedHeaderFilter;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

    @Value("${okta.oauth2.issuer}")
    private String issuer;

    @Value("${learnhub.base-url}")
    private String appBaseUrl;

    @Configuration
    @Order(1)
    public static class ApiSecurityConfig extends WebSecurityConfigurerAdapter {
        @Override
        protected void configure(HttpSecurity http) throws Exception {
            http
                    .antMatcher("/api/**")
                    .authorizeRequests()
                    .anyRequest().authenticated()
                    .and()
                    .oauth2ResourceServer().jwt();
        }
    }

    @Configuration
    public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

        private LogoutSuccessHandler oidcLogoutSuccessHandler() {
            return new OidcLogoutSuccessHandler(issuer, appBaseUrl);
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
                            groups.forEach(group -> mappedAuthorities.add(new SimpleGrantedAuthority(group)));
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
                    .oauth2Login()
                    .and()
                    .logout()
                    .invalidateHttpSession(true)
                    .clearAuthentication(true)
                    .deleteCookies("JSESSIONID")
                    .logoutSuccessHandler(oidcLogoutSuccessHandler());
        }
    }

    private static class OidcLogoutSuccessHandler implements LogoutSuccessHandler {
        private final String issuer;
        private final String postLogoutRedirectUri;

        public OidcLogoutSuccessHandler(String issuer, String postLogoutRedirectUri) {
            this.issuer = issuer;
            this.postLogoutRedirectUri = postLogoutRedirectUri;
        }

        @Override
        public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response,
                Authentication authentication) throws IOException, ServletException {
            String idToken = null;
            if (authentication instanceof OAuth2AuthenticationToken) {
                OAuth2AuthenticationToken oauthToken = (OAuth2AuthenticationToken) authentication;
                if (oauthToken.getPrincipal() instanceof DefaultOidcUser) {
                    idToken = ((DefaultOidcUser) oauthToken.getPrincipal()).getIdToken().getTokenValue();
                }
            }

            // Clear the session
            request.getSession().invalidate();

            String logoutUrl = issuer + "/v1/logout?id_token_hint=" +
                    URLEncoder.encode(idToken, StandardCharsets.UTF_8.name()) +
                    "&post_logout_redirect_uri=" +
                    URLEncoder.encode(postLogoutRedirectUri, StandardCharsets.UTF_8.name());

            response.sendRedirect(logoutUrl);
        }
    }

    @Bean
    public JwtDecoder jwtDecoder() {
        return JwtDecoders.fromOidcIssuerLocation(issuer);
    }

    @Bean
    ForwardedHeaderFilter forwardedHeaderFilter() {
        return new ForwardedHeaderFilter();
    }
}
