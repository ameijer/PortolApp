package com.portol.fragment.content;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.github.florent37.hollyviewpager.HollyViewGridPagerBus;
import com.github.florent37.hollyviewpager.ObservableGridView;
import com.github.florent37.hollyviewpager.ObservableScrollViewCallbacks;
import com.github.florent37.hollyviewpager.ScrollState;
import com.portol.R;
import com.portol.activity.MainActivity;
import com.portol.adapter.ContentListAdapter;
import com.portol.common.model.Category;
import com.portol.common.model.content.ContentMetadata;
import com.portol.repository.CategoriesRepository;
import com.portol.repository.ContentRepository;

import java.util.List;


//import butterknife.ButterKnife;

/**
 * Created by florentchampigny on 07/08/15.
 */
public class GridViewFragment extends Fragment implements GridItemClickedInterface {

    public static final String TAG = "GridViewFragment";
    //@Bind(R.id.scrollView)
    ObservableGridView gridView;
    // @Bind(R.id.title)
    TextView title;
    private ContentRepository contentRepo;
    private CategoriesRepository catRepo;
    private String categoryId;

    public static GridViewFragment newInstance(String key) {
        Bundle args = new Bundle();
        args.putString("key", key);
        GridViewFragment fragment = new GridViewFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle b = getArguments();
        if (b != null) {
            this.categoryId = b.getString("key");

        }
        this.contentRepo = ((MainActivity) getActivity()).getApp().getContentRepo();
        this.catRepo = ((MainActivity) getActivity()).getApp().getCategoriesRepo();

    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);


    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = super.onCreateView(inflater, container, savedInstanceState);
        View myView = inflater.inflate(R.layout.fragment_scroll, container, false);
        // LayoutInflater gridInflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        //View v = gridInflater.inflate(R.layout.fragment_scroll, null);

        this.gridView = (ObservableGridView) myView.findViewById(R.id.gridder);

        //add header
        View headerView = inflater.inflate(R.layout.hvp_header_placeholder, container, false);
        this.gridView.addHeaderView(headerView);

//        String[] numbers = new String[] {
//                this.categoryId, this.categoryId};
//
//        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, numbers);
        Category target = null;
        try {
            target = catRepo.findById(categoryId);
        } catch (Exception e) {
            e.printStackTrace();
        }

        List<ContentMetadata> content = null;
        try {
            content = this.contentRepo.findAllInCategory(target);
        } catch (Exception e) {
            Log.e(TAG, "error finding content in category: " + target.getName(), e);
        }
        ContentListAdapter adapt = new ContentListAdapter(getContext(), content, false);
        adapt.setClickedInterface(this);
        gridView.setAdapter(adapt);

        //this.title = (TextView) container.findViewById(R.id.title);
        return myView;
    }

    private void displayView(int position, String id) {
        // update the main content by replacing fragments
        Fragment fragment = null;
        fragment = ItemFocusFragment.newInstance(id, R.layout.item_focus);

        if (fragment != null) {
            FragmentManager fragmentManager = getFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(R.id.hollyViewGridPager, fragment)
                    .addToBackStack(null).commit();

        }
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //ButterKnife.bind(this, view);

        //title.setText("title");

        HollyViewGridPagerBus.registerGridView(getActivity(), gridView);
    }


    @Override
    public void gridClicked(String contentId, int position, String platformId) {
        this.displayView(position, contentId);
    }
}
