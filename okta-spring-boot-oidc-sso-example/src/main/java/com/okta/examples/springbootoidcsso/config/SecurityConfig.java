package com.okta.examples.springbootoidcsso.config;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.authority.mapping.GrantedAuthoritiesMapper;
import org.springframework.security.oauth2.core.oidc.OidcIdToken;
import org.springframework.security.oauth2.core.oidc.user.OidcUserAuthority;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtDecoders;
import org.springframework.security.oauth2.core.oidc.OidcIdToken;
import org.springframework.security.oauth2.core.oidc.user.OidcUserAuthority;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtDecoders;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

    @Value("${okta.oauth2.issuer}")
    private String issuer;

    @Bean
    public JwtDecoder jwtDecoder() {
        return JwtDecoders.fromOidcIssuerLocation(issuer);
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
    public static class WebSecurityConfig extends WebSecurityConfigurerAdapter {
        @Override
        protected void configure(HttpSecurity http) throws Exception {
            http
                .authorizeRequests()
                    .antMatchers("/", "/css/**", "/js/**").permitAll()
                    .anyRequest().authenticated()
                .and()
                .oauth2Login() // Use the standard login flow
                .and()
                .logout();
        }
    }
}