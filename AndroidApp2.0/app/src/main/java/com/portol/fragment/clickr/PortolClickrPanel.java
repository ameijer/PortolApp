package com.portol.fragment.clickr;

import android.app.Activity;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;

import com.portol.activity.MainActivity;
import com.portol.client.PortolClient;
import com.portol.client.PortolPlayerClient;
import com.portol.common.model.app.AppConnectResponse;
import com.portol.common.model.player.Player;
import com.portol.fragment.PortolPanel;
import com.portol.fragment.user.LoginOrRegisterFragment;

import java.net.URISyntaxException;
import java.security.NoSuchAlgorithmException;

/**
 * Created by Aidan on 7/3/15.
 */
public class PortolClickrPanel extends PortolPanel implements OnMediaControllerListener {
    //    @Override
//    public void onQrDetection(Result result) {
//        Log.d("portol", "Data scanned: " + result.getContents()); // Prints scan results
//        Log.d("portol", result.getBarcodeFormat().getName()); // Prints the scan format (qrcode, pdf417 etc.)
//
//        //TODO: startPaymentActivity uncomment
//        String qrcontents= result.getContents();
//        String btcaddr = qrcontents.substring(qrcontents.indexOf(":") + 1, qrcontents.indexOf("?"));
//
//        //TODO dialog box confirming buy
//        onBuyConfirm(btcaddr);
//
//
//
//    }
    public boolean connected;
    AppConnectResponse sourceResp;
    boolean isMaximized = true;
    private MainActivity mainHolder;
    private boolean isMinimized = false;
    private DevicesFragment mPairingOptionsFragment = new DevicesFragment();
    //    private ScannerFragment mScannerFragment = new ScannerFragment();
    private SmallControllerFragment mSmallControllerFragment;
    private LargeControllerFragment mLargeControllerFragment;
    //    private TextPairFragment mTextPairFragment = new TextPairFragment();
    private PlatformGridFragment mPlatformGridFragment = new PlatformGridFragment();
    private LoginOrRegisterFragment mLoginOrRegister = new LoginOrRegisterFragment();
    private PortolClient pClient;
    //private Fragment clickrDefaultFragment = mPairingOptionsFragment;
    private Fragment clickrDefaultFragment = mPairingOptionsFragment;
    private PortolPlayerClient clickrClient;

    public PortolClickrPanel(int layoutContainer, FragmentManager mainFragmentManager, Activity holdingActivity) {
        super(layoutContainer, mainFragmentManager, holdingActivity);
        //panelDefault declared as protected in PortolPanel
        panelDefault = clickrDefaultFragment;
        this.mainHolder = (MainActivity) holdingActivity;

        this.pClient = mainHolder.getpClient();
    }

    public MainActivity getMainHolder() {
        return mainHolder;
    }

    public boolean isMinimized() {
        return isMinimized;
    }

    public PortolPlayerClient getClickrClient() {
        return clickrClient;
    }

    public void displayPairing() {


        this.setNextFragment(mPairingOptionsFragment);


        Runnable expander = new Runnable() {


            public void run() {
                swapCurrentToNextFragment();


                float freed = expandTo(15.0F);
                mainHolder.expandContentBy(freed);
            }

        };
        mainHolder.runOnUiThread(expander);

    }

    public void revertToPairingOptions() {
        this.setNextFragment(this.mPairingOptionsFragment);
        this.swapCurrentToNextFragment();
    }

    @Override
    public void onPortolPause() {
        //doPause
        Log.d("portol (panel)", "Portol pause event seen.");
        if (clickrClient != null) {
            clickrClient.sendPause();
        }
    }

    @Override
    public void onPortolPlay() {
        //doPlay
        Log.d("portol (panel)", "Portol play event seen.");
        if (clickrClient != null) {
            clickrClient.sendPlay();
        }
    }

//    @Override
//    public void onStartQRScan(){
//        this.setNextFragment(mScannerFragment);
//        this.swapCurrentToNextFragment();
//    }
//
//    @Override
//    public void onTextPair(){
//        this.setNextFragment(mTextPairFragment);
//        this.swapCurrentToNextFragment();
//    }

    @Override
    public void onPortolStop() {
        //doStop
        Log.d("portol (panel)", "Portol stop event seen.");
        if (clickrClient != null) {
            clickrClient.sendStop();
        }
    }

    @Override
    public void onPortolLeft() {
        //goLeft
        Log.d("portol (panel)", "Portol go left event seen.");
        if (clickrClient != null) {
            clickrClient.sendSeekBack();
        }
    }


//    Thread switcher = new Thread() {
//        @Override
//    public void run(){
//            try {
//                Thread.sleep(5000);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//            displayPairing(true);
//        }
//    };
//    public PortolPlayerClient getClickrClient(){
//        if(this.connected){
//            return this.clickrClient;
//        } else return null;
//
//
//    }

