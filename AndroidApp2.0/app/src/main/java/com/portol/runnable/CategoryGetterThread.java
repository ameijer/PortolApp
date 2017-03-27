package com.portol.runnable;

import android.util.Log;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.portol.common.model.Category;
import com.portol.common.model.CategorySearchRequest;
import com.portol.repository.CategoriesRepository;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by alex on 6/25/15.
 */
public class CategoryGetterThread extends Thread {

    private final CategorySearchRequest request;
    List<Category> returnedCategories;
    String server = "REDACTED";
    int port = REDACTED;
    public CategoryGetterThread(CategorySearchRequest req) {
        super();
        this.request = req;

    }

    public List<Category> getReturnedCategories() {
        return returnedCategories;
    }

    @Override
    public void run() {

        ObjectMapper mapper = new ObjectMapper();
        try {
            String rawReq = mapper.writeValueAsString(request);
            Log.d("CategoryGetter", "Raw json to send: " + rawReq);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        HttpPost httpPost = new HttpPost(server + ":" + port + "/api/v0/categories");
        try {
            httpPost.setEntity(new StringEntity(mapper.writeValueAsString(request)));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        httpPost.setHeader("Accept", "application/json");
        httpPost.setHeader("Content-type", "application/json");


        HttpResponse resp = null;
        try {
            resp = new DefaultHttpClient().execute(httpPost);
        } catch (ClientProtocolException e1) {
            e1.printStackTrace();
        } catch (IOException e1) {
            e1.printStackTrace();
        }


        try {
            String contents = IOUtils.toString(resp.getEntity().getContent(), "UTF-8");
            returnedCategories = mapper.readValue(contents, new TypeReference<List<Category>>() {
            });

        } catch (JsonParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (JsonMappingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IllegalStateException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
