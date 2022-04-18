package com.graffitter.users.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Component;

@RefreshScope
@ConfigurationProperties("user-service")
@Component
public class Configuration {
    private String tata;
    private String x;
    private String y;

    public String getTata() {
        return tata;
    }

    public String getX() {
        return x;
    }

    public String getY() {
        return y;
    }

    public void setTata(String tata) {
        this.tata = tata;
    }

    public void setX(String x) {
        this.x = x;
    }

    public void setY(String y) {
        this.y = y;
    }
}
