package com.portol.common.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.portol.common.model.app.XmitPortolToken;

import java.io.Serializable;
import java.util.Date;

import co.uk.rushorm.core.Rush;
import co.uk.rushorm.core.RushCallback;
import co.uk.rushorm.core.RushCore;

@JsonIgnoreProperties(ignoreUnknown = true)
public class PortolToken implements Serializable, Rush {


    /**
     *
     */
    private static final long serialVersionUID = -481812811933799957L;
    private String value;
    private Date expiration;

    public PortolToken(XmitPortolToken xmitPortolToken) {
        super();
        this.value = xmitPortolToken.getValue();
        this.expiration = xmitPortolToken.getExpiration();

    }

    public PortolToken() {
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

    public Date getExpiration() {
        return expiration;
    }

    public void setExpiration(Date expiration) {
        this.expiration = expiration;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
