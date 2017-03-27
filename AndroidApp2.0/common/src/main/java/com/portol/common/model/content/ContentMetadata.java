package com.portol.common.model.content;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.portol.common.model.content.meta.EPGBar;
import com.portol.common.model.content.meta.Pricing;
import com.portol.common.model.content.meta.Rating;
import com.portol.common.model.content.meta.SeriesInfo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.persistence.Id;

import co.uk.rushorm.core.Rush;
import co.uk.rushorm.core.RushCallback;
import co.uk.rushorm.core.RushCore;
import co.uk.rushorm.core.annotations.RushList;


@JsonIgnoreProperties(ignoreUnknown = true)
public class ContentMetadata implements Serializable, Rush {

    /**
     *
     */
    private static final long serialVersionUID = 4996791562233328507L;
    @Id
    private String metadataId = UUID.randomUUID().toString();
    private long availabilityTime;
    private String type;
    private String CurrentTitle;
    @RushList(classType = CategoryReference.class)
    private ArrayList<CategoryReference> memberOf;
    private int categoryRanking;
    //common
    private double ratingDouble;
    private long numLoads;
    //number of actual paid plays
    private long numPlays;
    //live only
    private long currentViewers;
    private Pricing prices;
    private String subtitle;
    //if live
    private EPGBar epg;
    //if vod
    private SeriesInfo seriesInfo;
    @RushList(classType = Rating.class)
    private List<Rating> ratingList;
    private String info;
    private String channelOrVideoTitle;
    private String creatorInfo;
    private String splashURL;
    private String parentContentId;
    private String parentContentKey;

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

    public long getAvailabilityTime() {
        return availabilityTime;
    }

    public void setAvailabilityTime(long availabilityTime) {
        this.availabilityTime = availabilityTime;
    }

    public Pricing getPrices() {
        return prices;
    }

    public void setPrices(Pricing prices) {
        this.prices = prices;
    }

    public String getSubtitle() {
        return subtitle;
    }

    public void setSubtitle(String subtitle) {
        this.subtitle = subtitle;
    }

    public List<Rating> getRatingList() {
        return ratingList;
    }

    public void setRatingList(List<Rating> ratingList) {
        this.ratingList = ratingList;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public String getSplashURL() {
        return splashURL;
    }

    public void setSplashURL(String splashURL) {
        this.splashURL = splashURL;
    }

    public String getCreatorInfo() {
        return creatorInfo;
    }

    public void setCreatorInfo(String creatorInfo) {
        this.creatorInfo = creatorInfo;
    }

    public long getNumLoads() {
        return numLoads;
    }

    public void setNumLoads(long numLoads) {
        this.numLoads = numLoads;
    }

    public long getNumPlays() {
        return numPlays;
    }

    public void setNumPlays(long numPlays) {
        this.numPlays = numPlays;
    }

    public double getRatingDouble() {
        return ratingDouble;
    }

    public void setRatingDouble(double ratingDouble) {
        this.ratingDouble = ratingDouble;
    }

    public String getParentContentId() {
        return parentContentId;
    }

    public void setParentContentId(String parentContentId) {
        this.parentContentId = parentContentId;
    }

    public long getCurrentViewers() {
        return currentViewers;
    }

    public void setCurrentViewers(long currentViewers) {
        this.currentViewers = currentViewers;
    }

    public String getCurrentTitle() {
        return CurrentTitle;
    }

    public void setCurrentTitle(String currentTitle) {
        CurrentTitle = currentTitle;
    }

    public String getChannelOrVideoTitle() {
        return channelOrVideoTitle;
    }

    public void setChannelOrVideoTitle(String channelOrVideoTitle) {
        this.channelOrVideoTitle = channelOrVideoTitle;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public EPGBar getEpg() {
        return epg;
    }

    public void setEpg(EPGBar epg) {
        this.epg = epg;
    }

    public SeriesInfo getSeriesInfo() {
        return seriesInfo;
    }

    public void setSeriesInfo(SeriesInfo seriesInfo) {
        this.seriesInfo = seriesInfo;
    }

    public List<CategoryReference> getMemberOf() {
        return memberOf;
    }

    public void setMemberOf(ArrayList<CategoryReference> memberOf) {
        this.memberOf = memberOf;
    }

    public int getCategoryRanking() {
        return categoryRanking;
    }

    public void setCategoryRanking(int categoryRanking) {
        this.categoryRanking = categoryRanking;
    }

    public String getParentContentKey() {
        return parentContentKey;
    }

    public void setParentContentKey(String parentContentKey) {
        this.parentContentKey = parentContentKey;
    }

    public String getMetadataId() {
        return metadataId;
    }

    public void setMetadataId(String id) {
        this.metadataId = id;

    }
}
