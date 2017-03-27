package com.portol.fragment.clickr;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import com.portol.Portol;
import com.portol.R;
import com.portol.adapter.PlatformGridAdapter;
import com.portol.client.PortolClient;
import com.portol.common.model.PortolPlatform;
import com.portol.common.model.player.Player;
import com.portol.fragment.BaseFragment;
import com.portol.fragment.content.GridItemClickedInterface;
import com.portol.repository.ContentRepository;
import com.portol.repository.PlayerRepository;
import com.portol.repository.UserRepository;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by alex on 9/1/15.
 */
public class PlatformGridFragment extends BaseFragment implements GridItemClickedInterface, AdapterView.OnItemClickListener {
    private final ArrayList<PortolPlatform> availablePlatforms = new ArrayList<PortolPlatform>();
    PlatformGridAdapter adapt;
    private PortolClient pClient;
    //@Bind(R.id.scrollView)
    private GridView grid;
    private ContentRepository contentRepo;
    private UserRepository userRepo;
    private View mFragmentView;
    private List<Player> players;
    private PlayerRepository playerRepo;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.contentRepo = ((Portol) getActivity().getApplication()).getContentRepo();
        this.userRepo = ((Portol) getActivity().getApplication()).getUserRepo();
        this.playerRepo = ((Portol) getActivity().getApplication()).getPlayerRepo();
        pClient = new PortolClient(getActivity());

        //get devices
        List<PortolPlatform> platforms = new ArrayList<PortolPlatform>();
        try {
            platforms = userRepo.getLoggedInUser().getPlatforms();
        } catch (Exception e) {
            e.printStackTrace();
        }

        players = playerRepo.getAllPlayers();

        availablePlatforms.addAll(platforms);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mFragmentView = inflater.inflate(R.layout.portol_grid, container, false);
        grid = (GridView) mFragmentView.findViewById(R.id.portol_grid);

        View headerView = inflater.inflate(R.layout.portol_platform_holder, null);
        adapt = new PlatformGridAdapter(getContext(), availablePlatforms);


        grid.setAdapter(adapt);
        grid.setNumColumns(3);
        //gridWithHeader.fillHeader(null);
        grid.setOnItemClickListener(this);
        return mFragmentView;
    }


    private void quitDialog(String playerId, int position, String platformId) {
        /*
        Intent returnIntent = new Intent();
        returnIntent.putExtra("playerId", playerId);
        returnIntent.putExtra("platformId", platformId);
        returnIntent.putExtra("position", position);
        returnIntent.putExtra("platform", availablePlatforms.get(position));
        setResult(RESULT_OK, returnIntent);
        finish();
        */
    }

    @Override
    public void gridClicked(String playerId, int position, String platformId) {
        //TODO progress bar that spins inside grid until buy complete
        this.quitDialog(playerId, position, platformId);
    }

    public void backClicked(View view) {
        /*
        Intent returnIntent = new Intent();
        setResult(RESULT_CANCELED, returnIntent);
        finish();
        */
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        PortolPlatform selected = adapt.getPlayers().get(position);
        String playerId = "";
        for (Player cur : players) {
            if (cur.getHostPlatform().getPlatformId().equals(selected.getPlatformId())) {
                playerId = cur.getPlayerId();
            }
        }
        this.quitDialog(playerId, position, selected.getPlatformId());
    }
}
