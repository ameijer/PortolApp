package com.portol.fragment.content;

import android.app.Activity;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

import com.portol.R;
import com.portol.common.model.user.User;
import com.portol.fragment.PortolPanel;

/**
 * Created by Aidan on 7/3/15.
 */
public class PortolContentPanel extends PortolPanel {

    User mCurrentUser;

    private CategoryGridPagerFragment mCategoryGridPagerFragment = new CategoryGridPagerFragment();
    private ItemFocusFragment focusFragment = new ItemFocusFragment();
    private Fragment contentDefaultFragment = mCategoryGridPagerFragment;
    private Activity holdingActivity;


    public PortolContentPanel(int layoutContainer, FragmentManager mainFragmentManager, Activity holdingActivity) {
        super(layoutContainer, mainFragmentManager, holdingActivity);


        this.holdingActivity = holdingActivity;
        //panelDefault declared as protected in PortolPanel
        panelDefault = contentDefaultFragment;
    }

    public void displayContentLists() {
        setNextFragment(mCategoryGridPagerFragment);
        swapCurrentToNextFragment();
    }

    public void prepTransition(String id) {
        focusFragment = ItemFocusFragment.newInstance(id, R.layout.item_focus);
    }

    private void displayView() {
        // update the main content by replacing fragments

        FragmentManager fragmentManager = super.mainFragmentManager;
        fragmentManager.beginTransaction()
                .replace(R.id.hollyViewGridPager, focusFragment)
                .addToBackStack(null).commit();

    }

    public void focusOnItem() {
        this.displayView();
    }

}