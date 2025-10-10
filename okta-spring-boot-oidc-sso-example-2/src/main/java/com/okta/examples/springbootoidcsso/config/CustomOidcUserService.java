package com.okta.examples.springbootoidcsso.config;

import java.util.HashMap;
import java.util.Map;

import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
public class CustomOidcUserService extends OidcUserService {
    @Override
    public OidcUser loadUser(OidcUserRequest userRequest) {
        OidcUser oidcUser = super.loadUser(userRequest);
        Map<String, Object> claims = new HashMap<>(oidcUser.getClaims());
        // Store the access token as a claim
        claims.put("access_token", userRequest.getAccessToken().getTokenValue());
        return new DefaultOidcUser(oidcUser.getAuthorities(), userRequest.getIdToken()) {
            @Override
            public Map<String, Object> getClaims() {
                return claims;
            }
        };
    }
}
