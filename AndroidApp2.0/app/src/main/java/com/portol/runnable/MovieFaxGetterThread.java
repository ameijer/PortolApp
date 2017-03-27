package com.portol.runnable;

import android.util.Log;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.portol.common.model.MovieFact;
import com.portol.common.model.app.AppCommand;
import com.portol.common.model.content.ContentMetadata;
import com.portol.common.model.player.Player;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class MovieFaxGetterThread extends Thread {

    public static final String cmdPath = "/api/v0/module/moviefax";
    public static final int cmdPort = 8901;
    public static final String cloudprotocol = "http://";
    private final String targetIP;

    private final ObjectMapper mapper = new ObjectMapper();
    private final String playerId;
    private ArrayList<MovieFact> facts = new ArrayList<MovieFact>();

    public MovieFaxGetterThread(String playerId, String targetIP) {
        this.playerId = playerId;
        this.targetIP = targetIP;

    }

    public ArrayList<MovieFact> getFacts() {
        return facts;
    }

    public void run() {

        System.out.println("attemping to retureive facts from: " + targetIP);
        HttpGet httpGet = new HttpGet(this.cloudprotocol + targetIP + ":" + cmdPort + cmdPath + "?playerId=" + playerId);

        httpGet.setHeader("Accept", "application/json");
        httpGet.setHeader("Content-type", "application/json");

        HttpResponse resp = null;
        try {
            resp = new DefaultHttpClient().execute(httpGet);
        } catch (ClientProtocolException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        ArrayList<MovieFact> resultsRaw = null;
        try {
            String contents = IOUtils.toString(resp.getEntity().getContent(), "UTF-8");
            if (resp.getEntity() != null) {
                resultsRaw = mapper.readValue(contents, new TypeReference<ArrayList<MovieFact>>() {
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

        this.facts = resultsRaw;
        if (this.facts != null) {
            Collections.sort(this.facts);
        }
    }
}
