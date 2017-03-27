package com.portol.common.model.cdn;

import java.util.Date;
import java.util.List;

public class CDNReply {

    private Status status;
    private Date expirationDate;
    private List<RegionalServer> results;

    public CDNReply(Status status, Date expirationDate) {
        super();
        this.status = status;
        this.expirationDate = expirationDate;
    }

    public CDNReply() {

    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public Date getExpirationDate() {
        return expirationDate;
    }

    public void setExpirationDate(Date expirationDate) {
        this.expirationDate = expirationDate;
    }

    public List<RegionalServer> getResults() {
        return results;
    }

    public void setResults(List<RegionalServer> results) {
        this.results = results;
    }

    public enum Status {
        ADDED, UPDATED, ERROR, NO_RESULTS, QUERY_SUCCESS
    }

}
