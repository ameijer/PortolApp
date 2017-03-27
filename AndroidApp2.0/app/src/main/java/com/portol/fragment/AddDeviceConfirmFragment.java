package com.portol.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.github.florent37.hollyviewpager.ObservableGridView;
import com.portol.R;
import com.portol.activity.AddDeviceActivity;
import com.portol.activity.MainActivity;
import com.portol.activity.PortolGridActivity;
import com.portol.adapter.ContentListAdapter;
import com.portol.adapter.DownloadImageTask;
import com.portol.common.model.Category;
import com.portol.common.model.content.ContentMetadata;
import com.portol.common.model.player.Player;
import com.portol.repository.CategoriesRepository;
import com.portol.repository.ContentRepository;
import com.portol.repository.PlayerRepository;

import java.util.List;

/**
 * Created by alex on 9/28/15.
 */
public class AddDeviceConfirmFragment extends BaseFragment {
    //views
    TextView pairedPlayerInfo;
    ImageView cover;
    TextView title;
    TextView price;
    TextView info;
    Button addOnly;
    Button buyAndAdd;
    OnConfirmationListener mCallback;
    private String pairingPlayerId;
    private ContentRepository contentRepo;
    private CategoriesRepository catRepo;
    private PlayerRepository playerRepo;
    private Player focused;
    private ContentMetadata buying;


    //this fragment displays a small verson of the content on the paired player and has two options:
    //1. buy movie and add
    //2. add device

    public static AddDeviceConfirmFragment newInstance(String key) {
        Bundle args = new Bundle();
        args.putString("playerId", key);
        AddDeviceConfirmFragment fragment = new AddDeviceConfirmFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception
        try {
            mCallback = (OnConfirmationListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnConfirmationListener");
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle b = getArguments();
        if (b != null) {
            this.pairingPlayerId = b.getString("playerId");

        }
        this.contentRepo = ((AddDeviceActivity) getActivity()).getApp().getContentRepo();
        this.catRepo = ((AddDeviceActivity) getActivity()).getApp().getCategoriesRepo();
        this.playerRepo = ((AddDeviceActivity) getActivity()).getApp().getPlayerRepo();
        this.focused = playerRepo.findPlayerWithId(this.pairingPlayerId);
        this.buying = contentRepo.findByParentContentKey(focused.getVideoKey());
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = super.onCreateView(inflater, container, savedInstanceState);
        View myView = inflater.inflate(R.layout.add_device_confirm, container, false);

        this.pairedPlayerInfo = (TextView) myView.findViewById(R.id.paired_player_intro);
        this.title = (TextView) myView.findViewById(R.id.confirm_title);
        this.price = (TextView) myView.findViewById(R.id.confirm_price);
        this.info = (TextView) myView.findViewById(R.id.confirm_info);

        this.cover = (ImageView) myView.findViewById(R.id.confirm_image);


        this.buyAndAdd = (Button) myView.findViewById(R.id.confirm_buyandadd);

        buyAndAdd.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                mCallback.onBuyAndAdd(focused, buying.getParentContentId());
            }
        });

        this.addOnly = (Button) myView.findViewById(R.id.confirm_addonly);

        addOnly.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                mCallback.onAddDeviceOnly(focused);
            }
        });
        //fill the views
        this.pairedPlayerInfo.setText("Pairing: " + focused.getHostPlatform().getPlatformName());
        this.title.setText(buying.getChannelOrVideoTitle());
        this.price.setText("Price: " + buying.getPrices().getShardPrice());
        this.info.setText(buying.getInfo());

        DownloadImageTask downloader = new DownloadImageTask(this.cover);
        downloader.execute(this.buying.getSplashURL());


        return myView;
    }

    // Container Activity must implement this interface
    public interface OnConfirmationListener {
        public void onAddDeviceOnly(Player added);

        public void onBuyAndAdd(Player added, String content);
    }

}
