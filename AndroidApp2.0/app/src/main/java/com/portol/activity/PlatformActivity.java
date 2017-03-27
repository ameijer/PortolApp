package com.portol.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.widget.Toast;

import com.portol.Portol;
import com.portol.R;
import com.portol.client.PortolClient;
import com.portol.common.model.PortolPlatform;
import com.portol.common.model.app.AppConnectResponse;
import com.portol.common.model.bookmark.Bookmark;
import com.portol.common.model.content.ContentMetadata;
import com.portol.common.model.player.Player;
import com.portol.common.model.player.PlayerGetRequest;
import com.portol.common.model.user.User;
import com.portol.fragment.AddDeviceConfirmFragment;
import com.portol.fragment.PlayerOptionsFragment;
import com.portol.fragment.PlayerSelectFragment;
import com.portol.fragment.clickr.PairingChoiceFragment;
import com.portol.fragment.clickr.ScannerFragment;
import com.portol.fragment.clickr.TextPairFragment;
import com.portol.repository.ContentRepository;
import com.portol.repository.PlayerRepository;
import com.portol.repository.UserRepository;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import me.dm7.barcodescanner.zbar.Result;


/**
 * Created by alex on 9/1/15.
 */
public class PlatformActivity extends FragmentActivity implements PlayerSelectFragment.OnPlayerSelectedListener, PlayerOptionsFragment.OnPlayerOptionSelectListener {

    public static final String TAG = "PlatformActivity";
    protected FragmentManager mainFragmentManager;
    private PortolClient pClient;
    private PortolPlatform targetDevice;
    private Player pairedPlayer;
    private Portol app;
    private ContentRepository contentRepo;
    private UserRepository userRepo;
    private PlayerRepository playerRepo;
    private PlayerSelectFragment selectorFrag;
    private PlayerOptionsFragment optionsFrag;

    public Portol getApp() {
        return app;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle b = getIntent().getExtras();
        targetDevice = (PortolPlatform) b.getSerializable("platform");

        if (targetDevice == null) {
            Log.e(TAG, "No platform supplied to player select activity");
            Toast toast = Toast.makeText(this, "Invalid device", Toast.LENGTH_LONG);
            toast.show();
            finish();
            return;
        }

        getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        this.app = ((Portol) getApplication());
        this.contentRepo = ((Portol) getApplication()).getContentRepo();
        this.userRepo = ((Portol) getApplication()).getUserRepo();
        this.playerRepo = ((Portol) getApplication()).getPlayerRepo();
        pClient = new PortolClient(this);
        mainFragmentManager = getSupportFragmentManager();

        setContentView(R.layout.device_dialog);
        List<Player> onPlatform = new ArrayList<Player>();
        try {
            onPlatform = playerRepo.getCurrentPlayers(targetDevice);
        } catch (Exception e) {
            Log.e(TAG, "error retreiving players on platform");
        }

        Fragment initialFrag = null;
        if (onPlatform.size() > 1) {
            //then go to a player select fragment
            selectorFrag = PlayerSelectFragment.newInstance(targetDevice.getPlatformId());
            selectorFrag.setListener(this);
            initialFrag = selectorFrag;
        } else {
            //go directly to the player options
            optionsFrag = PlayerOptionsFragment.newInstance(onPlatform.get(0).getPlayerId());
            optionsFrag.setmCallback(this);
            initialFrag = optionsFrag;
        }


        //initially, start with the choice fragment
        FragmentTransaction fragTransaction = mainFragmentManager.beginTransaction();

        fragTransaction.add(R.id.dialog_root, initialFrag);
        fragTransaction.commit();

    }

    private void quitDialog(String playerId, int position, String platformId) {
        Intent returnIntent = new Intent();
        returnIntent.putExtra("playerId", playerId);
        returnIntent.putExtra("platformId", platformId);
        returnIntent.putExtra("position", position);
        //returnIntent.putExtra("platform", availableDevices.get(position));
        setResult(RESULT_OK, returnIntent);
        finish();
    }


    public void backClicked() {
        Intent returnIntent = new Intent();
        setResult(RESULT_CANCELED, returnIntent);
        finish();
    }

    public void popBackstack() {
        mainFragmentManager.popBackStack();
    }


    private void quitDialog(boolean purchased, String contentPurchased, String playerId, Player paired, AppConnectResponse resp) {
        Intent returnIntent = new Intent();
        returnIntent.putExtra("playerId", playerId);
        returnIntent.putExtra("contentId", contentPurchased);
        returnIntent.putExtra("purchased", purchased);


        if (paired != null) {
            returnIntent.putExtra("paired", paired);
        }

        if (resp != null) {
            returnIntent.putExtra("AppConnectResponse", resp);
        }


        setResult(RESULT_OK, returnIntent);
        finish();
    }

