package com.portol.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.TextView;

import com.portol.R;
import com.portol.common.model.Category;
import com.portol.dataobject.CategoryViewHolder;


import java.util.List;

/**
 * Created by Aidan on 7/4/2015.
 * <p>
 * This does something similar to ContentListAdapter, but in a GridView
 */
public class CategoryListAdapter extends BaseAdapter implements ListAdapter {

    //keep a copy for local reference
    private List<Category> categoryList;
    private LayoutInflater layoutInflator;
    private Context context;

    public CategoryListAdapter(Context context, List<Category> realmResults, boolean automaticUpdate) {
        this.categoryList = realmResults;
        layoutInflator = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.context = context;
    }

    @Override
    public int getCount() {
        return categoryList.size();
    }

    @Override
    public Object getItem(int position) {
        return categoryList.get(position);
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
        Category toDisplay = categoryList.get(position);

        CategoryViewHolder viewHolder;
        if (convertView == null) {
            Log.d("Portol", "Category type: " + toDisplay.getType());
            switch (toDisplay.getType()) {

                case "VIDEO":
                    convertView = layoutInflator.inflate(R.layout.content_live_category, parent, false);

                    viewHolder = new CategoryViewHolder();
                    viewHolder.icon = (ImageView) convertView.findViewById(R.id.live_category_icon);
                    viewHolder.name = (TextView) convertView.findViewById(R.id.live_category_name);

                    break;

                case "RADIO":
                    Log.e("Adapter", "RADIO UNSUPPORTED");
                    viewHolder = null;
                    break;

                default:
                    Log.d("Portol", "No category type given.");
                    viewHolder = null;
                    break;
            }

            try {
                convertView.setTag(viewHolder);
            } catch (NullPointerException e) {
                e.printStackTrace();
            }

        } else {
            viewHolder = (CategoryViewHolder) convertView.getTag();
        }

/*
 * This is how it's done in content.
        DownloadImageTask downloader = new DownloadImageTask(viewHolder.thumb);
        downloader.doInBackground(toDisplay.
*/
        //TODO: Get icons properly
        int[] tempIconList = {R.drawable.search_icon, R.drawable.popular_icon,
                R.drawable.nebula_icon, R.drawable.wave_icon,
                R.drawable.flower_icon, R.drawable.popular_icon,
                R.drawable.plus_button};

        //TODO: Call setBackground & setDrawable(R.drawable.icn_q, THEME);
        viewHolder.icon.setBackgroundDrawable(this.context.getResources().getDrawable(tempIconList[position]));
        viewHolder.name.setText(toDisplay.getName());

        return convertView;
    }
}
