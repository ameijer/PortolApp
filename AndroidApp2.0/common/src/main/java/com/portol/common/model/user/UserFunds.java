package com.portol.common.model.user;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.io.Serializable;

import co.uk.rushorm.core.Rush;
import co.uk.rushorm.core.RushCallback;
import co.uk.rushorm.core.RushCore;

@JsonIgnoreProperties(ignoreUnknown = true)
public class UserFunds implements Serializable, Rush {
    /**
     *
     */
    private static final long serialVersionUID = -2371395757464454820L;
    private int userCredits;
    private int userBits;

    @Override
    public void save() {
        RushCore.getInstance().save(this);
    }

    @Override
    public void save(RushCallback callback) {
        RushCore.getInstance().save(this, callback);
    }

    @Override
    public void delete() {
        RushCore.getInstance().delete(this);
    }

    @Override
    public void delete(RushCallback callback) {
        RushCore.getInstance().delete(this, callback);
    }

    @Override
    public String getId() {
        return RushCore.getInstance().getId(this);
    }

    public int getUserCredits() {
        return userCredits;
    }

    public void setUserCredits(int userCredits) {
        this.userCredits = userCredits;
    }

    public int getUserBits() {
        return userBits;
    }

    public void setUserBits(int userBits) {
        this.userBits = userBits;
    }
}
