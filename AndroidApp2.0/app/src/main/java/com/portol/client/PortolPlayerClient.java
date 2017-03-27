package com.portol.client;

import android.util.Log;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.portol.common.model.MovieFact;
import com.portol.common.model.SeekStatus;
import com.portol.common.model.app.AppCommand;
import com.portol.common.model.content.ContentMetadata;
import com.portol.common.model.player.Player;
import com.portol.common.model.user.User;
import com.portol.runnable.AppCommandSenderThread;
import com.portol.runnable.MovieFaxGetterThread;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.drafts.Draft_17;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;
import java.net.URISyntaxException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;

/**
 * Created by alex on 6/16/15.
 */
public class PortolPlayerClient {
    public static final String TAG = "PlayerClient";
    private final String playerID;
    String wsuri = "ws://<cloudlet>:8901/client/ws";
    ObjectMapper om = new ObjectMapper();
    SeekStatus mostRecent = null;
    private boolean connected;
    private WebSocketClient client;
    Thread synchronizer = new Thread() {
        @Override
        public void run() {
            while (true) {
                try {
                    Thread.sleep(45000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                User currentState = null;

                try {
                    client.send("ping");
                } catch (Exception e) {
                    Log.w(TAG, "ping failed for websocket");
                }

            }
        }
    };
    private String cloudIP;
    private PlayerActivityListener listener;

    public PortolPlayerClient(String source, String playerId) throws NoSuchAlgorithmException, URISyntaxException {
        this.cloudIP = source;
        this.playerID = playerId;

        final String queryString = "?id=" + playerID;

        wsuri = wsuri.replace("<cloudlet>", this.cloudIP);

        client = new WebSocketClient(new URI(this.wsuri + queryString), new Draft_17()) {
            @Override
            public void onOpen(ServerHandshake handshakedata) {
                Log.d(TAG, "Status: Connected to " + wsuri + queryString);
            }

            @Override
            public void onMessage(String message) {
                Log.d(TAG, "Got echo: " + message);

                if (message.contains("pong") && message.length() < 10) {
                    Log.i(TAG, "pong received");
                    return;
                }


                try {
                    mostRecent = om.readValue(message, SeekStatus.class);

                    if (listener != null) {
                        listener.onSeekUpdate(playerID, mostRecent);
                    }
                } catch (Exception e) {
                    Log.e(TAG, "error parsing player object from ws, ignoring update", e);
                    return;

                }

            }

            @Override
            public void onClose(int code, String reason, boolean remote) {
                Log.d(TAG, "Connection lost.");
            }

            @Override
            public void onError(Exception ex) {
                Log.e(TAG, "Error connecting to websocket");
            }
        };


        client.connect();
        synchronizer.start();
    }

    public void setPlayerActivityListener(PlayerActivityListener listener) {
        this.listener = listener;
    }

    public boolean sendSeek(SeekStatus target, String playerId) {
        if (client == null || client.isClosed()) {
            return false;
        }
        AppCommand seek = new AppCommand(AppCommand.Command.SEEK, playerId);
        seek.setNewStatus(target);

        String newState = null;
        try {
            newState = om.writeValueAsString(seek);
        } catch (JsonProcessingException e) {
            Log.e(TAG, "error processing seek data", e);
            return false;
        }
        client.send(newState);
        return true;
    }

    public ArrayList<MovieFact> getFaxForFocused() {
        MovieFaxGetterThread faxGet = new MovieFaxGetterThread(playerID, cloudIP);
        faxGet.start();
        try {
            faxGet.join();
        } catch (InterruptedException e) {
            Log.e(TAG, "error running fax getter thread", e);
            return null;
        }
        return faxGet.getFacts();
    }

    public SeekStatus getMostRecent() {
        return mostRecent;
    }

    private void testConnection() {
        AppCommand cmd = new AppCommand(AppCommand.Command.STATUS_CHECK, playerID);


        AppCommandSenderThread statusChecker = new AppCommandSenderThread(cmd, cloudIP);

        statusChecker.start();
        try {
            statusChecker.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
            this.connected = false;
        }

        Player reply = statusChecker.getReply();

        if (reply != null && reply.getStatus() != null && !reply.getStatus().equalsIgnoreCase(Player.FAILURE_STREAM)) {
            //then lets assume we're good
            connected = true;
        } else {
            connected = false;
        }


    }

    public boolean isConnected() {
        this.testConnection();
        return connected;
    }

    public Player sendPause() {

        AppCommand cmd = new AppCommand(AppCommand.Command.PAUSE, playerID);


        AppCommandSenderThread pauser = new AppCommandSenderThread(cmd, cloudIP);

        pauser.start();
        try {
            pauser.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
            this.connected = false;
        }

        Player reply = pauser.getReply();

        return reply;


    }

    public Player sendSeekFwd() {
        Log.e(TAG, "sendseekfwd not yet implemented!");
        return null;
    }

    public Player sendSeekBack() {
        Log.e(TAG, "sendseekback not yet implemented!");
        return null;
    }

    public Player sendStop() {
        Log.e(TAG, "sendstop not yet implemented!");
        AppCommand cmd = new AppCommand(AppCommand.Command.STOP, playerID);


        AppCommandSenderThread stopper = new AppCommandSenderThread(cmd, cloudIP);

        stopper.start();
        try {
            stopper.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
            this.connected = false;
        }

        Player reply = stopper.getReply();

        return reply;
    }

    public Player sendPlay() {

        AppCommand cmd = new AppCommand(AppCommand.Command.PLAY, playerID);


        AppCommandSenderThread pauser = new AppCommandSenderThread(cmd, cloudIP);

        pauser.start();
        try {
            pauser.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
            this.connected = false;
        }

        Player reply = pauser.getReply();

        return reply;
    }

    public interface PlayerActivityListener {
        public void onSeekUpdate(String playerId, SeekStatus updated);
    }
}
