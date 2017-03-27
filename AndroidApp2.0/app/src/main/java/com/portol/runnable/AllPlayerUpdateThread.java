package com.portol.runnable;

import android.util.Log;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.portol.common.model.content.ContentMetadata;
import com.portol.common.model.player.Player;
import com.portol.common.model.player.PlayerGetRequest;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.List;

/**
 * Created by alex on 6/24/15.
 */
public class AllPlayerUpdateThread extends Thread {


    public static final String TAG = "AllPlayerUpdateThread";
    String server = "REDACTED";
    int port = 5555;
    String path = "/api/v0/player/active/{platform}";
    private List<Player> activePlayers;
    private PlayerGetRequest req;

    public AllPlayerUpdateThread(String platformToGet) {
        super();
        path = path.replace("{platform}", platformToGet);
    }

    public List<Player> getActivePlayers() {


        return activePlayers;
    }

    @Override
    public void run() {

        ObjectMapper mapper = new ObjectMapper();
        try {
            Log.d("ContentGetter", mapper.writeValueAsString(req));
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }


        HttpGet httpGet = new HttpGet(server + ":" + port + path);
        httpGet.setHeader("Accept", "application/json");
        httpGet.setHeader("Content-type", "application/json");


        HttpResponse resp = null;
        try {
            resp = new DefaultHttpClient().execute(httpGet);
        } catch (ClientProtocolException e1) {
            e1.printStackTrace();
        } catch (IOException e1) {
            e1.printStackTrace();
        }


        try {

            if (resp.getEntity() != null) {
                String contents = IOUtils.toString(resp.getEntity().getContent(), "UTF-8");
                activePlayers = mapper.readValue(contents, new TypeReference<List<Player>>() {
                });
            }
        } catch (JsonParseException e) {
            e.printStackTrace();
        } catch (JsonMappingException e) {
            e.printStackTrace();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
