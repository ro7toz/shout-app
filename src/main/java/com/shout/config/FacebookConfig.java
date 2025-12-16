package com.shout.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class FacebookConfig {

    @Value("${facebook.app-id:}")
    private String appId;

    @Value("${facebook.app-secret:}")
    private String appSecret;

    @Value("${facebook.api-version:v19.0}")
    private String apiVersion;

    public String getAppId() {
        return appId;
    }

    public String getAppSecret() {
        return appSecret;
    }

    public String getApiVersion() {
        return apiVersion;
    }
}
