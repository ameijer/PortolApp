package com.portol.common.model.user;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.io.Serializable;

import co.uk.rushorm.core.Rush;
import co.uk.rushorm.core.RushCallback;
import co.uk.rushorm.core.RushCore;

@JsonIgnoreProperties(ignoreUnknown = true)
public class UserSettings implements Serializable, Rush {

    /**
     *
     */
    private static final long serialVersionUID = 700748486954918789L;
    private Currency preferred;

    public Currency getPreferred() {
        return preferred;
    }

    public void setPreferred(Currency preferred) {
        this.preferred = preferred;
    }

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

    public enum Currency {
        CENTS, CREDITS, BTC
    }

}
