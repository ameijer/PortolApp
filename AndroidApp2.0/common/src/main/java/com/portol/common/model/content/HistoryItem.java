package com.portol.common.model.content;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import org.mongojack.Id;

import java.io.Serializable;
import java.util.UUID;

import co.uk.rushorm.core.Rush;
import co.uk.rushorm.core.RushCallback;
import co.uk.rushorm.core.RushCore;

@JsonIgnoreProperties(ignoreUnknown = true)
public class HistoryItem implements Serializable, Rush {

    /**
     *
     */
    private static final long serialVersionUID = -4565279989140828664L;
    @Id
    private String id = UUID.randomUUID().toString();
    private String color;
    private String platform;
    private long timeViewed = 0;
    private String viewedContentId;

    public HistoryItem() {
        super();
    }


    public HistoryItem(String viewedContentId) {
        super();
        this.timeViewed = System.currentTimeMillis();
        this.viewedContentId = viewedContentId;
    }

    public HistoryItem(long timeViewed, String viewedContentId) {
        super();
        this.timeViewed = timeViewed;
        this.viewedContentId = viewedContentId;
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

    public void setId(String id) {
        this.id = id;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public long getTimeViewed() {
        return timeViewed;
    }

    public void setTimeViewed(long timeViewed) {
        this.timeViewed = timeViewed;
    }

    public String getViewedContentId() {
        return viewedContentId;
    }

    public void setViewedContentId(String viewedContentId) {
        this.viewedContentId = viewedContentId;
    }

    public String getPlatform() {
        return platform;
    }

    public void setPlatform(String platform) {
        this.platform = platform;
    }
}
