package com.portol.adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;


import com.portol.R;

import com.portol.common.model.PortolPlatform;
import com.portol.common.model.player.Player;
import com.portol.dataobject.PlayerViewHolder;
import com.portol.fragment.content.GridItemClickedInterface;

import java.util.ArrayList;


/**
 * Created by Aidan on 7/4/2015.
 * <p>
 * This will do the same thing as the ContentListAdapter, except there will also be a cool picture.
 * Probably other stuff too.
 */
public class PlatformGridAdapter extends BaseAdapter {


    public static final String TAG = "gridAdapter";
    Context context;
    private ArrayList<PortolPlatform> players;
    private LayoutInflater layoutInflator;


    public PlatformGridAdapter(Context context, ArrayList<PortolPlatform> players) {
        this.players = players;
        this.context = context;
        layoutInflator = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public ArrayList<PortolPlatform> getPlayers() {
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
        PortolPlatform hold = this.players.get(position);

        DeviceHolder viewHolder;
        if (convertView == null) {
            convertView = layoutInflator.inflate(R.layout.portol_device_card, parent, false);
            viewHolder = new DeviceHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (DeviceHolder) convertView.getTag();
        }

        int color = Color.WHITE;

        try {
            color = Color.parseColor("#" + hold.getPlatformColor());
        } catch (Exception e) {
            Log.i(TAG, "Invalid color for player found, filling with white");
        }

        viewHolder.card.setCardBackgroundColor(color);


        viewHolder.setPlatformName(hold.getPlatformId());

        viewHolder.setStatus("Status: ONLINE");


        //deviceHolder.setHeightPercent(1);
        //headerHolder.setEnabled(hold.position == 0);

        //TODO remove this edge case hack
        if (hold.getPlatformId().contains("java")) {
            hold.setPlatformType("Java");
        }
        switch (hold.getPlatformType()) {
            case ("Java"):
                viewHolder.platformIcon.setImageResource(R.drawable.java);
                break;

            case ("Xbox"):
                viewHolder.platformIcon.setImageResource(R.drawable.xbox);
                break;

            case ("Playstation"):
                viewHolder.platformIcon.setImageResource(R.drawable.xbox);
                break;

            default:
                viewHolder.platformIcon.setImageResource(R.drawable.xbox);
                break;

        }


        final String playerId = hold.getId();
        final String platformId = hold.getPlatformId();
        final int posit = position;
//        convertView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                //animator.onHeaderClick(posit);
//                deviceInterface.onDeviceSelected(position, playerId, platformId);
//            }
//        });
        //   }
//    {
//        // fillHeader(this.);
//        ViewGroup.LayoutParams layoutParams = headerLayout.getLayoutParams();
//
//        int overallHeight = this.getHeight();
//        layoutParams.height = this.settings.headerHeightPx;
//        headerLayout.setLayoutParams(layoutParams);
//    }

        return convertView;
    }
}
