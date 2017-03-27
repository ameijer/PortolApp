package com.portol.fragment.clickr;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.games.Players;
import com.portol.R;
import com.portol.activity.AddDeviceActivity;
import com.portol.activity.MainActivity;

import com.portol.activity.PlatformActivity;
import com.portol.adapter.PlatformListAdapter;
import com.portol.common.model.PortolPlatform;
import com.portol.common.model.player.Player;
import com.portol.fragment.BaseFragment;
import com.portol.repository.PlayerRepository;
import com.portol.repository.UserRepository;

import org.lucasr.twowayview.ItemClickSupport;
import org.lucasr.twowayview.widget.TwoWayView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;


/**
 * Created by Aidan on 7/4/15.
 */
public class DevicesFragment extends BaseFragment {

    public static final String TAG = "DevicesFragment";
    public static final int DEVICE_REQUEST = 2;
    public static final int PLATFORM_REQUEST = 3;
    private static final String ARG_PARAM1 = "param1";
    boolean visible = false;
    MainActivity mainActivity;
    //private OnTryPairingListener mOnTryPairingListener;
    private String mParam1;
    private PortolClickrPanel mContainingPanel;
    private TwoWayView mHorizontalListView;
    private PlayerRepository playerRepo;
    //private ListView mListView;
    //private Button mScanButton;
    private ImageButton mNewDeviceButton;
    private Button minimize;
    private List<PortolPlatform> active = null;
    private PlatformListAdapter adapt;
    private final BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equalsIgnoreCase("com.portol.PlayerUpdate")) {
                if (visible) {
                    Player updated = (Player) intent.getSerializableExtra("player");

                    mainActivity.getApp().getPlayerRepo().removeDeadPlayers();
                    updatePlayers();
                }

            }
        }
    };

