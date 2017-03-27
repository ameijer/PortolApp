package com.portol.client;

import android.content.Context;
import android.util.Log;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.portol.Installation;
import com.portol.common.model.Category;
import com.portol.common.model.PortolPlatform;
import com.portol.common.model.app.AppConnectResponse;
import com.portol.common.model.app.AppPaymentRequest;
import com.portol.common.model.app.XmitPortolToken;
import com.portol.common.model.bookmark.Bookmark;
import com.portol.common.model.content.ContentMetadata;
import com.portol.common.model.content.ContentSearchRequest;
import com.portol.common.model.player.Player;
import com.portol.common.model.player.PlayerGetRequest;
import com.portol.common.model.user.User;
import com.portol.repository.CategoriesRepository;
import com.portol.runnable.AllPlayerUpdateThread;
import com.portol.runnable.AppPayerThread;
import com.portol.runnable.PlayerAddThread;
import com.portol.runnable.PlayerGetterThread;
import com.portol.runnable.UserLoginThread;
import com.portol.runnable.UserRegisterThread;
import com.portol.runnable.UserRefreshThread;

import com.portol.common.model.CategorySearchRequest;

import com.portol.common.model.PortolToken;

import com.portol.runnable.CategoryGetterThread;
import com.portol.runnable.ContentGetterThread;
import com.portol.runnable.UserUpdateThread;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


/**
 * Created by alex on 6/16/15.
 */
public class PortolClient {


    public static final String TAG = "PortolClient";
    //use this to do app related things
    //holds jersey client for reqs
    String platId = null;

    public PortolClient(Context context) {
        platId = Installation.id(context);
    }

    public User loginUser(String uname, String pass, String email) throws JsonGenerationException, IOException, InterruptedException {

        PortolPlatform me = new PortolPlatform();
        me.setPlatformId(platId);
        me.setPaired(true);
        me.setPlatformType("Android");

        UserLoginThread login = new UserLoginThread(email, uname, pass, me);
        login.start();
        login.join();

        User currentUser = login.getCurrentlyViewing();
        if (currentUser == null) {
            //bad credentials most likely
            return null;
        }
        currentUser.setLoggedIn(true);

        return currentUser;
    }

    public User refreshUser(PortolToken valid, User registering) throws InterruptedException {
        UserRefreshThread updateThread;
        updateThread = new UserRefreshThread(valid, registering);

        updateThread.start();
        updateThread.join();

        User currentUser = updateThread.getUpdatedUser();
        if (currentUser == null) {
            //bad credentials most likely
            return null;
        }
        currentUser.setLoggedIn(true);

        return currentUser;

    }

    public User updateUser(User newState) {
        UserUpdateThread updateThread;
        updateThread = new UserUpdateThread(newState);

        updateThread.start();
        try {
            updateThread.join();
        } catch (InterruptedException e) {
            Log.e(TAG, "thread interrupted");
        }

        User currentUser = updateThread.getUpdatedUser();
        if (currentUser == null) {
            //bad credentials most likely
            return null;
        }
        currentUser.setLoggedIn(true);

        return currentUser;

    }

    public User registerUser(String uname, String pass, String email) throws JsonGenerationException, IOException, InterruptedException {

        UserRegisterThread register = new UserRegisterThread(email, uname, pass);
        register.start();
        register.join();

        User registeredUser = register.getRegisteredUser();

        return registeredUser;
    }

    public boolean makePaymentForCode(User user, String ident) {

        AppPaymentRequest req = new AppPaymentRequest();
        req.setUserID(user.getUserId());
        req.setPlayerIdentifier(ident);
        req.setValidToken(new XmitPortolToken(user.getCurrentToken()));

        AppConnectResponse response = null;

        try {
            response = submitAppPaymentRequest(req);
        } catch (Exception e) {
            Log.e("Portol", "Error when submitting payment request.");
        }

        if (response == null) {
            return false;
        }

        System.out.println("Content key of video being loaded: " + response.getPurchasedContent().getParentContentId());
        return true;
    }

    public AppConnectResponse makePaymentForPlayer(User user, Player buyingFor, ContentMetadata buying) {


        AppPaymentRequest req = new AppPaymentRequest();
        req.setUserID(user.getUserId());
        req.setPlayerIdentifier(buyingFor.getPlayerId());
        req.setPurchasedContentID(buying.getParentContentId());

        req.setValidToken(new XmitPortolToken(user.getCurrentToken()));

        AppConnectResponse response = null;

        try {
            response = submitAppPaymentRequest(req);
        } catch (Exception e) {
            Log.e("Portol", "Error when submitting payment request.");
        }

        if (response == null) {
            return null;
        }

        System.out.println("Content key of video being loaded: " + response.getPurchasedContent().getParentContentId());
        return response;
    }

