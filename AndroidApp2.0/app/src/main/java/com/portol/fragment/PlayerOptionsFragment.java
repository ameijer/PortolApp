package com.portol.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.portol.R;
import com.portol.activity.AddDeviceActivity;
import com.portol.activity.PlatformActivity;
import com.portol.adapter.DownloadImageTask;
import com.portol.common.model.content.ContentMetadata;
import com.portol.common.model.player.Player;
import com.portol.repository.ContentRepository;
import com.portol.repository.PlayerRepository;

/**
 * Created by alex on 9/28/15.
 */
public class PlayerOptionsFragment extends BaseFragment {
    //views
    TextView pairedPlayerInfo;
    ImageView cover;
    TextView title;
    TextView price;
    TextView info;
    Button vidInfo;
    Button buy;
    Button favorite;
    OnPlayerOptionSelectListener mCallback;
    private Player focused;
    private ContentMetadata playerMeta;
    private String focusedPlayerId;
    private ContentRepository contentRepo;
    private PlayerRepository playerRepo;

    public static PlayerOptionsFragment newInstance(String key) {
        Bundle args = new Bundle();
        args.putString("playerId", key);
        PlayerOptionsFragment fragment = new PlayerOptionsFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public void setmCallback(OnPlayerOptionSelectListener mCallback) {
        this.mCallback = mCallback;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception
        try {
            mCallback = (OnPlayerOptionSelectListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnPlayerOptionSelectListener");
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle b = getArguments();
        if (b != null) {
            this.focusedPlayerId = b.getString("playerId");

        }
        this.contentRepo = ((PlatformActivity) getActivity()).getApp().getContentRepo();
        this.playerRepo = ((PlatformActivity) getActivity()).getApp().getPlayerRepo();

        this.focused = playerRepo.findPlayerWithId(this.focusedPlayerId);
        this.playerMeta = contentRepo.findByParentContentKey(focused.getVideoKey());
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = super.onCreateView(inflater, container, savedInstanceState);
        View myView = inflater.inflate(R.layout.player_focus, container, false);

        this.pairedPlayerInfo = (TextView) myView.findViewById(R.id.focused_player_intro);
        this.title = (TextView) myView.findViewById(R.id.confirm_title);
        this.price = (TextView) myView.findViewById(R.id.confirm_price);
        this.info = (TextView) myView.findViewById(R.id.confirm_info);

        this.cover = (ImageView) myView.findViewById(R.id.confirm_image);


        this.vidInfo = (Button) myView.findViewById(R.id.player_focus_vidinfo);
        this.favorite = (Button) myView.findViewById(R.id.player_focus_favorite);
        this.buy = (Button) myView.findViewById(R.id.player_focus_purchase);

        vidInfo.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                mCallback.onItemFocus(playerMeta.getParentContentId());
            }
        });

        favorite.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                mCallback.onFavoriteRequest(focused, playerMeta.getParentContentId());
            }
        });

        buy.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                mCallback.onBuy(focused, playerMeta.getParentContentId());
            }
        });

        //fill the views
        this.pairedPlayerInfo.setText("Player " + focused.getPlayerId().substring(0, 5));
        this.title.setText(playerMeta.getChannelOrVideoTitle());
        this.price.setText("Price: " + playerMeta.getPrices().getShardPrice());
        this.info.setText(playerMeta.getInfo());

        DownloadImageTask downloader = new DownloadImageTask(this.cover);
        downloader.execute(this.playerMeta.getSplashURL());

        return myView;
    }


    // Container Activity must implement this interface
    public interface OnPlayerOptionSelectListener {
        public void onItemFocus(String contentId);

        public void onBuy(Player added, String contentId);

        public void onFavoriteRequest(Player infoRequested, String contentId);
    }

}
