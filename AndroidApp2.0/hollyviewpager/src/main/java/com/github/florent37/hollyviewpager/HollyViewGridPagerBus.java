package com.github.florent37.hollyviewpager;

import android.content.Context;
import android.support.v7.widget.RecyclerView;


import java.util.HashMap;
import java.util.Map;

/**
 * Created by florentchampigny on 07/08/15.
 */
public class HollyViewGridPagerBus {

    public static Map<Context, HollyViewGridPager> map = new HashMap<>();

    public static void register(Context context, HollyViewGridPager hollyViewPager) {
        map.put(context, hollyViewPager);
    }

    public static void unregister(Context context){
        map.remove(context);
    }

    public static void registerGridView(Context context, ObservableGridView gridView) {
        HollyViewGridPager hollyViewPager = map.get(context);
        if(hollyViewPager != null)
            hollyViewPager.registerGridView(gridView);
    }

    public static void registerRecyclerView(Context context, RecyclerView recyclerView) {
        HollyViewGridPager hollyViewPager = map.get(context);
        if(hollyViewPager != null)
            hollyViewPager.registerRecyclerView(recyclerView);
    }

    public static HollyViewGridPager get(Context context){
        return map.get(context);
    }
}
