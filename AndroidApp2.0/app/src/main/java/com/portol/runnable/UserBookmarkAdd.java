package com.portol.runnable;

import android.util.Log;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.portol.common.model.PortolPlatform;
import com.portol.common.model.bookmark.Bookmark;
import com.portol.common.model.user.User;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.List;

/**
 * Created by Alex on 6/17/2015.
 */
public class UserBookmarkAdd extends Thread {

    public static final String TAG = "UserUpdateThread";
    private final User toUpdate;
    private final Bookmark toAdd;
    private String server = "REDACTED";
    private int port = REDACTED;
    private List<Bookmark> returned;

    public UserBookmarkAdd(User toupdate, Bookmark toAdd) {
        super();
        this.toUpdate = toupdate;
        this.toAdd = toAdd;
    }

    public void run() {

        System.out.println("updating user");


        ObjectMapper mapper = new ObjectMapper();
        try {
            Log.i(TAG, "JSON being sent: " + mapper.writeValueAsString(toAdd));
        } catch (JsonProcessingException e3) {
            // TODO Auto-generated catch block
            e3.printStackTrace();
        }

        HttpPost httpPost = new HttpPost(server + ":" + port + "/api/v0/bookmark/synch?userId=" + toUpdate.getUserId());
        try {


            httpPost.setEntity(new StringEntity(mapper.writeValueAsString(toAdd)));
        } catch (UnsupportedEncodingException e2) {
            // TODO Auto-generated catch block
            e2.printStackTrace();
        } catch (JsonProcessingException e2) {
            // TODO Auto-generated catch block
            e2.printStackTrace();
        }
        httpPost.setHeader("Accept", "application/json");
        httpPost.setHeader("Content-type", "application/json");


        HttpResponse resp = null;
        try {
            resp = new DefaultHttpClient().execute(httpPost);

        } catch (ClientProtocolException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        } catch (Exception e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }


        try {
            if (resp.getEntity() == null) return;
            String contents = IOUtils.toString(resp.getEntity().getContent(), "UTF-8");
            returned = mapper.readValue(contents, new TypeReference<List<Bookmark>>() {
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

    public List<Bookmark> getReturned() {
        return returned;
    }
}
