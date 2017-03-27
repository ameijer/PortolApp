package com.portol.common.model.content;

import java.util.ArrayList;

public class ContentSearchRequest {


    private String apiKey;
    private ArrayList<String> contentId;
    private int count;
    private ArrayList<String> keyWords;
    private RequestType type;
    private Sort sortType;
    private int pageIndex;

    public ArrayList<String> getContentId() {
        return contentId;
    }

    public void setContentId(ArrayList<String> contentId) {
        this.contentId = contentId;
    }

    public ArrayList<String> getKeyWords() {
        return keyWords;
    }

    public void setKeyWords(ArrayList<String> keyWords) {
        this.keyWords = keyWords;
    }

    public RequestType getType() {
        return type;
    }

    public void setType(RequestType type) {
        this.type = type;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public String getApiKey() {
        return apiKey;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    public Sort getSortType() {
        return sortType;
    }

    public void setSortType(Sort sortType) {
        this.sortType = sortType;
    }

    public int getPageIndex() {
        return pageIndex;
    }

    public void setPageIndex(int pageIndex) {
        this.pageIndex = pageIndex;
    }

    public enum RequestType {
        LIVE, VOD, LESS_THAN, MORE_THAN, ALL, WITH_ID
    }

    public enum Sort {
        UNSORTED, VIEW_COUNT_ASCEND, VIEW_COUNT_DSCEND
    }
}
