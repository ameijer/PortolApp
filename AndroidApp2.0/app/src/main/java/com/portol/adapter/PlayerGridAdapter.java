package com.portol.adapter;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.portol.R;
import com.portol.common.model.PortolPlatform;
import com.portol.common.model.content.ContentMetadata;
import com.portol.common.model.player.Player;
import com.portol.repository.ContentRepository;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by Aidan on 7/4/2015.
 * <p>
 * This will do the same thing as the ContentListAdapter, except there will also be a cool picture.
 * Probably other stuff too.
 */
public class PlayerGridAdapter extends BaseAdapter {

    public static final String TAG = "PlayerGridAdapter";
    private final ContentRepository contentRepo;
    Context context;
    private List<Player> players;
    private LayoutInflater layoutInflator;

    public PlayerGridAdapter(Context context, List<Player> players, ContentRepository contentRepo) {
        this.players = players;
        this.context = context;
        this.contentRepo = contentRepo;
        layoutInflator = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public List<Player> getPlayers() {
        return players;
    }

    @Override
    public int getCount() {
        return players.size();
    }

    @Override
    public Object getItem(int position) {
        return players.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    /**
     * Get a View that displays the data at the specified position in the data set. You can either
     * create a View manually or inflate it from an XML layout file. When the View is inflated, the
     * parent View (GridView, ListView...) will apply default layout parameters unless you use
     * {@link LayoutInflater#inflate(int, ViewGroup, boolean)}
     * to specify a root view and to prevent attachment to the root.
     *
     * @param position    The position of the item within the adapter's data set of the item whose view
     *                    we want.
     * @param convertView The old view to reuse, if possible. Note: You should check that this view
     *                    is non-null and of an appropriate type before using. If it is not possible to convert
     *                    this view to display the correct data, this method can create a new view.
     *                    Heterogeneous lists can specify their number of view types, so that this View is
     *                    always of the right type (see {@link #getViewTypeCount()} and
     *                    {@link #getItemViewType(int)}).
     * @param parent      The parent that this view will eventually be attached to
     * @return A View corresponding to the data at the specified position.
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        int i = 0;
        Player hold = this.players.get(position);

        PlayerHolder viewHolder;
        if (convertView == null) {
            convertView = layoutInflator.inflate(R.layout.portol_player_card, parent, false);
            viewHolder = new PlayerHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (PlayerHolder) convertView.getTag();
        }

        int color = Color.WHITE;

        try {
            color = Color.parseColor("#" + hold.getHostPlatform().getPlatformColor());
        } catch (Exception e) {
            Log.i(TAG, "Invalid color for player found, filling with white");
        }

        viewHolder.card.setCardBackgroundColor(color);


        viewHolder.playerId.setText("Player: " + hold.getPlayerId().substring(0, 5));

        ContentMetadata matching = contentRepo.findByParentContentKey(hold.getVideoKey());
        DownloadImageTask downloader = new DownloadImageTask(viewHolder.cover);
        downloader.execute(matching.getSplashURL());

        return convertView;
    }


}
