package com.portol.runnable;

import android.content.Context;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.portol.common.model.LoginRequest;
import com.portol.common.model.PortolPlatform;
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

public class UserLoginThread extends Thread {
    String email;
    String uname;
    String pass;
    String server = "REDACTED";
    int port = 5555;
    PortolPlatform plat = null;

    User currentlyViewing;

    public UserLoginThread(String email, String uname, String pass, PortolPlatform thisPlat) {
        this.email = email;
        this.uname = uname;
        this.pass = pass;
        this.plat = thisPlat;
    }

    public User getCurrentlyViewing() {
        return currentlyViewing;
    }

    public void setCurrentlyViewing(User currentlyViewing) {
        this.currentlyViewing = currentlyViewing;
    }

    public void run() {

        System.out.println("logging in user");

        //User newusr = new User("superstronghashedpw", "richy", "richy@rich.com", "rich", 123456789)
        //WebResource webResource = client.resource();
        User loggingIn = new User();
        loggingIn.setEmail(email);
        loggingIn.setUserName(uname);
        loggingIn.setHashedPass(new String(Hex.encodeHex(DigestUtils.sha512(pass))));

        Log.d("portol", "Hashedd PW: " + pass);


        LoginRequest loginRequest = new LoginRequest(loggingIn, plat);
        ObjectMapper mapper = new ObjectMapper();
        try {
            System.out.println(mapper.writeValueAsString(loginRequest));
        } catch (JsonProcessingException e3) {
            // TODO Auto-generated catch block
            e3.printStackTrace();
        }
        //TODO: I might need String.valueOf(port)

        HttpPost httpPost = new HttpPost(server + ":" + port + "/api/v0/user/login");
        try {


            httpPost.setEntity(new StringEntity(mapper.writeValueAsString(loginRequest)));
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
        } catch (IOException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }


        try {
            String contents = IOUtils.toString(resp.getEntity().getContent(), "UTF-8");
            this.currentlyViewing = mapper.readValue(contents, User.class);
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
