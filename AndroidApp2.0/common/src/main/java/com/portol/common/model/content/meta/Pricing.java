package com.portol.common.model.content.meta;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.io.Serializable;
import java.util.UUID;

import co.uk.rushorm.core.Rush;
import co.uk.rushorm.core.RushCallback;
import co.uk.rushorm.core.RushCore;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Pricing implements Serializable, Rush {
    /**
     *
     */
    private static final long serialVersionUID = -8677364022227664401L;
    public String pricingId = UUID.randomUUID().toString();
    //prive per view for VOD, or price per hour for live
    private long priceInCents;
    private int shardPrice;
    private long priceInBits;

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

    public long getPriceInCents() {
        return priceInCents;
    }

    public void setPriceInCents(long priceInCents) {
        this.priceInCents = priceInCents;
    }

    public int getShardPrice() {
        return shardPrice;
    }

    public void setShardPrice(int shardPrice) {
        this.shardPrice = shardPrice;
    }

    public long getPriceInBits() {
        return priceInBits;
    }

    public void setPriceInBits(long priceInBits) {
        this.priceInBits = priceInBits;
    }
}
