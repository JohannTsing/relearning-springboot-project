package com.johann.binarytea;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * <p>
 *
 * @author Johann
 * @version 1.0
 * @see
 **/
@ConfigurationProperties("binarytea")
public class BinaryTeaProperties {
    private boolean ready;
    private String openHours;

    public boolean isReady() {
        return ready;
    }

    public void setReady(boolean ready) {
        this.ready = ready;
    }

    public String getOpenHours() {
        return openHours;
    }

    public void setOpenHours(String openHours) {
        this.openHours = openHours;
    }
}
