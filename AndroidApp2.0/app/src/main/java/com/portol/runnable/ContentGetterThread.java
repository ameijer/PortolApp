package com.portol.runnable;

import android.util.Log;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.portol.common.model.content.ContentMetadata;
import com.portol.common.model.content.ContentSearchRequest;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by alex on 6/24/15.
 */
public class ContentGetterThread extends Thread {

    String server = "REDACTED";
    int port = REDACTED;
    private List<ContentMetadata> results;
    private ContentSearchRequest req;
    public ContentGetterThread(ContentSearchRequest req) {
        super();
        this.req = req;
    }

    public List<ContentMetadata> getResults() {
        return results;
    }

    public List<ContentMetadata> getReturnedContent() {


        return results;
    }

    @Override
    public void run() {

        ObjectMapper mapper = new ObjectMapper();
        try {
            Log.d("ContentGetter", mapper.writeValueAsString(req));
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        HttpPost httpPost = new HttpPost(server + ":" + port + "/api/v0/contentFind");
        try {
            httpPost.setEntity(new StringEntity(mapper.writeValueAsString(req)));
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

        results = new ArrayList<ContentMetadata>();
        try {

            String contents = IOUtils.toString(resp.getEntity().getContent(), "UTF-8");
            if (resp.getEntity() != null) {
                results = mapper.readValue(contents, new TypeReference<List<ContentMetadata>>() {
                });
            }
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