    @Override
    public void onPortolRight() {
        //goLeft
        Log.d("portol (panel)", "Portol go right event seen.");
        if (clickrClient != null) {
            clickrClient.sendSeekFwd();
        }
    }

    private void establishClickrConnection(AppConnectResponse connectInfo) {

        try {
            this.clickrClient = new PortolPlayerClient(connectInfo.getSource(), connectInfo.getPlayerID());
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

        if (this.clickrClient.isConnected()) {
            this.connected = true;
            clickrClient.setPlayerActivityListener(mLargeControllerFragment);
        } else {
            this.connected = false;
        }
    }


//    @Override
//    public void onTextPairAttempt(Result playerCode){
//        Log.d("portol", "code entered: " + playerCode.getContents()); // Prints scan results
//
//        String miniCode = playerCode.getContents();
//
//        if(miniCode.length() < 5){
//            Toast.makeText(this.getParentContext(), "Invalid code", Toast.LENGTH_SHORT).show();
//            return;
//        }
//
//        this.mTextPairFragment.clearEditText();
//
//        this.onBuyConfirm(miniCode);
//
//    }


//    public void onBuyConfirm(Player paired, ContentMetadata purchased) {
//
//        //TODO: doPayment needs to crossover to the account thread, I believe.
//
//
//        pClient.makePaymentForPlayer(mainHolder.getCurrentUser(), paired, purchased);
//
//        //startPaymentActivity(result.getContents());
//
//        this.clickrClient = pCrefreshPanellient.gestablishClickrConnectionetClickrClient();
//
//        //TODO: Waiting for a pair screen. For now, let's just go back to the default fragment
//        this.setNextFragment(mControllerFragment);
//        this.swapCurrentToNextFragment();
//    }

    public void onBuyConfirm(AppConnectResponse resp) {
        //TODO: doPayment needs to crossover to the account thread, I believe.


        //pClient.makePaymentForCode(mainHolder.getCurrentUser(), playerCode);

        //startPaymentActivity(result.getContents());

        this.sourceResp = resp;
        this.establishClickrConnection(resp);
        if (mLargeControllerFragment == null) {
            mLargeControllerFragment = LargeControllerFragment.newInstance(resp.getPlayerID());
        }
        //TODO: Waiting for a pair screen. For now, let's just go back to the default fragment
        this.setNextFragment(mLargeControllerFragment);

    }

    public float minimizeToggle() {
        if (isMinimized) {
            isMinimized = false;
            return this.contractTo(2.4F);
        } else {
            isMinimized = true;
            return this.contractTo(15.0F);
        }

    }

    public void displayLogin() {
        this.setNextFragment(mLoginOrRegister);
        this.swapCurrentToNextFragment();
    }

    public void displayLargeClickr(String focusedPlayerId) {


        if (mLargeControllerFragment == null) {
            mLargeControllerFragment = LargeControllerFragment.newInstance(focusedPlayerId);
        }


        this.setNextFragment(mLargeControllerFragment);
        this.swapCurrentToNextFragment();

        float freed = expandTo(100.0F);
        mainHolder.expandContentBy(freed);

    }

    public void displaySmallClickr() {


        if (mSmallControllerFragment == null) {
            mSmallControllerFragment = new SmallControllerFragment();
        }


        this.setNextFragment(mSmallControllerFragment);


        Runnable expander = new Runnable() {


            public void run() {
                swapCurrentToNextFragment();
                float freed = expandTo(25.0F);
                mainHolder.expandContentBy(freed);
            }

        };
        mainHolder.runOnUiThread(expander);

    }

    public float maximizeToggle() {

        if (isMaximized) {
            isMaximized = false;


            return this.contractTo(25.0F);
        } else {
            isMaximized = true;
            return this.expandTo(100.0F);

        }
    }

    public void onBuyConfirm(String currentSourceIP, String playerId) {
        AppConnectResponse resp = new AppConnectResponse();
        resp.setSource(currentSourceIP);
        resp.setPlayerID(playerId);

        sourceResp = resp;
        this.onBuyConfirm(sourceResp);

    }

    public void displaySmallClickr(Player toFocusOn) {
        AppConnectResponse resp = new AppConnectResponse();
        resp.setSource(toFocusOn.getCurrentSourceIP());
        resp.setPlayerID(toFocusOn.getPlayerId());
        this.displaySmallClickr();

        sourceResp = resp;
        this.onBuyConfirm(sourceResp);
    }
}