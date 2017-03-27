package com.portol.adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;


import com.portol.R;
import com.portol.common.model.PortolPlatform;
import com.portol.common.model.content.ContentMetadata;
import com.portol.common.model.player.Player;
import com.portol.repository.ContentRepository;
import com.portol.repository.PlayerRepository;

import org.lucasr.twowayview.widget.TwoWayView;

import java.util.HashSet;
import java.util.List;


/**
 * Created by alex on 9/8/15.
 */
public class PlatformListAdapter extends RecyclerView.Adapter<PlatformListAdapter.ViewHolder> {

    public static final String TAG = "HistoryAdapter";
    private final Context mContext;
    private final TwoWayView mRecyclerView;
    private LayoutInflater layoutInflator;
    private List<PortolPlatform> active;
    private PlayerRepository playerRepo;

    public PlatformListAdapter(Context context, List<PortolPlatform> realmResults, TwoWayView recyclerview, PlayerRepository repo) {
        this.mContext = context;
        this.playerRepo = repo;
        this.mRecyclerView = recyclerview;
        layoutInflator = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.active = realmResults;


    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        // create a new view
        View itemLayoutView = LayoutInflater.from(mContext)
                .inflate(R.layout.platform_tile, parent, false);

        ViewHolder viewHolder = new ViewHolder(itemLayoutView);
        return viewHolder;

    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        PortolPlatform plat = active.get(position);

//        if(item == null){
//            holder.name.setText("no content found");
//         //   holder.videoKey.setText(("no videokey found"));
//        } else {
//            holder.name.setText("Platform: " + plat.getPlatformName());
//          //  holder.videoKey.setText("Key: " + item.getVideoKey());
//        }

        //set icon based on platform type
        if (plat.getPlatformName().contains("Firefox/")) {
            holder.icon.setImageResource(R.drawable.firefox);
            holder.name.setText("Name: FireFox");
        } else if (plat.getPlatformName().contains("Chrome/")) {
            holder.icon.setImageResource(R.drawable.chrome);
            holder.name.setText("Name: Chrome");
        } else if (plat.getPlatformName().contains("Chromium/")) {
            holder.icon.setImageResource(R.drawable.chromium);
            holder.name.setText("Name: Chromium");
        } else if (plat.getPlatformName().contains("Safari/")) {
            holder.icon.setImageResource(R.drawable.safari);
            holder.name.setText("Name: Safari");
        } else if (plat.getPlatformName().contains(";MSIE")) {
            holder.icon.setImageResource(R.drawable.internetexplorer);
            holder.name.setText("Name: IE");
        } else {
            //generic browser image
            holder.icon.setImageResource(R.drawable.browser);
            holder.name.setText("Name: Browser");
        }

        int color = Color.WHITE;
        try {
            color = Color.parseColor("#" + plat.getPlatformColor());
        } catch (Exception e) {
            Log.e(TAG, "error coloring in history tile", e);
        }

        holder.card.setCardBackgroundColor(color);
        //viewHolder.title.setText(item.getViewedContentId());

        List<Player> players = playerRepo.getActivePlayersOnPlatform(plat);

        if (players == null || players.size() == 0) {

            holder.overlay.setVisibility(View.INVISIBLE);

        } else {
            //display a play sign
            holder.overlay.setVisibility(View.VISIBLE);
        }
        return;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return active.size();
    }

    public List<PortolPlatform> getRealmResults() {
        return active;
    }

    public static interface OnItemClickListener {
        public void onItemClick(ContentMetadata example);
    }

//    @Override
//    public View getView(int position, View convertView, ViewGroup parent) {
//        ViewHolder viewHolder;
//        if (convertView == null) {
//            convertView = layoutInflator.inflate(R.layout.platform_tile, parent, false);
//            viewHolder = new ViewHolder();
//
//           // viewHolder.title = (TextView) convertView.findViewById(android.R.id.text2);
//            convertView.setTag(viewHolder);
//        } else {
//            viewHolder = (ViewHolder) convertView.getTag();
//        }
//
//
//    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView name;
        public CardView card;
        public ImageView icon;
        public ImageView overlay;


        public ViewHolder(View itemView) {
            super(itemView);
            name = (TextView) itemView.findViewById(R.id.textbox);
            card = (CardView) itemView.findViewById(R.id.cardtile);
            icon = (ImageView) itemView.findViewById(R.id.cardImg);
            overlay = (ImageView) itemView.findViewById(R.id.overlay);
            //timestamp
        }
    }


}
