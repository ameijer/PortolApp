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
import com.portol.common.model.user.User;

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
public class PlayerAddThread extends Thread {

    private final Player toAdd;
    private final User toAddTo;
    String server = "REDACTED";
    int port = 5555;
    private User updated;
    public PlayerAddThread(Player toAdd, User toAddTo) {
        super();
        this.toAdd = toAdd;
        this.toAddTo = toAddTo;
    }

    public User getUpdated() {
        return updated;
    }

    @Override
    public void run() {
        ObjectMapper mapper = new ObjectMapper();


        Log.d("PlayerGetter", toAdd.getHostPlatform().getPlatformId());

        HttpPost httpPost = new HttpPost(server + ":" + port + "/api/v0/user/adopt/" + toAdd.getHostPlatform().getPlatformId());
        try {
            httpPost.setEntity(new StringEntity(toAddTo.getUserId()));
        } catch (UnsupportedEncodingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        httpPost.setHeader("Accept", "application/json");
        httpPost.setHeader("Content-type", "application/json");


        HttpResponse resp = null;
        try {
            resp = new DefaultHttpClient().execute(httpPost);
        } catch (ClientProtocolException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        } catch (IOException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }


        try {

            if (resp.getEntity() != null) {
                String contents = IOUtils.toString(resp.getEntity().getContent(), "UTF-8");
                updated = mapper.readValue(contents, User.class);
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
