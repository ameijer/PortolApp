package com.portol.common.model;

public class CategorySearchRequest {


    private String apiKey;
    private RequestType type;

    public CategorySearchRequest(String apiKey, RequestType type) {
        this.apiKey = apiKey;
        this.type = type;
    }

    public CategorySearchRequest() {
    }

    public RequestType getType() {
        return type;
    }

    public void setType(RequestType type) {
        this.type = type;
    }

    public String getApiKey() {
        return apiKey;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    public enum RequestType {
        MUSIC, VIDEO, TEXT
    }

}
