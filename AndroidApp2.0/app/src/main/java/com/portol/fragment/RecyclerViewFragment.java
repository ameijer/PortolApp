package com.portol.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import com.portol.R;
import com.recycler.RecyclerAdapter;
import com.github.florent37.hollyviewpager.HollyViewPagerBus;

//import butterknife.ButterKnife;

/**
 * Created by florentchampigny on 07/08/15.
 */
public class RecyclerViewFragment extends Fragment {

    // @Bind(R.id.recyclerView)
    RecyclerView recyclerView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View myView = inflater.inflate(R.layout.fragment_recyclerview, container, false);
        this.recyclerView = (RecyclerView) myView.findViewById(R.id.recyclerView);
        return myView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //ButterKnife.bind(this,view);

        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(new RecyclerAdapter());

        HollyViewPagerBus.registerRecyclerView(getActivity(), recyclerView);
    }
}
