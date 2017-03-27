package com.portol.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import com.portol.R;
import com.portol.activity.PlatformActivity;
import com.portol.adapter.ContentListAdapter;
import com.portol.adapter.PlatformGridAdapter;
import com.portol.adapter.PlayerGridAdapter;
import com.portol.common.model.Category;
import com.portol.common.model.PortolPlatform;
import com.portol.common.model.app.AppConnectResponse;
import com.portol.common.model.content.ContentMetadata;
import com.portol.common.model.player.Player;
import com.portol.fragment.content.GridItemClickedInterface;
import com.portol.repository.ContentRepository;
import com.portol.repository.PlayerRepository;
import com.portol.repository.UserRepository;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by alex on 9/1/15.
 */
public class PlayerSelectFragment extends Fragment implements AdapterView.OnItemClickListener {

    public static final String TAG = "PlayerSelectFragment";
    //get devices
    List<Player> players = new ArrayList<Player>();
    private GridView grid;
    private ContentRepository contentRepo;
    private PlayerRepository playerRepo;
    //keep a copy for local reference
//    private GridItemClickedInterface clickedInterface;
    private PlayerGridAdapter adapt;
    private String focusedPlatId;
    private OnPlayerSelectedListener mCallback;

    public static PlayerSelectFragment newInstance(String platId) {
        Bundle args = new Bundle();
        args.putString("platId", platId);
        PlayerSelectFragment fragment = new PlayerSelectFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public void setListener(OnPlayerSelectedListener mCallback) {
        this.mCallback = mCallback;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);


    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle b = getArguments();
        if (b != null) {
            this.focusedPlatId = b.getString("platId");

        }
        this.contentRepo = ((PlatformActivity) getActivity()).getApp().getContentRepo();
        this.playerRepo = ((PlatformActivity) getActivity()).getApp().getPlayerRepo();

        Log.i(TAG, "finished attaching repos");

        try {
            players = playerRepo.getPlatformPlayers(focusedPlatId);
        } catch (Exception e) {
            Log.e(TAG, "error accessing players for focused paltform", e);
        }

        adapt = new PlayerGridAdapter(getContext(), players, contentRepo);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = super.onCreateView(inflater, container, savedInstanceState);
        View myView = inflater.inflate(R.layout.player_select_grid, container, false);

        this.grid = (GridView) myView.findViewById(R.id.player_grid);
        this.grid.setAdapter(adapt);
        grid.setNumColumns(1);
        grid.setOnItemClickListener(this);

        return myView;
    }

    public void backClicked(View view) {
        if (mCallback != null) {
            this.mCallback.onPlayerSelectCancel();
        }
    }
//
//    private void setClickedInterface(GridItemClickedInterface clickedInterface) {
//        this.clickedInterface = clickedInterface;
//    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Player selected = adapt.getPlayers().get(position);

        Log.i(TAG, "player selected: " + selected.getPlayerId());

        if (mCallback != null) {
            this.mCallback.onPlayerSelected(selected.getPlayerId());
        }


    }


    public interface OnPlayerSelectedListener {
        public void onPlayerSelected(String playerId);

        public void onPlayerSelectCancel();
    }
}