//    //TODO: Conceptual interface at the moment.
//    public interface OnTryPairingListener {
//        public void onStartQRScan();
//
//        public void onTextPair();
//    }
    private TextView mControlStatement;
    private UserRepository userRepo;
    private Toast mToast;
    public DevicesFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            visible = true;
            mainActivity = (MainActivity) activity;
            mContainingPanel = mainActivity.getClickrPanel();
            this.userRepo = mainActivity.getApp().getUserRepo();
            this.playerRepo = mainActivity.getApp().getPlayerRepo();
            //ContainingPanel implements the media controller interface.
            //   mOnTryPairingListener = mContainingPanel;
            if (active == null) {
                active = new ArrayList<PortolPlatform>();

            }
        } catch (ClassCastException e) {
            throw new ClassCastException(mContainingPanel.toString()
                    + " must implement OnMediaControllerListener");
        }


    }

    //TODO:
    // not actually a todo, but making a note here.
    // onCreateView apparently gets called when you use fragmentManager.add, so be aware of this.
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View mFragmentView = inflater.inflate(R.layout.device_list, container, false);

        // mScanButton = (Button) mFragmentView.findViewById(R.id.qrScan);
        mNewDeviceButton = (ImageButton) mFragmentView.findViewById(R.id.addDevice);
        minimize = (Button) mFragmentView.findViewById(R.id.minimzeButton);
        minimize.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //when play is clicked show stop button and hide play button
                if (mContainingPanel.isMinimized()) {
                    minimize.setText("^");
                } else {
                    minimize.setText("V");
                }
                float freed = mContainingPanel.minimizeToggle();
                mainActivity.expandContentBy(freed);
            }
        });


        mNewDeviceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), AddDeviceActivity.class);
                getActivity().startActivityForResult(intent, DEVICE_REQUEST);
            }
        });


        mControlStatement = (TextView) mFragmentView.findViewById(R.id.control_statement);


        this.mHorizontalListView = (TwoWayView) mFragmentView.findViewById(R.id.horizontalContentList);
        //  this.mListView = (ListView) mFragmentView.findViewById(R.id.hist_list);

        mToast = Toast.makeText(mainActivity, "", Toast.LENGTH_SHORT);
        mToast.setGravity(Gravity.CENTER, 0, 0);

        final ItemClickSupport itemClick = ItemClickSupport.addTo(mHorizontalListView);

        itemClick.setOnItemClickListener(new ItemClickSupport.OnItemClickListener() {
            @Override
            public void onItemClick(RecyclerView parent, View child, int position, long id) {

                PortolPlatform matching = active.get(position);
                String platformId = matching.getPlatformId();
                mToast.setText("Item clicked: " + platformId + " @ position: " + position);
                mToast.show();

                List<Player> activePlayers = playerRepo.getActivePlayersOnPlatform(matching);

                if (activePlayers == null || activePlayers.size() == 0) {
                    //Start platformActivity, since no players are running

                    Intent intent2 = new Intent(getActivity(), PlatformActivity.class);
                    Bundle b = new Bundle();
                    b.putSerializable("platform", matching);
                    intent2.putExtras(b);
                    getActivity().startActivityForResult(intent2, PLATFORM_REQUEST);
                } else {
                    //otherwise, snap to the clickr for the active player
                    if (activePlayers.size() > 1) {
                        mToast.setText("no support for multiple players, choosing one to display clickr");
                        mToast.show();
                        Log.e(TAG, "unable to support multiple active players!");
                    }

                    Player toFocusOn = activePlayers.get(0);

                    mContainingPanel.displaySmallClickr(toFocusOn);
                }


            }
        });

        try {
            mainActivity.getApp().getPlayerRepo().removeDeadPlayers();
            List<Player> activePlayers = mainActivity.getApp().getPlayerRepo().getAllPlayers();

            for (Player cur : activePlayers) {
                active.add(cur.getHostPlatform());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


        setAdapter();
        updatePlayers();
//        for(int i = 0; i < 5; i ++){
//            withHistory.add(new HistoryItem(UUID.randomUUID().toString()));
//        }


        // updater.start();
        //uncomment for vertical separator bar
//        final Drawable divider = getResources().getDrawable(R.drawable.divider);
//       mHorizontalListView.addItemDecoration(new DividerItemDecoration(divider));

        mHorizontalListView.setAdapter(adapt);
        return mFragmentView;
    }

//    Thread updater = new Thread(){
//        @Override
//        public void run(){
//            while(true) {
//                try {
//                    Thread.sleep(20000);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//
//                if(visible) {
//                    updatePlayers();
//                }
//            }
//        }
//    };


    public void setAdapter() {
        adapt = new PlatformListAdapter(this.getActivity(), active, mHorizontalListView, mainActivity.getApp().getPlayerRepo());
    }

    public void updatePlayers() {


        Runnable run = new Runnable() {
            public void run() {

                HashSet<String> exisiting = new HashSet<String>();
                active.clear();
                List<Player> activePlayers = mainActivity.getApp().getPlayerRepo().getAllPlayers();

                for (Player cur : activePlayers) {
                    if (cur.getHostPlatform() == null) return;
                    if (!exisiting.contains(cur.getHostPlatform().getPlatformId())) {
                        active.add(cur.getHostPlatform());
                        exisiting.add(cur.getHostPlatform().getPlatformId());
                    }
                }

                adapt.notifyDataSetChanged();

            }
        };
        this.getActivity().runOnUiThread(run);
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
        }

    }

    @Override
    public void onDetach() {
        super.onDetach();
        visible = false;
        //mOnTryPairingListener = null;

    }

    @Override
    public void onResume() {
        super.onResume();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("com.portol.PlayerUpdate");
        getActivity().registerReceiver(receiver, intentFilter);
        //Do something everytime the activity regains focus
    }

//    private void doStartQRScan(){
//        mOnTryPairingListener.onStartQRScan();
//    }
//
//    private void doTextPair(){
//        mOnTryPairingListener.onTextPair();
//    }
}
