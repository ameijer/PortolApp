package com.portol.runnable;

import android.util.Log;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.portol.common.model.content.ContentMetadata;
import com.portol.common.model.content.ContentSearchRequest;
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
public class PlayerGetterThread extends Thread {


    public static final String TAG = "PlayerGetterThread";
    String server = "REDACTED";
    int port = 5555;
    private Player result;
    private PlayerGetRequest req;


    public PlayerGetterThread(PlayerGetRequest req) {
        super();
        this.req = req;
    }

    public Player getReturnedPlayer() {


        return result;
    }

    @Override
    public void run() {

        ObjectMapper mapper = new ObjectMapper();
        try {
            Log.d("ContentGetter", mapper.writeValueAsString(req));
        } catch (JsonProcessingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        String path = "/api/v0/player/";
        switch (req.getPairingType()) {
            case (PlayerGetRequest.QR_ID):
                path += "address";
                break;
            case (PlayerGetRequest.TEXT_ID):
                path += "code";
                break;
            case (PlayerGetRequest.PLAYER_ID):
                path += "playerId";
                break;
            default:
                Log.e(TAG, "Invalid pairing type specified");
                return;
        }
        HttpPost httpPost = new HttpPost(server + ":" + port + path);
        try {
            httpPost.setEntity(new StringEntity(mapper.writeValueAsString(req)));
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
                result = mapper.readValue(contents, Player.class);
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
