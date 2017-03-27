package com.portol.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import com.portol.Portol;
import com.portol.R;
import com.portol.adapter.PlatformGridAdapter;
import com.portol.client.PortolClient;
import com.portol.common.model.PortolPlatform;
import com.portol.common.model.app.AppConnectResponse;
import com.portol.common.model.content.ContentMetadata;
import com.portol.common.model.player.Player;
import com.portol.common.model.user.User;
import com.portol.fragment.content.GridItemClickedInterface;
import com.portol.repository.ContentRepository;
import com.portol.repository.PlayerRepository;
import com.portol.repository.UserRepository;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by alex on 9/1/15.
 */
public class PortolGridActivity extends Activity implements AdapterView.OnItemClickListener {

    public static final String TAG = "PortolGridActivity";
    private final ArrayList<PortolPlatform> availableDevices = new ArrayList<PortolPlatform>();
    //get devices
    List<Player> players = new ArrayList<Player>();
    PlatformGridAdapter adapt;
    private PortolClient pClient;
    //@Bind(R.id.scrollView)
    private GridView grid;
    private ContentRepository contentRepo;
    private UserRepository userRepo;
    // private final ArrayList<Player> availablePlayers = new ArrayList<Player>();
    private PlayerRepository playerRepo;
    private ContentMetadata buying;
    //keep a copy for local reference
    private GridItemClickedInterface clickedInterface;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle b = getIntent().getExtras();
        if (b != null) {
            this.buying = (ContentMetadata) b.getSerializable("buyingContent");

        }
        getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        this.contentRepo = ((Portol) getApplication()).getContentRepo();
        this.userRepo = ((Portol) getApplication()).getUserRepo();
        this.playerRepo = ((Portol) getApplication()).getPlayerRepo();
        pClient = new PortolClient(this);


        try {
            players = playerRepo.getAllPlayers();
        } catch (Exception e) {
            e.printStackTrace();
        }

        for (Player active : players) {
            availableDevices.add(active.getHostPlatform());
        }
        //availableDevices.addAll(players);
        //availableDevices.addAll(players);
        //Collections.sort(this.availableDevices);

        //this.gridWithHeader.setHeaderInfos(availableDevices);
        setContentView(R.layout.portol_grid);

        grid = (GridView) findViewById(R.id.portol_grid);

        // this.gridWithHeader.setHeaderInfos(availableDevices);

        //this.gridWithHeader.fillHeader(this.availableDevices);
        //LayoutInflater inflater = (LayoutInflater)getBaseContext().getSystemService(this.LAYOUT_INFLATER_SERVICE);
        LayoutInflater inflater = LayoutInflater.from(this);

//

//        this.gridWithHeader.setConfigurator(new HollyViewPagerConfigurator() {
//            @Override
//            public float getHeightPercentForPage(int page) {
//                return 1000f;
//            }
//        });


        View headerView = inflater.inflate(R.layout.portol_platform_holder, null);


        adapt = new PlatformGridAdapter(this, this.availableDevices);


//        this.gridWithHeader.setDeviceSelectInterface(this);
        this.grid.setAdapter(adapt);
        grid.setNumColumns(3);
        grid.setOnItemClickListener(this);
        //gridWithHeader.fillHeader(null);

    }

    public void setClickedInterface(GridItemClickedInterface clickedInterface) {
        this.clickedInterface = clickedInterface;
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

    public void backClicked(View view) {
        Intent returnIntent = new Intent();
        setResult(RESULT_CANCELED, returnIntent);
        finish();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        PortolPlatform selected = adapt.getPlayers().get(position);
        Player targetPlayer = null;
        for (Player cur : players) {
            if (cur.getHostPlatform().getPlatformId().equals(selected.getPlatformId())) {
                targetPlayer = cur;
            }
        }


        //Buy video
        Log.i(TAG, "buying video on device: " + targetPlayer.getPlayerId());
        User loggedin = null;
        try {
            loggedin = userRepo.getLoggedInUser();
        } catch (Exception e) {
            Log.e(TAG, "error getting logged in user for buy", e);
        }

        if (loggedin == null) {
            Log.e(TAG, "NO LOGGED IN USER FOUND");
            Toast toast = Toast.makeText(this, "Error during purchase", Toast.LENGTH_LONG);
            toast.show();
        }

        //buy the content
        AppConnectResponse resp = null;
        try {
            resp = pClient.makePaymentForPlayer(loggedin, targetPlayer, buying);
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
            mostRecent = pClient.getUpdatedPlayer(targetPlayer.getPlayerId());


            mostRecent.setCurrentSourceIP(resp.getSource());
            playerRepo.save(mostRecent);
        } catch (Exception e) {
            Log.e(TAG, "error updating player onbuyandadd", e);
        }
//
//        Intent intent2 = new Intent();
//        intent2.setAction("com.portol.Purchase");
//        intent2.putExtra("playerId", targetPlayer.getPlayerId());
//        intent2.putExtra("itemPurchased", buying.getParentContentId());
//        intent2.putExtra("AppConnectResponse", resp);
//        sendBroadcast(intent2);
        //ESAD
        quitDialog(true, buying.getParentContentId(), targetPlayer.getPlayerId(), mostRecent, resp);


    }
}
