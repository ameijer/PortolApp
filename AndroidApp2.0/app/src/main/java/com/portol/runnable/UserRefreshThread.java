package com.portol.runnable;

import android.util.Log;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.portol.common.model.LoginRequest;
import com.portol.common.model.PortolPlatform;
import com.portol.common.model.PortolToken;
import com.portol.common.model.user.User;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

/**
 * Created by Alex on 6/17/2015.
 */
public class UserRefreshThread extends Thread {

    private PortolToken oldTok;
    private User toRefresh;
    private String server = "REDACTED";
    private int port = REDACTED;
    private User updated;


    public UserRefreshThread(PortolToken toUse, User toRefresh) {
        super();
        this.oldTok = toUse;
        this.toRefresh = toRefresh;
    }

    public User getUpdatedUser() {
        return updated;
    }


    public void run() {

        System.out.println("updating user");


        Log.d("portol", "Old token: " + oldTok.getValue());


        ObjectMapper mapper = new ObjectMapper();
        try {
            System.out.println(mapper.writeValueAsString(oldTok));
        } catch (JsonProcessingException e3) {
            // TODO Auto-generated catch block
            e3.printStackTrace();
        }

        HttpPost httpPost = new HttpPost(server + ":" + port + "/api/v0/user/refresh");
        try {


            httpPost.setEntity(new StringEntity(mapper.writeValueAsString(toRefresh)));
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
            String contents = IOUtils.toString(resp.getEntity().getContent(), "UTF-8");
            this.updated = mapper.readValue(contents, User.class);
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
