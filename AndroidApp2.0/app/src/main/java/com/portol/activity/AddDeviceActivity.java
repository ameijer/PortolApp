package com.portol.activity;

import android.content.Intent;
import android.os.Bundle;
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
import com.portol.common.model.content.ContentMetadata;
import com.portol.common.model.player.Player;
import com.portol.common.model.player.PlayerGetRequest;
import com.portol.common.model.user.User;
import com.portol.fragment.AddDeviceConfirmFragment;
import com.portol.fragment.clickr.PairingChoiceFragment;
import com.portol.fragment.clickr.ScannerFragment;
import com.portol.fragment.clickr.TextPairFragment;
import com.portol.repository.ContentRepository;
import com.portol.repository.PlayerRepository;
import com.portol.repository.UserRepository;

import me.dm7.barcodescanner.zbar.Result;


/**
 * Created by alex on 9/1/15.
 */
public class AddDeviceActivity extends FragmentActivity implements PairingChoiceFragment.OnOptionSelectedListener, ScannerFragment.OnScannerDetectionListener, TextPairFragment.OnPairAttemptListener, AddDeviceConfirmFragment.OnConfirmationListener {

    public static final String TAG = "AddDeviceActivity";
    protected FragmentManager mainFragmentManager;
    PairingChoiceFragment pairingChoiceFragment = new PairingChoiceFragment();
    ScannerFragment scannerFragment = new ScannerFragment();
    TextPairFragment textPair = new TextPairFragment();
    AddDeviceConfirmFragment confirmFrag;
    private PortolClient pClient;
    private PortolPlatform targetDevice;
    private Player pairedPlayer;
    private Toast mToast;
    //@Bind(R.id.scrollView)
    private ContentRepository contentRepo;
    private UserRepository userRepo;
    private PlayerRepository playerRepo;
    private Portol app;

    public Portol getApp() {
        return app;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        this.app = ((Portol) getApplication());
        this.contentRepo = ((Portol) getApplication()).getContentRepo();
        this.userRepo = ((Portol) getApplication()).getUserRepo();
        this.playerRepo = ((Portol) getApplication()).getPlayerRepo();
        pClient = new PortolClient(this);
        mainFragmentManager = getSupportFragmentManager();

        setContentView(R.layout.device_dialog);

        //initially, start with the choice fragment
        FragmentTransaction fragTransaction = mainFragmentManager.beginTransaction();


        fragTransaction.add(R.id.dialog_root, pairingChoiceFragment);
        fragTransaction.commit();

    }

    private void quitDialog(String playerId, int position, String platformId) {
        Intent returnIntent = new Intent();
        returnIntent.putExtra("playerId", playerId);
        returnIntent.putExtra("platformId", platformId);
        returnIntent.putExtra("position", position);
        setResult(RESULT_OK, returnIntent);
        finish();
    }


    public void backClicked(View view) {
        Intent returnIntent = new Intent();
        setResult(RESULT_CANCELED, returnIntent);
        finish();
    }

    public void popBackstack() {
        mainFragmentManager.popBackStack();
    }

    @Override
    public void onOptionSelected(int position) {
        //transition to the selected item
        FragmentTransaction replace = null;
        switch (position) {
            case PairingChoiceFragment.QR_POSIT:
                replace = mainFragmentManager.beginTransaction();

                replace.replace(R.id.dialog_root, scannerFragment);
                replace.addToBackStack(null);
                replace.commit();
                break;
            case PairingChoiceFragment.TEXT_POSIT:
                replace = mainFragmentManager.beginTransaction();

                replace.replace(R.id.dialog_root, textPair);
                replace.addToBackStack(null);
                replace.commit();
                break;

            default:
                Log.e(TAG, "Invalid button position");
                return;
        }

    }

    private void openDeviceConfirm(Player target) {
        confirmFrag = AddDeviceConfirmFragment.newInstance(target.getPlayerId());


        FragmentTransaction replace = null;
        replace = mainFragmentManager.beginTransaction();

        replace.replace(R.id.dialog_root, confirmFrag);
        replace.addToBackStack(null);
        replace.commit();
    }

