package com.portol.common.model.bookmark;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.io.Serializable;
import java.util.Date;

import co.uk.rushorm.core.Rush;
import co.uk.rushorm.core.RushCallback;
import co.uk.rushorm.core.RushCore;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Bookmark implements Serializable, Rush {

    /**
     *
     */
    private static final long serialVersionUID = -2371445757464412820L;
    private String bookmarkedContentId;
    private String referrerId;
    private Date dateBookmarked;

    public Bookmark(String bookmarkedContentId, Date dateBookmarked) {
        super();
        this.setBookmarkedContentId(bookmarkedContentId);
        this.setDateBookmarked(dateBookmarked);
    }

    public Bookmark() {
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

    public Date getDateBookmarked() {
        return dateBookmarked;
    }

    public void setDateBookmarked(Date dateBookmarked) {
        this.dateBookmarked = dateBookmarked;
    }

    public String getBookmarkedContentId() {
        return bookmarkedContentId;
    }

    public void setBookmarkedContentId(String bookmarkedContentId) {
        this.bookmarkedContentId = bookmarkedContentId;
    }

    public String getReferrerId() {
        return referrerId;
    }

    public void setReferrerId(String referrerId) {
        this.referrerId = referrerId;
    }
}
