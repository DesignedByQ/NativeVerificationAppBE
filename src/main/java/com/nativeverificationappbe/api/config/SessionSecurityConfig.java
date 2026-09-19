package com.nativeverificationappbe.api.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.session.web.http.*;
import org.springframework.beans.factory.annotation.Value;

@Configuration
public class SessionSecurityConfig {

    @Value("${server.servlet.session.cookie.secure:false}")
    private boolean useSecureCookie;

    @Bean
    public CookieSerializer cookieSerializer() {
        DefaultCookieSerializer serializer = new DefaultCookieSerializer();
        serializer.setCookieName("SESSIONID");
        serializer.setUseHttpOnlyCookie(true);
        serializer.setUseSecureCookie(useSecureCookie); // Toggles dynamically between dev and prod
        serializer.setSameSite("Strict");
        return serializer;
    }
}
