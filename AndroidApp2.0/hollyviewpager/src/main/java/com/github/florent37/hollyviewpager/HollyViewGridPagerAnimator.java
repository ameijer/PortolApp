package com.github.florent37.hollyviewpager;

import android.animation.ObjectAnimator;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ScrollView;

import com.nineoldandroids.view.ViewHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by florentchampigny on 07/08/15.
 */
public class HollyViewGridPagerAnimator implements ViewPager.OnPageChangeListener {
    HollyViewGridPager hvp;

    float initialHeaderHeight = -1;
    float finalHeaderHeight = -1;
    List<Object> scrolls = new ArrayList<>();
    List<Object> calledScrolls = new ArrayList<>();

    int oldpage = -2;

    protected void onPageScroll(int position) {
        if (position != oldpage) {
            oldpage = position;
            for (int i = 0, size = hvp.headerHolders.size(); i < size; ++i) {
                if (i == position) {
                    hvp.headerHolders.get(i).animateEnabled(true);

                    if (!hvp.headerHolders.get(i).isVisible()) {
                        if (i - 1 > 0)
                            hvp.headerScroll.smoothScrollTo(hvp.headerHolders.get(i - 1).view.getLeft(), 0);
                        else
                            hvp.headerScroll.smoothScrollTo(hvp.headerHolders.get(i).view.getLeft(), 0);
                    }

                } else {
                    hvp.headerHolders.get(i).animateEnabled(false);
                }
            }
        }
    }

    public HollyViewGridPagerAnimator(HollyViewGridPager beautifulViewPager) {
        this.hvp = beautifulViewPager;
        hvp.viewPager.addOnPageChangeListener(this);
    }

    public void onScroll(Object source, int verticalOffset) {
        dispatchScroll(source, verticalOffset);

        hvp.headerScroll.setTranslationY(-verticalOffset);
        if (hvp.headerScroll.getTranslationY() > 0)
            hvp.headerScroll.setTranslationY(0);

        if (initialHeaderHeight <= 0) {
            initialHeaderHeight = hvp.headerScroll.getHeight();
            finalHeaderHeight = (float) initialHeaderHeight / 1.5f;
        }
        if (initialHeaderHeight > 0) {

            if (verticalOffset < finalHeaderHeight) {
                float percent = (initialHeaderHeight - verticalOffset) / initialHeaderHeight;

                int page = Math.max(0,oldpage);

                if (!hvp.headerHolders.get(page).isVisible()) {
                    if (page - 1 > 0) {
                        hvp.headerScroll.smoothScrollTo(hvp.headerHolders.get(page - 1).view.getLeft(), 0);
                         //ObjectAnimator animator = ObjectAnimator.ofInt(hvp.headerScroll, "scrollX", hvp.headerHolders.get(page - 1).view.getLeft());
                        // animator.setDuration(500);
                        // animator.start();
                        //hvp.headerScroll.scrollTo(hvp.headerHolders.get(page - 1).view.getLeft(), 0);
                    } else {
                        hvp.headerScroll.smoothScrollTo(hvp.headerHolders.get(page).view.getLeft(), 0);
                       // ObjectAnimator animator = ObjectAnimator.ofInt(hvp.headerScroll, "scrollX", hvp.headerHolders.get(page).view.getLeft());
                       //  animator.setDuration(500);
                       //  animator.start();
                        // hvp.headerScroll.scrollTo(hvp.headerHolders.get(page).view.getLeft(), 0);
                    }
                }
//                hvp.headerLayout.setPivotY(0);
//                ViewHelper.setPivotX(hvp.headerLayout, hvp.headerLayout.getChildAt(page).getLeft());
//
//                ViewHelper.setScaleX(hvp.headerLayout, percent);
//                ViewHelper.setScaleY(hvp.headerLayout, percent);
//                ViewHelper.setTranslationY(hvp.headerLayout, verticalOffset / 2);
//
//                ViewHelper.setTranslationX(hvp.headerLayout,1.5f * verticalOffset);
//
//                float alphaPercent = 1 - verticalOffset / finalHeaderHeight;
//                for (int i = 0, count = hvp.headerHolders.size(); i < count; ++i) {
//                    HeaderHolder headerHolder = hvp.headerHolders.get(i);
//                    headerHolder.name.setAlpha(alphaPercent);
//
//                    headerHolder.view.setTranslationX(-i * 4 * headerHolder.view.getPaddingLeft() * (1 - percent));
//                }
            }
        }
    }




    public void onHeaderClick(int position) {
        hvp.viewPager.setCurrentItem(position, true);
    }

    public void registerRecyclerView(RecyclerView recyclerView) {
        scrolls.add(recyclerView);
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if (calledScrolls.contains(recyclerView))
                    calledScrolls.remove(recyclerView);
                else {
                    onScroll(recyclerView, recyclerView.computeVerticalScrollOffset());
                }
            }
        });

    }

    public void registerGridView(final ObservableGridView scrollView) {
        scrolls.add(scrollView);

        if (scrollView.getParent() != null && scrollView.getParent().getParent() != null && scrollView.getParent().getParent() instanceof ViewGroup)
            scrollView.setTouchInterceptionViewGroup((ViewGroup) scrollView.getParent().getParent());

        scrollView.setScrollViewCallbacks(new ObservableScrollViewCallbacks() {
            @Override
            public void onScrollChanged(int i, boolean b, boolean b1) {
                onScroll(scrollView, i);
            }

            @Override
            public void onDownMotionEvent() {

            }

            @Override
            public void onUpOrCancelMotionEvent(ScrollState scrollState) {

            }
        });
    }

    public void dispatchScroll(Object source, int yOffset) {
        for (Object scroll : scrolls) {
            if (scroll != source) {
                calledScrolls.add(scroll);
                if (scroll instanceof RecyclerView) {
                    RecyclerView recyclerView = (RecyclerView) scroll;
                    if (recyclerView != null && recyclerView.getLayoutManager() != null && recyclerView.getLayoutManager() instanceof LinearLayoutManager) {
                        LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                        linearLayoutManager.scrollToPositionWithOffset(0, -yOffset);
                    }
                } else if (scroll instanceof GridView) {
                    ((GridView) scroll).scrollTo(0, yOffset);
                }
            }
        }

        calledScrolls.clear();
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
    }

    @Override
    public void onPageSelected(int position) {
        onPageScroll(position);
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }
}
