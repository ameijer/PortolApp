package com.portol.common.model.content;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.io.Serializable;
import java.util.UUID;

import co.uk.rushorm.core.Rush;
import co.uk.rushorm.core.RushCallback;
import co.uk.rushorm.core.RushCore;

/**
 * Created by alex on 9/30/15.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class CategoryReference implements Serializable, Rush {
    /**
     *
     */
    private static final long serialVersionUID = -8306288323038988298L;
    public String nonce = UUID.randomUUID().toString();
    private String categoryId;

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

    public String getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(String cat) {
        this.categoryId = cat;
    }

}

