package com.portol.runnable;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.portol.common.model.app.AppConnectResponse;
import com.portol.common.model.app.AppPaymentRequest;
import com.portol.common.model.content.ContentMetadata;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.IOException;
import java.io.UnsupportedEncodingException;


public class AppPayerThread extends Thread {
    String server = "REDACTED";
    int port = 5555;

    AppPaymentRequest req;

    AppConnectResponse mConnectResponse;
    ContentMetadata currentlyViewing;


    public AppPayerThread(AppPaymentRequest req) {
        this.req = req;
    }

    public AppConnectResponse getConnectionInfo() {
        return mConnectResponse;
    }

    public void setCurrentlyViewing(ContentMetadata purchasedContent) {
        mConnectResponse.setPurchasedContent(purchasedContent);
    }

    ;

    public AppConnectResponse getConnectResponse() {
        return mConnectResponse;
    }

    public void run() {

        System.out.println("attemping to submit payment request to server");

        ObjectMapper mapper = new ObjectMapper();
        try {
            System.out.println(mapper.writeValueAsString(req));
        } catch (JsonProcessingException e) {

            e.printStackTrace();
        }

        HttpPost httpPost = new HttpPost(server + ":" + port + "/api/v0/buyvideo/app");
        try {
            String contents = mapper.writeValueAsString(req);
            httpPost.setEntity(new StringEntity(contents));
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
        } catch (ClientProtocolException e) {

            e.printStackTrace();
        } catch (IOException e) {

            e.printStackTrace();
        }


        if (resp.getStatusLine().getStatusCode() != 500) {
            try {
                mConnectResponse = mapper.readValue(resp.getEntity().getContent(), AppConnectResponse.class);
            } catch (JsonParseException e) {
                e.printStackTrace();
            } catch (JsonMappingException e) {
                e.printStackTrace();
            } catch (IllegalStateException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            currentlyViewing = mConnectResponse.getPurchasedContent();
        } else {
            //server error
            currentlyViewing = null;
        }

    }
}
