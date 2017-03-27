package com.portol.fragment.content;

/*
 * These is equivalent to your ViewPagerTabListViewFragment. Displays a grid of similar videos
 */

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.florent37.hollyviewpager.HeaderHolder;
import com.github.florent37.hollyviewpager.HeaderInfo;
import com.github.florent37.hollyviewpager.HollyViewGridPager;
import com.github.florent37.hollyviewpager.HollyViewPagerConfigurator;
import com.github.florent37.hollyviewpager.ObservableGridView;
import com.google.common.base.Charsets;
import com.portol.Portol;
import com.portol.R;
import com.portol.activity.MainActivity;
import com.portol.common.model.Category;

import com.portol.fragment.BaseFragment;
import com.portol.repository.CategoriesRepository;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class CategoryGridPagerFragment extends BaseFragment /*implements ObservableScrollViewCallbacks,ObservableGridView.OnItemClickListener*/ {

    public static final String ARG_INITIAL_POSITION = "ARG_INITIAL_POSITION";
    public static final String ARG_CONTENT_ID = "ARG_CONTENT";

    int pageCount;
    HollyViewGridPager hollyViewPager;
    private MainActivity parentActivity;
    private Portol app;
    private CategoriesRepository catRepo;
    private List<Category> mCategories;
    private int mInitialPosition;
    private int mBaseTranslationY;
    private View mHeaderView;
    //private OnCategoryCommandListener mCategoryCommandListener;
    private PortolContentPanel mContainingPanel;

    public CategoryGridPagerFragment() {
        super();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.app = (Portol) getActivity().getApplication();
        catRepo = app.getCategoriesRepo();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.content_category_grid, container, false);
        this.hollyViewPager = (HollyViewGridPager) view.findViewById(R.id.hollyViewGridPager);


        ArrayList<HeaderInfo> holders = null;
        try {
            holders = this.getCurrentHeaders();
        } catch (Exception e) {
            e.printStackTrace();
        }
        this.pageCount = holders.size();
        hollyViewPager.setHeaderInfos(holders);
        // hollyViewPager.getViewPager().setPageMargin(getResources().getDimensionPixelOffset(R.dimen.viewpager_margin));
        hollyViewPager.setConfigurator(new HollyViewPagerConfigurator() {
            @Override
            public float getHeightPercentForPage(int page) {
                return 1000f;
            }
        });

        hollyViewPager.setAdapter(new FragmentPagerAdapter(getActivity().getSupportFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
                //if(position%2==0)
                //    return new RecyclerViewFragment();
                //else
                try {
                    Category cur = catRepo.findByPosition(position);
                    if (cur == null)
                        throw new Exception("No category found for position: " + position);

                    return GridViewFragment.newInstance(cur.getCategoryId());
                } catch (Exception e) {
                    Log.e("CatGrdiPagerFragment", "WARNING< NULL CATEGORY, using index 0 as placeholder", e);
                    Category cur = catRepo.findByPosition(0);
                    return GridViewFragment.newInstance(cur.getCategoryId());


                }

            }

            @Override
            public int getCount() {
                return pageCount;
            }

            @Override
            public CharSequence getPageTitle(int position) {
                return "TITLE " + position;
            }
        });

        return view;
    }

    public ArrayList<HeaderInfo> getCurrentHeaders() {
        List<Category> cats = null;

        cats = this.catRepo.getAllValidCategories();

        ArrayList<HeaderInfo> headers = new ArrayList<HeaderInfo>();
        for (Category cat : cats) {
            HeaderInfo newHeader = new HeaderInfo();
            newHeader.desc = cat.getDesc();
            //newHeader.iconHeader = Base64.decode(cat.getIconEncoded(), Base64.DEFAULT);
            newHeader.name = cat.getName();
            newHeader.position = cat.getPosition();
            headers.add(newHeader);
        }

        return headers;
    }

}