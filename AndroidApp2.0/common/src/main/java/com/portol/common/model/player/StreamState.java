package com.portol.common.model.player;

import java.io.Serializable;

import co.uk.rushorm.core.Rush;
import co.uk.rushorm.core.RushCallback;
import co.uk.rushorm.core.RushCore;

public class StreamState implements Serializable, Rush {

    /**
     *
     */
    private static final long serialVersionUID = -409453707095322628L;
    private int numsegments;
    private int maxSent;
    private long segmentDuration;

    public StreamState(int numsegments, int maxSent, long segmentDuration) {
        super();
        this.numsegments = numsegments;
        this.maxSent = maxSent;
        this.segmentDuration = segmentDuration;
    }


    public StreamState() {
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

    public int getNumsegments() {
        return numsegments;
    }

    public void setNumsegments(int numsegments) {
        this.numsegments = numsegments;
    }

    public int getMaxSent() {
        return maxSent;
    }

    public void setMaxSent(int maxSent) {
        this.maxSent = maxSent;
    }

    public long getSegmentDuration() {
        return segmentDuration;
    }

    public void setSegmentDuration(long segmentDuration) {
        this.segmentDuration = segmentDuration;
    }


}