package com.portol.runnable;

import android.util.Log;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.portol.common.model.PortolPlatform;
import com.portol.common.model.player.Player;
import com.portol.common.model.player.PlayerGetRequest;

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
public class DeviceGetterThread extends Thread {

    String server = "REDACTED";
    int port = REDACTED;
    private List<PortolPlatform> results;
    private PlayerGetRequest req;
    public DeviceGetterThread(PlayerGetRequest req) {
        super();
        this.req = req;
    }

    public List<PortolPlatform> getResults() {
        return results;
    }

    @Override
    public void run() {

        ObjectMapper mapper = new ObjectMapper();
        try {
            Log.d("PlayerGetter", mapper.writeValueAsString(req));
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        HttpPost httpPost = new HttpPost(server + ":" + port + "/api/v0/device/all");
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

        results = new ArrayList<PortolPlatform>();
        try {

            if (resp.getEntity() != null) {
                String contents = IOUtils.toString(resp.getEntity().getContent(), "UTF-8");
                results = mapper.readValue(contents, new TypeReference<List<PortolPlatform>>() {
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
