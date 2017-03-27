package com.portol.common.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.portol.common.model.user.User;

import java.io.Serializable;

@JsonIgnoreProperties(ignoreUnknown = true)
public class LoginRequest implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = -421051001256623462L;


    private User loggingIn;
    private PortolPlatform loginPlatform;

    public LoginRequest() {
        super();
    }

    public LoginRequest(User loggingIn, PortolPlatform loginPlatform) {
        super();
        this.loggingIn = loggingIn;
        this.loginPlatform = loginPlatform;
    }

    public User getLoggingIn() {
        return loggingIn;
    }

    public void setLoggingIn(User loggingIn) {
        this.loggingIn = loggingIn;
    }

    public PortolPlatform getLoginPlatform() {
        return loginPlatform;
    }

    public void setLoginPlatform(PortolPlatform loginPlatform) {
        this.loginPlatform = loginPlatform;
    }

}