    @Override
    public void onQrDetection(Result result) {

        Log.d("portol", "Data scanned: " + result.getContents()); // Prints scan results
        Log.d("portol", result.getBarcodeFormat().getName()); // Prints the scan format (qrcode, pdf417 etc.)

        String qrcontents = result.getContents();
        String btcaddr = qrcontents.substring(qrcontents.indexOf(":") + 1, qrcontents.indexOf("?"));

        Player pairing = null;
        try {
            pairing = pClient.getMatchingPlayer(btcaddr, PlayerGetRequest.QR_ID);
        } catch (InterruptedException e) {
            Log.e(TAG, "error getting player", e);
        }

        if (pairing == null) {
            mToast = Toast.makeText(this, "", Toast.LENGTH_LONG);
            mToast.setGravity(Gravity.CENTER, 0, 0);


            mToast.setText("error finding player matching QR code: " + result.getContents());
            mToast.show();
            return;
        }

        try {
            Player saved = playerRepo.save(pairing);
            if (saved == null) throw new Exception("player save error");
        } catch (Exception e) {
            Log.e(TAG, "Error saivng paired player", e);
            return;
        }
        openDeviceConfirm(pairing);

    }

    @Override
    public void onTextPairAttempt(Result textPair) {

        Player pairing = null;
        try {
            pairing = pClient.getMatchingPlayer(textPair.getContents(), PlayerGetRequest.TEXT_ID);
        } catch (InterruptedException e) {
            Log.e(TAG, "error getting player", e);
        }

        if (pairing == null) {
            mToast = Toast.makeText(this, "", Toast.LENGTH_LONG);
            mToast.setGravity(Gravity.CENTER, 0, 0);


            mToast.setText("Invalid player pairing code: " + textPair.getContents());
            mToast.show();
            return;
        }

        try {
            Player saved = playerRepo.save(pairing);
            if (saved == null) throw new Exception("player save error");
        } catch (Exception e) {
            Log.e(TAG, "Error saivng paired player", e);
            return;
        }


        openDeviceConfirm(pairing);
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
    public void onAddDeviceOnly(Player added) {
        Log.i(TAG, "adding device only");

        User loggedin = null;
        try {
            loggedin = userRepo.getLoggedInUser();
        } catch (Exception e) {
            Log.e(TAG, "error getting logged in user for buy", e);
        }

        if (loggedin == null) {
            Log.e(TAG, "NO LOGGED IN USER FOUND");
            mToast.setText("Error during update");
            mToast.show();
        }

        //add the player
        try {
            pClient.addPlayer(loggedin, added);
        } catch (Exception e) {
            Log.e(TAG, "error making payment onbuyandadd", e);
        }

        //update user
        try {
            User mostRecent = pClient.refreshUser(loggedin.getCurrentToken(), loggedin);
            userRepo.save(mostRecent);
        } catch (Exception e) {
            Log.e(TAG, "error updating user onbuyandadd", e);
        }

        try {
            Player mostRecent = pClient.getUpdatedPlayer(added.getPlayerId());
            playerRepo.save(mostRecent);
        } catch (Exception e) {
            Log.e(TAG, "error updating player onbuyandadd", e);
        }

        //broadcast this event to the app
        Toast toast = Toast.makeText(this, "Device added: " + added.getHostPlatform().getPlatformId(), Toast.LENGTH_LONG);
        toast.show();
        Intent intent = new Intent();
        intent.setAction("com.portol.DeviceAdd");
        intent.putExtra("playerId", added.getPlayerId());
        intent.putExtra("platformId", added.getHostPlatform().getPlatformId());
        sendBroadcast(intent);

        //ESAD
        quitDialog(false, null, added.getPlayerId(), null, null);


    }

    @Override
    public void onBuyAndAdd(Player added, String contentId) {
        Log.i(TAG, "buying video and adding device");
        User loggedin = null;
        try {
            loggedin = userRepo.getLoggedInUser();
        } catch (Exception e) {
            Log.e(TAG, "error getting logged in user for buy", e);
        }

        if (loggedin == null) {
            Log.e(TAG, "NO LOGGED IN USER FOUND");
            mToast.setText("Error during purchase");
            mToast.show();
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


        //broadcast this event to the app
        Intent intent = new Intent();
        intent.setAction("com.portol.DeviceAdd");
        intent.putExtra("playerId", added.getPlayerId());
        sendBroadcast(intent);

        Intent intent2 = new Intent();
        intent2.setAction("com.portol.Purchase");
        intent2.putExtra("playerId", added.getPlayerId());
        intent2.putExtra("itemPurchased", contentId);
        intent2.putExtra("AppConnectResponse", resp);
        sendBroadcast(intent2);

        //ESAD
        quitDialog(true, contentId, added.getPlayerId(), mostRecent, resp);

    }
}
