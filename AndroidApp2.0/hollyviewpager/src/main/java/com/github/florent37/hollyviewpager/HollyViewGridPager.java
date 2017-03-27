package com.github.florent37.hollyviewpager;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;



import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by florentchampigny on 07/08/15.
 */
public class HollyViewGridPager extends FrameLayout {

    protected HollyViewGridPagerAnimator animator;
    protected HollyViewPagerSettings settings = new HollyViewPagerSettings();

    protected HorizontalScrollView headerScroll;
    protected ViewGroup headerLayout;
    protected ViewPager viewPager;


    protected List<HeaderHolder> headerHolders = new ArrayList<>();

    protected HollyViewPagerConfigurator configurator;




    public void setHeaderInfos(ArrayList<HeaderInfo> headerInfos) {
        this.headerInfos = headerInfos;
    }

    protected ArrayList<HeaderInfo> headerInfos;

    public HollyViewGridPager(Context context) {
        super(context);
    }

    public HollyViewGridPager(Context context, AttributeSet attrs) {
        super(context, attrs);
        settings.handleAttributes(context, attrs);
    }

    public HollyViewGridPager(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        settings.handleAttributes(context, attrs);
    }

    public HollyViewPagerConfigurator getConfigurator() {
        return configurator;
    }

    public void setConfigurator(HollyViewPagerConfigurator configurator) {
        this.configurator = configurator;
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        HollyViewGridPagerBus.register(getContext(), this);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        HollyViewPagerBus.unregister(getContext());
    }

    public ViewPager getViewPager() {
        return viewPager;
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

       View root =  LayoutInflater.from(getContext()).inflate(R.layout.holly_view_pager, this, false);
        addView(root);

        viewPager = (ViewPager) findViewById(R.id.bfp_viewPager);
        headerScroll = (HorizontalScrollView) findViewById(R.id.bfp_headerScroll);
        headerLayout = (ViewGroup) findViewById(R.id.bfp_headerLayout);

        {
            ViewGroup.LayoutParams layoutParams = headerLayout.getLayoutParams();

            int overallHeight = this.getHeight();
            layoutParams.height = this.settings.headerHeightPx;
            headerLayout.setLayoutParams(layoutParams);
        }

        animator = new HollyViewGridPagerAnimator(this);
    }

    protected void fillHeader(PagerAdapter adapter) {
        headerLayout.removeAllViews();

        LayoutInflater layoutInflater = LayoutInflater.from(getContext());

        Collections.sort(headerInfos);
        for (HeaderInfo hold : this.headerInfos) {
            View view = layoutInflater.inflate(R.layout.hvp_header_card, headerLayout, false);
            headerLayout.addView(view);

            HeaderHolder headerHolder = new HeaderHolder(view);
            headerHolders.add(headerHolder);

            headerHolder.setDescContents(hold.desc);
            headerHolder.setName(hold.name);
            headerHolder.setEnabled(hold.position == 0);

            final int position = hold.position;
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    animator.onHeaderClick(position);
                }
            });
        }
    }

    public void setAdapter(PagerAdapter adapter) {
        viewPager.setAdapter(adapter);
        fillHeader(adapter);
        viewPager.setOffscreenPageLimit(adapter.getCount()); //TODO remove
    }

    public void registerRecyclerView(RecyclerView recyclerView) {
        animator.registerRecyclerView(recyclerView);
    }

    public void registerGridView(ObservableGridView scrollView) {
        animator.registerGridView(scrollView);
    }
}