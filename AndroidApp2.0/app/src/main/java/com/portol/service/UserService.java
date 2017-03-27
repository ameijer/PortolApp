package com.portol.service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.portol.Portol;

import com.portol.common.model.PortolPlatform;
import com.portol.common.model.bookmark.Bookmark;
import com.portol.common.model.content.ContentMetadata;
import com.portol.common.model.player.Player;
import com.portol.common.model.user.User;
import com.portol.repository.PlayerRepository;
import com.portol.repository.UserRepository;
import com.portol.client.PortolClient;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.drafts.Draft_17;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;
import java.util.List;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;


/**
 * Created by alex on 6/8/15.
 */
public class UserService extends Service {

    public static final String TAG = "UserService";
    final String wsuri = "REDACTED";
    ObjectMapper mapper = new ObjectMapper();
    WebSocketClient client;
    ObjectMapper om = new ObjectMapper();
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
    private Portol app;
    private PortolClient pClient;
    private IBinder mBinder = new LocalBinder();
    private UserRepository userRepo;
    private PlayerRepository playerRepo;
    private User loggedIn;
    private final BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals("com.portol.UserUpdate")) {
                //synchronize user by pushing it to cloud
                loggedIn = (User) intent.getSerializableExtra("user");
                User updated = pClient.updateUser(loggedIn);

                if (!updated.getUserId().equalsIgnoreCase(loggedIn.getUserId())) {
                    try {
                        throw new Exception("error updating user, logged in id does not match exisiting id!");
                    } catch (Exception e) {
                        Log.e(TAG, "error updating user", e);
                        return;
                    }
                }
                try {
                    User saved = userRepo.save(updated);
                } catch (Exception e) {
                    Log.e(TAG, "error saving updated user");
                }

            } else if (action.equals("com.portol.Bookmarked")) {
                try {
                    loggedIn = userRepo.getLoggedInUser();
                } catch (Exception e) {
                    Log.e(TAG, "error getting user in bcastrecevier", e);
                    return;
                }
                String userWithBookmark = intent.getStringExtra("userBookmarked");
                Bookmark added = (Bookmark) intent.getSerializableExtra("bookmark");
                pClient.addBookmark(loggedIn, added);

            }

        }
    };

    public void onLogin(boolean dbInitied) throws Exception {
        loggedIn = this.userRepo.getLoggedInUser();
        //get initial players

        try {
            for (PortolPlatform userPlat : loggedIn.getPlatforms()) {
                if (userPlat.getActivePlayers() > 0) {
                    //then figure out what has already been connected
                    List<Player> active = pClient.getActivePlayersOn(userPlat);

                    playerRepo.saveAll(active);
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "error in getting existing players", e);
        }
        if (loggedIn != null) {
            if (loggedIn.getUserId() != null && client == null) {
                attachWSForUser();
            }

            //broadcast to all interested parties
            this.broadcastLogin();
//            broadcastUpdated();
        }


    }

    private void broadcastLogin() {
        System.out.println("User logged into Portol.");
        Intent intent = new Intent();
        intent.setAction("com.portol.Login");
        intent.putExtra("loggedIn", loggedIn);
        intent.putExtra("userId", loggedIn.getUserId());
        sendBroadcast(intent);


    }

    public void onLogout() {
        this.client.close();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public void onCreate() {

        app = (Portol) getApplication();
        pClient = new PortolClient(this);
        userRepo = app.getUserRepo();
        playerRepo = app.getPlayerRepo();

        IntentFilter filter = new IntentFilter();
        filter.addAction("com.portol.UserUpdate");
        filter.addAction("com.portol.Bookmarked");
        registerReceiver(receiver, filter);
    }

    private void attachWSForUser() throws Exception {

        final String queryString = "?id=" + this.loggedIn.getUserId();


        client = new WebSocketClient(new URI(this.wsuri + queryString), new Draft_17()) {
            @Override
            public void onOpen(ServerHandshake handshakedata) {
                Log.d(TAG, "Status: Connected to " + wsuri + queryString);
                client.send("hello");
            }

            @Override
            public void onMessage(String message) {
                Log.d(TAG, "Got echo: " + message);

                if (message.contains("pong") && message.length() < 10) {
                    Log.i(TAG, "pong received");
                    return;
                }

                Player updated = null;
                try {
                    updated = mapper.readValue(message, Player.class);
                } catch (Exception e) {
                    Log.e(TAG, "error parsing player object from ws, ignoring update", e);
                    return;

                }

                try {
                    if (updated.getStatus().equalsIgnoreCase(Player.DEAD)) {
                        playerRepo.remove(updated);

                    } else if (updated.getCurrentSourceIP() != null) {

                        Player existing = playerRepo.findPlayerWithId(updated.getPlayerId());

                        playerRepo.save(updated);

                        if (existing.getCurrentSourceIP() == null) {
                            //then this is a new event
                            ContentMetadata purchased = app.getContentRepo().findByParentContentKey(updated.getVideoKey());
                            //broadcastPurchased(updated, purchased);

                        }
                    } else {
                        playerRepo.save(updated);

                    }
                    broadcastUpdated(updated);

                } catch (Exception e) {
                    Log.e(TAG, "error saving player object from ws, ignoring update", e);
                    return;

                }


            }

            @Override
            public void onClose(int code, String reason, boolean remote) {
                Log.d(TAG, "Connection lost.");
            }

            @Override
            public void onError(Exception ex) {

            }
        };


        SSLContext sslContext = SSLContext.getInstance("TLS");
        sslContext.init(null, null, null);

        SSLSocketFactory factory = sslContext.getSocketFactory();// (SSLSocketFactory) SSLSocketFactory.getDefault();

        client.setSocket(factory.createSocket());


        client.connect();
        synchronizer.start();
    }

    private void broadcastPurchased(Player buying, ContentMetadata purchased) {
        Intent intent2 = new Intent();
        intent2.setAction("com.portol.Purchase");
        intent2.putExtra("playerId", buying.getPlayerId());
        intent2.putExtra("itemPurchased", purchased.getParentContentId());

        sendBroadcast(intent2);
    }

    private void broadcastUpdated(Player updated) {
        Intent intent = new Intent();
        intent.setAction("com.portol.PlayerUpdate");
        intent.putExtra("player", updated);

        sendBroadcast(intent);
    }

    public void updateUserInfo() throws Exception {

        User loggedIn = this.getCurrentUser();
        if (loggedIn == null) {
            Log.d(TAG, "no user logged in, skipping user info update");
            return;
        }

        User updated = null;
        if (loggedIn.getCurrentToken() != null) {
            updated = pClient.refreshUser(loggedIn.getCurrentToken(), loggedIn);
            this.userRepo.save(updated);
        }
    }

    public boolean isUserLoggedIn() throws Exception {
        if (userRepo.getLoggedInUser() == null) {
            return false;
        } else return true;
    }

    public User getCurrentUser() throws Exception {
        return userRepo.getLoggedInUser();
    }

    @Override
    public void onDestroy() {
        unregisterReceiver(receiver);
    }

    public class LocalBinder extends Binder {
        public UserService getUpdaterInstance() {
            return UserService.this;
        }
    }

}