    @Override
    public void onPlayerSelected(String playerId) {
        FragmentTransaction replace = null;

        this.optionsFrag = PlayerOptionsFragment.newInstance(playerId);

        replace = mainFragmentManager.beginTransaction();

        replace.replace(R.id.dialog_root, this.optionsFrag);
        replace.addToBackStack(null);
        replace.commit();

    }

    @Override
    public void onPlayerSelectCancel() {
        this.backClicked();
    }

    @Override
    public void onItemFocus(String contentId) {
        //broadcast this event to the app
        Intent intent = new Intent();
        intent.setAction("com.portol.ItemFocus");
        intent.putExtra("contentId", contentId);
        sendBroadcast(intent);

        Intent returnIntent = new Intent();

        setResult(RESULT_OK, returnIntent);

        finish();

    }

    @Override
    public void onBuy(Player added, String contentId) {
        Log.i(TAG, "buying video");
        User loggedin = null;
        try {
            loggedin = userRepo.getLoggedInUser();
        } catch (Exception e) {
            Log.e(TAG, "error getting logged in user for buy", e);
        }

        if (loggedin == null) {
            Log.e(TAG, "NO LOGGED IN USER FOUND");
            Toast toast = Toast.makeText(this, "no logged in user found", Toast.LENGTH_LONG);
            toast.show();
        }

        ContentMetadata matching = contentRepo.findByParentContentId(contentId);
        //buy the content
        AppConnectResponse resp = null;
        try {
            resp = pClient.makePaymentForPlayer(loggedin, added, matching);
        } catch (Exception e) {
            Log.e(TAG, "error making payment onbuyandadd", e);
        }

        if (resp == null) {
            Toast.makeText(this, "purchase failed!", Toast.LENGTH_LONG).show();

            return;
        }


        //update user
        try {
            User mostRecent = pClient.refreshUser(loggedin.getCurrentToken(), loggedin);
            userRepo.save(mostRecent);
        } catch (Exception e) {
            Log.e(TAG, "error updating user onbuyandadd", e);
        }

        //update player

        Player mostRecent = null;
        try {
            mostRecent = pClient.getUpdatedPlayer(added.getPlayerId());


            mostRecent.setCurrentSourceIP(resp.getSource());
            playerRepo.save(mostRecent);
        } catch (Exception e) {
            Log.e(TAG, "error updating player onbuyandadd", e);
        }

        Intent intent2 = new Intent();
        intent2.setAction("com.portol.Purchase");
        intent2.putExtra("playerId", added.getPlayerId());
        intent2.putExtra("itemPurchased", contentId);
        intent2.putExtra("AppConnectResponse", resp);
        sendBroadcast(intent2);

        //ESAD
        quitDialog(true, contentId, added.getPlayerId(), mostRecent, resp);
    }

    @Override
    public void onFavoriteRequest(Player infoRequested, String contentId) {
        Log.i(TAG, "favorting content: " + infoRequested.getVideoKey());
        User loggedin = null;
        try {
            loggedin = userRepo.getLoggedInUser();
        } catch (Exception e) {
            Log.e(TAG, "error getting logged in user for buy", e);
        }


        Bookmark newMark = new Bookmark();
        newMark.setDateBookmarked(new Date(System.currentTimeMillis()));
        newMark.setBookmarkedContentId(contentId);
        newMark.setReferrerId(infoRequested.getReferrerId().toString());


        List<Bookmark> existing = loggedin.getBookmarked();

        if (existing == null) {
            existing = new ArrayList<Bookmark>();
        } else {

            for (int i = 0; i < existing.size(); i++) {
                if (existing.get(i).getBookmarkedContentId().equalsIgnoreCase(contentId)) {
                    Toast toast = Toast.makeText(this, "already bookmarked this content", Toast.LENGTH_LONG);
                    toast.show();
                    return;
                }
            }

        }

        existing.add(newMark);

        loggedin.setBookmarked(existing);
        try {
            userRepo.save(loggedin);
        } catch (Exception e) {
            Log.e(TAG, "error saving updated player after bookmarking", e);
            return;
        }

        Intent intent2 = new Intent();
        intent2.setAction("com.portol.Bookmarked");
        intent2.putExtra("userBookmarked", loggedin.getUserId());
        intent2.putExtra("numBookmarks", existing.size());
        intent2.putExtra("bookmark", newMark);

        sendBroadcast(intent2);

    }
}
