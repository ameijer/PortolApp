package com.portol.common.model.bookmark;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import org.mongojack.Id;

import java.io.Serializable;
import java.util.UUID;

@JsonIgnoreProperties(ignoreUnknown = true)
public class BookmarkResponse implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 4279711134365686064L;


    @Id
    private String id = UUID.randomUUID().toString();

    private boolean success;
    private String status;

    public BookmarkResponse() {
        super();
    }

    public BookmarkResponse(boolean success, String status) {
        super();
        this.success = success;
        this.status = status;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

}
