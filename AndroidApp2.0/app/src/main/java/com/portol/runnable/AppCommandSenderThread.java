package com.portol.runnable;

import android.util.Log;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.portol.common.model.app.AppCommand;
import com.portol.common.model.player.Player;


import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.IOException;
import java.io.UnsupportedEncodingException;


public class AppCommandSenderThread extends Thread {

    public static final String cmdPath = "/api/v0/client";
    public static final int cmdPort = REDACTED;
    public static final String cloudprotocol = "http://";
    private final String targetIP;

    private AppCommand cmd;

    private Player sreply;

    public AppCommandSenderThread(AppCommand cmd, String targetIP) {
        this.cmd = cmd;
        this.targetIP = targetIP;

    }

    public Player getReply() {
        return sreply;
    }

    public void run() {

        System.out.println("attemping to send app command to server");

        ObjectMapper mapper = new ObjectMapper();
        try {
            Log.d("CommandSender", "Sending JSON:" + mapper.writeValueAsString(cmd));
        } catch (JsonProcessingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        HttpPost httpPost = new HttpPost(this.cloudprotocol + targetIP + ":" + cmdPort + cmdPath);
        try {
            httpPost.setEntity(new StringEntity(mapper.writeValueAsString(cmd)));
        } catch (UnsupportedEncodingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (JsonProcessingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        httpPost.setHeader("Accept", "application/json");
        httpPost.setHeader("Content-type", "application/json");

        HttpResponse resp = null;
        try {
            resp = new DefaultHttpClient().execute(httpPost);
        } catch (ClientProtocolException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        Player response = null;
        try {
            if (resp.getEntity() != null) {
                response = mapper.readValue(resp.getEntity().getContent(), Player.class);
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

        this.sreply = response;
    }
}