    //TODO background thread that keeps clicker up to date

    private AppConnectResponse submitAppPaymentRequest(AppPaymentRequest req) throws JsonGenerationException, IOException, InterruptedException {
        AppPayerThread runner = new AppPayerThread(req);

        runner.start();
        runner.join();

        AppConnectResponse returned = runner.getConnectionInfo();
        Log.d("Portol", "Content returned:" + returned.getPurchasedContent().getId());
        Log.d("portol", "url of splash it has: " + returned.getPurchasedContent().getSplashURL());

        // establishClickrConnection(returned);

        return returned;
    }

    public ContentMetadata refreshSingleContentPiece(String contentID) {


        ContentSearchRequest req = new ContentSearchRequest();
        req.setType(ContentSearchRequest.RequestType.WITH_ID);
        ArrayList<String> toAdd = new ArrayList<String>();
        toAdd.add(contentID);
        req.setContentId(toAdd);
        ContentGetterThread contentGetter = new ContentGetterThread(req);
        ContentMetadata thisMeta = contentGetter.getReturnedContent().get(0);

        return thisMeta;

    }

    public List<ContentMetadata> refreshMultipleContentPieces(ArrayList<String> contentIDs) {
        ContentSearchRequest req = new ContentSearchRequest();
        req.setType(ContentSearchRequest.RequestType.WITH_ID);
        ArrayList<String> toAdd = new ArrayList<String>();
        toAdd.addAll(contentIDs);
        req.setContentId(toAdd);
        ContentGetterThread contentGetter = new ContentGetterThread(req);
        List<ContentMetadata> thisMeta = contentGetter.getReturnedContent();

        return thisMeta;
    }

    public List<ContentMetadata> keywordSearch(ArrayList<String> keywords) {

        //this is hard, lets do search later on
        return null;
    }

    public PortolPlayerClient establish_connection(String qrContents) {

        return null;
    }

    public List<ContentMetadata> getAllContent() throws InterruptedException {

        ContentSearchRequest req = new ContentSearchRequest();
        req.setPageIndex(-1);
        req.setType(ContentSearchRequest.RequestType.ALL);
        ContentGetterThread contentGetter = new ContentGetterThread(req);
        contentGetter.start();
        contentGetter.join();
        List<ContentMetadata> thisMeta = contentGetter.getReturnedContent();
        return thisMeta;

    }

    public List<Category> getAllCategories(CategoriesRepository catRepo) throws InterruptedException {
        CategorySearchRequest req = new CategorySearchRequest();
        CategoryGetterThread categoryGetter = new CategoryGetterThread(req);

        categoryGetter.start();
        categoryGetter.join();

        return categoryGetter.getReturnedCategories();
    }


    public boolean invalidateToken() {
        //TODO server side support
        return false;
    }

    public Player getMatchingPlayer(String pairingCode, String type) throws InterruptedException {
        PlayerGetRequest playerReq = new PlayerGetRequest(pairingCode);

        playerReq.setPairingType(type);

        PlayerGetterThread playerGetter = new PlayerGetterThread(playerReq);

        playerGetter.start();
        playerGetter.join();

        return playerGetter.getReturnedPlayer();


    }

    public Player getUpdatedPlayer(String playerId) throws InterruptedException {
        String type = PlayerGetRequest.PLAYER_ID;
        PlayerGetRequest playerReq = new PlayerGetRequest(playerId);

        playerReq.setPairingType(type);

        PlayerGetterThread playerGetter = new PlayerGetterThread(playerReq);

        playerGetter.start();
        playerGetter.join();

        return playerGetter.getReturnedPlayer();


    }

    public void addPlayer(User loggedin, Player added) throws InterruptedException {
        PlayerAddThread adder = new PlayerAddThread(added, loggedin);
        adder.start();
        adder.join();

        return;

    }

    public List<Player> getActivePlayersOn(PortolPlatform target) throws InterruptedException {
        AllPlayerUpdateThread updater = new AllPlayerUpdateThread(target.getPlatformId());
        updater.start();
        updater.join();

        return updater.getActivePlayers();

    }

    public void refreshUser(User loggedIn) {
    }

    public void addBookmark(User loggedIn, Bookmark toAdd) {


    }
}
