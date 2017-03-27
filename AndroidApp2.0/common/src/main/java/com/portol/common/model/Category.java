package com.portol.common.model;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import org.mongojack.Id;

import java.io.Serializable;
import java.util.Date;
import java.util.UUID;

import co.uk.rushorm.core.Rush;
import co.uk.rushorm.core.RushCallback;
import co.uk.rushorm.core.RushCore;

/**
 * Created by alex on 6/8/15.
 * <p>
 * Used to offer dynamically changing categories
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Category implements Serializable, Rush {

    public static final String VIDEO = "VIDEO";
    public static final String MUSIC = "MUSIC";
    /**
     *
     */
    private static final long serialVersionUID = -435723044313107458L;
    private String version;

    private String iconEncoded;
    private String desc;
    private String name;

    @Id
    private String categoryId = UUID.randomUUID().toString();
    private int position;
    private String type;
    private Date validDate;
    private Date expirationDate;

    public Category() {
    }

    public Category(String name) {

    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(String id) {
        this.categoryId = id;
    }

    public Date getValidDate() {
        return validDate;
    }

    public void setValidDate(Date validDate) {
        this.validDate = validDate;
    }

    public Date getExpirationDate() {
        return expirationDate;
    }

    public void setExpirationDate(Date expirationDate) {
        this.expirationDate = expirationDate;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getIconEncoded() {
        return iconEncoded;
    }

    public void setIconEncoded(String icon) {
        this.iconEncoded = icon;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
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

}
