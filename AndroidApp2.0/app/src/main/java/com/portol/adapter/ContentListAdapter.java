package com.portol.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.portol.R;
import com.portol.common.model.content.ContentMetadata;
import com.portol.dataobject.ContentViewHolder;
import com.portol.dataobject.LiveObjectHolder;
import com.portol.dataobject.VODObjectHolder;
import com.portol.dataobject.VODProfileHolder;
import com.portol.fragment.content.GridItemClickedInterface;

import org.apache.commons.lang3.ArrayUtils;

import java.util.List;


/**
 * Created by Aidan on 7/4/2015.
 * <p>
 * This will do the same thing as the ContentListAdapter, except there will also be a cool picture.
 * Probably other stuff too.
 */
public class ContentListAdapter extends BaseAdapter implements ListAdapter {

    Context context;
    LayoutInflater layoutInflator;
    //keep a copy for local reference
    private GridItemClickedInterface clickedInterface;
    private List<ContentMetadata> contentList;
    private String[] ids = new String[0];
    public ContentListAdapter(Context context, List<ContentMetadata> realmResults, boolean automaticUpdate) {
        this.contentList = realmResults;
        this.context = context;
        layoutInflator = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

    }

    public void setClickedInterface(GridItemClickedInterface clickedInterface) {
        this.clickedInterface = clickedInterface;
    }

    @Override
    public int getCount() {
        return contentList.size();
    }

    @Override
    public Object getItem(int position) {
        return contentList.get(position);
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
        final ContentMetadata toDisplay = contentList.get(position);
        final int posit = position;
        final String id = toDisplay.getParentContentId();
        ContentViewHolder viewHolder;
        if (convertView == null) {


            switch (toDisplay.getType()) {

                case "LIVE":

                    /* Grid layout with infobars

                     */
                    convertView = layoutInflator.inflate(R.layout.content_live_object, parent, false);

                    viewHolder = new LiveObjectHolder();

                    ((LiveObjectHolder) viewHolder).epgBar = (ImageView) convertView.findViewById(R.id.epgBar);
                    ;

                    viewHolder.price = (TextView) convertView.findViewById(R.id.live_price);
                    viewHolder.rating = (RatingBar) convertView.findViewById(R.id.live_rating);
                    viewHolder.title = (TextView) convertView.findViewById(R.id.live_synopsis);
                    viewHolder.thumb = (ImageButton) convertView.findViewById(R.id.live_thumb);

                    break;
                case "VOD":

                    /*
                    convertView = layoutInflator.inflate(R.layout.content_vod_object, parent, false);
                    viewHolder = new VODObjectHolder();

                    viewHolder.price = (TextView) convertView.findViewById(R.id.vod_price);
                    viewHolder.rating = (RatingBar)convertView.findViewById(R.id.vod_rating);
                    viewHolder.title = (TextView)convertView.findViewById(R.id.vod_synopsis);
                    viewHolder.thumb = (ImageButton)convertView.findViewById(R.id.vod_thumb);
                    */
                    convertView = layoutInflator.inflate(R.layout.content_cover_profile, parent, false);

                    viewHolder = new VODProfileHolder();
                    viewHolder.thumb = (ImageButton) convertView.findViewById(R.id.vod_thumb);

                    viewHolder.thumb.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            String message = "Button " + posit + " is clicked for content: " + id;
                            //Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
                            Log.v("ONCLICK", "click: " + message);
                            clickedInterface.gridClicked(id, posit, null);
                        }
                    });

                    break;
                case "RADIO":
                    Log.e("Adapter", "RADIO UNSUPPORTED");
                    viewHolder = null;
                    break;

                default:

                    viewHolder = null;
                    break;
            }
            try {
                convertView.setTag(viewHolder);
            } catch (NullPointerException e) {
                e.printStackTrace();
            }

        } else {
            viewHolder = (ContentViewHolder) convertView.getTag();
        }


        //TODO
        if (viewHolder instanceof LiveObjectHolder) {
            //set up epg
        }

        DownloadImageTask downloader = new DownloadImageTask(viewHolder.thumb);
        downloader.execute(toDisplay.getSplashURL());
        //viewHolder.title.setText(toDisplay.getChannelOrVideoTitle());

        //TODO
        //viewHolder.rating.setNumStars(3);
        //viewHolder.price.setText(toDisplay.getPrices().getPriceInBits() + "B");

        return convertView;
    }
}
