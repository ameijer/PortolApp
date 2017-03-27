package com.portol.runnable;

import android.util.Log;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.portol.common.model.user.User;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

/**
 * Created by Aidan on 5/25/15.
 */
public class UserRegisterThread extends Thread {
    String server = "REDACTED";
    int port = REDACTED;
    private String mEmail;
    private String mUsername;
    private String mPassword;
    private User mNoob;
    private User mRegisteredUser;

    public UserRegisterThread(String email, String uname, String pass) {
        mEmail = email;
        mUsername = uname;
        mPassword = pass;
    }

    public void run() {
        mNoob = new User();
        mNoob.setEmail(mEmail);
        mNoob.setUserName(mUsername);
        mNoob.setHashedPass(new String(Hex.encodeHex(DigestUtils.sha512(mPassword))));

        Log.d("prefs", "registering new user: " + mNoob.getUserName());

        ObjectMapper mapper = new ObjectMapper();
        try {
            System.out.println(mapper.writeValueAsString(mNoob));
        } catch (JsonProcessingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        HttpPost httpPost = new HttpPost(server + ":" + port + "/api/v0/user/newuser");
        try {
            httpPost.setEntity(new StringEntity(mapper.writeValueAsString(mNoob)));
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

        mRegisteredUser = null;
        try {
            User temp = mapper.readValue(resp.getEntity().getContent(), User.class);
            mRegisteredUser = temp;
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

        if (mRegisteredUser != null) {
            Log.d("prefs", "User registered!");
        } else {
            Log.d("prefs", "User NOT registered!");
        }
    }

    public User getRegisteredUser() {
        return mRegisteredUser;
    }
}
