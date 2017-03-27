package com.portol.common.model.payment;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import org.mongojack.Id;

import java.io.Serializable;
import java.util.UUID;

@JsonIgnoreProperties(ignoreUnknown = true)
public class PlayerBuyRequest implements Serializable {


    /**
     *
     */
    private static final long serialVersionUID = -8574115836638604186L;
    @Id
    private String id = UUID.randomUUID().toString();
    private String playerId;

    private String btcAddress;


    public PlayerBuyRequest() {
        super();
    }


    public String getId() {
        return id;
    }


    public void setId(String id) {
        this.id = id;
    }


    public String getBtcAddress() {
        return btcAddress;
    }

    public void setBtcAddress(String btcAddress) {
        this.btcAddress = btcAddress;
    }


    public String getPlayerId() {
        return playerId;
    }


    public void setPlayerId(String playerId) {
        this.playerId = playerId;
    }
}
