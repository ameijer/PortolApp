package com.portol.fragment.clickr;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.portol.R;
import com.portol.activity.MainActivity;
import com.portol.activity.PortolGridActivity;
import com.portol.adapter.DownloadImageTask;
import com.portol.client.PortolPlayerClient;
import com.portol.common.model.MovieFact;
import com.portol.common.model.content.ContentMetadata;
import com.portol.common.model.content.meta.Rating;
import com.portol.datastruct.IntervalTree;
import com.portol.fragment.PortolPanel;
import com.portol.repository.ContentRepository;

import java.util.ArrayList;
import java.util.List;


//import butterknife.ButterKnife;

/**
 * Created by florentchampigny on 07/08/15.
 */
public class MovieFaxFragment extends Fragment {


    public static final String TAG = "MovieFaxFragment";
    /* Just some random URLs
       *
       * Each page of our pager will display one URL from this array
       * Swiping, to the right will take you to the next page having
       * the next URL.
       */
    ArrayList<MovieFact> fax = new ArrayList<MovieFact>();
    private PortolPanel mContainingPanel;
    private ViewPager pager;
    private FragmentStatePagerAdapter adapter;
    private ContentMetadata focused;
    private IntervalTree<Integer> tree = new IntervalTree<Integer>();
    private int resId;
    private long currentTime;
    private int currentPosition;
    private PortolPlayerClient client;

    public static MovieFaxFragment newInstance(ContentMetadata focused, int resId) {
        Bundle args = new Bundle();
        args.putSerializable("contentMetadata", focused);
        args.putInt("resId", resId);
        MovieFaxFragment fragment = new MovieFaxFragment();
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle b = getArguments();
        if (b != null) {
            this.focused = (ContentMetadata) b.getSerializable("contentMetadata");
            this.resId = b.getInt("resId");
        }

    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            MainActivity mainActivity = (MainActivity) activity;
            mContainingPanel = mainActivity.getContentPanel();

        } catch (ClassCastException e) {
            throw new ClassCastException(mContainingPanel.toString()
                    + " must implement OnScannerDetectionListener");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = super.onCreateView(inflater, container, savedInstanceState);
        View myView = inflater.inflate(resId, container, false);

        pager = (ViewPager) myView.findViewById(R.id.mypager);

        adapter = new FragmentStatePagerAdapter(getActivity().getSupportFragmentManager()) {

            @Override
            public int getCount() {
                // This makes sure getItem doesn't use a position
                // that is out of bounds of our array of URLs
                return fax.size();
            }

            @Override
            public Fragment getItem(int position) {
                // Here is where all the magic of the adapter happens
                // As you can see, this is really simple.
                return BrowserFragment.newInstance(fax.get(position));
            }
        };

        //Let the pager know which adapter it is supposed to use
        pager.setAdapter(adapter);
        //this.title = (TextView) container.findViewById(R.id.title);
        return myView;
    }


    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    }


    public void setClient(PortolPlayerClient client) {
        this.client = client;

        //make call to update fax list
        ArrayList<MovieFact> rawFax = client.getFaxForFocused();

        this.fax.addAll(rawFax);
        //load up interval tree
        for (int i = 0; i < this.fax.size(); i++) {
            MovieFact fact = this.fax.get(i);
            this.tree.addInterval(fact.getStartTime(), fact.getStartTime() + fact.getDuration(), i);
        }
    }

    public void updateTime(long targetTime) {
        this.currentTime = targetTime;

        //find out what fact matches to this time
        List<Integer> posits = this.tree.get(targetTime);
        if (posits.size() > 1) {
            Log.e(TAG, "no support for multiple simultaneous fax, using just one...");
        }

        if (posits.size() < 1) {
            //no changes to be made
            return;
        }
        final int position = posits.get(0);

        //set viewpager to the right fact
        if (position != this.currentPosition) {
            this.currentPosition = position;
            final Handler h = new Handler(Looper.getMainLooper());
            final Runnable r = new Runnable() {
                public void run() {
                    if (pager != null) {
                        pager.setCurrentItem(position, true);

                    }
                }
            };
            h.post(r);
        }

    }
}
