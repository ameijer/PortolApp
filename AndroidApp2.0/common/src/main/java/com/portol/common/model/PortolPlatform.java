package com.portol.common.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.io.Serializable;
import java.util.UUID;

import co.uk.rushorm.core.Rush;
import co.uk.rushorm.core.RushCallback;
import co.uk.rushorm.core.RushCore;

@JsonIgnoreProperties(ignoreUnknown = true)
public class PortolPlatform implements Serializable, Rush {

    /**
     *
     */
    private static final long serialVersionUID = 8271368781302939617L;
    private String platformId;
    private String platformName = "unspecified";
    private String platformType = "unspecified";
    private String platformColor;
    private boolean paired;
    private int activePlayers = 0;

    public PortolPlatform(String platformName, String platformType, String color) {


        this(platformName, platformType, UUID.randomUUID().toString(), color);


    }
    public PortolPlatform(String platformName, String platformType, String platformId, String color) {
        super();
        this.platformColor = color;
        this.platformId = platformId;
        this.platformName = platformName;
        this.platformType = platformType;

    }
    public PortolPlatform() {
        super();
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

    public int getActivePlayers() {
        return activePlayers;
    }

    public void setActivePlayers(int activePlayers) {
        this.activePlayers = activePlayers;
    }

    public boolean isPaired() {
        return paired;
    }

    public void setPaired(boolean paired) {
        this.paired = paired;
    }

    public String getPlatformId() {
        return platformId;
    }

    public void setPlatformId(String platformId) {
        this.platformId = platformId;
    }

    public String getPlatformType() {
        return platformType;
    }

    public void setPlatformType(String platformType) {
        this.platformType = platformType;
    }

    public String getPlatformColor() {
        return platformColor;
    }

    public void setPlatformColor(String platformColor) {
        this.platformColor = platformColor;
    }

    public String getPlatformName() {
        return platformName;
    }

    public void setPlatformName(String platformName) {
        this.platformName = platformName;
    }

}
