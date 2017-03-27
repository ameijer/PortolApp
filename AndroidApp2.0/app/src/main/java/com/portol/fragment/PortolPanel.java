package com.portol.fragment;

import android.app.Activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;

import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.widget.LinearLayout;

import com.portol.animation.ViewAnimator;

import io.codetail.animation.SupportAnimator;
import io.codetail.animation.ViewAnimationUtils;

/**
 * Created by Aidan on 7/3/15.
 */
public abstract class PortolPanel extends BroadcastReceiver {
    public static final String TAG = "PortolPanel";
    public Activity holdingActivity = null;
    protected FragmentManager mainFragmentManager;
    protected Fragment panelDefault = null;
    protected Fragment mCurrentFragment = null;
    protected Fragment mNextFragment = null;
    private float weight;
    private int mContainer;
    private View mContainingView = null;

    public PortolPanel(int layoutContainer, FragmentManager mainFragmentManager, Activity holdingActivity) {
        mContainer = layoutContainer;

        //TODO: Should really just pull this from holdingActivity.getFragmentManager()
        this.mainFragmentManager = mainFragmentManager;
        this.holdingActivity = holdingActivity;
        mContainingView = holdingActivity.findViewById(mContainer);
    }

    private void setCurrentFragment(Fragment currentFragment) {
        this.mCurrentFragment = currentFragment;
    }

    public Fragment getCurrentFocusedFragment() {
        return this.mCurrentFragment;
    }

    public Context getParentContext() {
        return this.holdingActivity.getApplicationContext();
    }

    public void setNextFragment(Fragment nextFragment) {
        this.mNextFragment = nextFragment;
    }

    public float hide() {
        if (mContainingView != null) {
            mContainingView.setVisibility(View.GONE);
        }
        this.weight = ((LinearLayout.LayoutParams) mContainingView.getLayoutParams()).weight;
        return ((LinearLayout.LayoutParams) mContainingView.getLayoutParams()).weight;
    }

    public float show() {
        if (mContainingView != null) {
            mContainingView.setVisibility(View.VISIBLE);
        }

        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) mContainingView.getLayoutParams();
        float initialSize = params.weight;
        float newSize = this.weight;
        params.weight = newSize;

        mContainingView.setLayoutParams(params);

        return ((LinearLayout.LayoutParams) mContainingView.getLayoutParams()).weight;
    }

    public float expandBy(float toExpand) {
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) mContainingView.getLayoutParams();
        float initialSize = params.weight;
        float newSize = initialSize + toExpand;
        params.weight = newSize;

        mContainingView.setLayoutParams(params);
        return newSize;

    }

    public float contractBy(float needed) {
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) mContainingView.getLayoutParams();
        float initialSize = params.weight;
        float newSize = initialSize - needed;
        params.weight = newSize;

        mContainingView.setLayoutParams(params);
        return newSize;
    }

    public float contractTo(float newWeight) {
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) mContainingView.getLayoutParams();
        float initialSize = params.weight;
        float freed = initialSize - newWeight;
        params.weight = newWeight;

        mContainingView.setLayoutParams(params);
        return freed;
    }

    public float expandTo(float newWeight) {
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) mContainingView.getLayoutParams();
        float initialSize = params.weight;
        float freed = initialSize - newWeight;
        params.weight = newWeight;

        mContainingView.setLayoutParams(params);
        return freed;
    }

//    public void displayDefault() {
//
//        if (null != panelDefault) {
//            mCurrentFragment = panelDefault;
//        }
//
//        updateDisplay();
//
//        return;
//    }

    public void popBackstack() {
        mainFragmentManager.popBackStack();
    }

    public void updateDisplay() {
        FragmentTransaction t = mainFragmentManager.beginTransaction();

        /*TODO:
         * if (containerHasDisplayAlready)
         *      t.add(new display)
         * else
         *      t.replace(old, new display)
         */
        t.add(mContainer, mCurrentFragment);
        t.addToBackStack(null);
        t.commit();
    }

    protected Fragment swapCurrentToNextFragment() {
        //TODO: Get fragment view container.
//        int finalRadius = Math.max(mContainingView.getWidth(), mContainingView.getHeight());

//        //TODO: slide right / swap out animation instead?
//        //TODO: Alex had a "top" value here, in place of the middle 0... what was that about?
//        try {
//            SupportAnimator animator = ViewAnimationUtils.createCircularReveal(mContainingView, 0, 0, 0, finalRadius);
//            animator.setInterpolator(new AccelerateInterpolator());
//            animator.setDuration(ViewAnimator.CIRCULAR_REVEAL_ANIMATION_DURATION);
//
//            //TODO: figure out the bitmap stuff
//            //mContainingView.setBackgroundDrawable(new BitmapDrawable(getResources(), screenShotable.getBitmap()));
//            animator.start();
//        } catch (Exception e){
//            Log.e(TAG, "Error animating fragment", e);
//        }
        mCurrentFragment = mNextFragment;
        FragmentTransaction replace = mainFragmentManager.beginTransaction();

        replace.replace(mContainer, mNextFragment);
        replace.addToBackStack(null);
        replace.commit();
//mainFragmentManager.executePendingTransactions();
        return mNextFragment;
    }

    @Override
    public void onReceive(Context context, Intent intent) {

    }


}