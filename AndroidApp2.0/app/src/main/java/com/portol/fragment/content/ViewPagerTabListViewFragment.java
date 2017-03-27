package com.portol.fragment.content;/*
 * Copyright 2014 Soichiro Kashima
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.portol.adapter.ContentListAdapter;
import com.portol.Portol;
import com.portol.R;
import com.portol.common.model.content.ContentMetadata;
import com.portol.fragment.BaseFragment;
import com.portol.util.ScrollUtils;
import com.portol.activity.MainActivity;
import com.portol.common.model.Category;
import com.portol.repository.CategoriesRepository;
import com.portol.repository.ContentRepository;
import com.portol.view.ObservableListView;
import com.portol.view.ObservableScrollViewCallbacks;

import java.util.ArrayList;
import java.util.List;


public class ViewPagerTabListViewFragment extends BaseFragment {

    public static final String ARG_INITIAL_POSITION = "ARG_INITIAL_POSITION";
    public static final String ARG_CONTENT_ID = "ARG_CONTENT";

    private Portol app;
    private CategoriesRepository catRepo;
    private ContentRepository contentRepo;

    public ViewPagerTabListViewFragment() {
        super();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        MainActivity parent = (MainActivity) getActivity();
        app = (Portol) parent.getApplication();
        catRepo = app.getCategoriesRepo();
        contentRepo = app.getContentRepo();

        View view = inflater.inflate(R.layout.fragment_listview, container, false);

        Activity parentActivity = getActivity();
        final ObservableListView listView = (ObservableListView) view.findViewById(R.id.scroll);

        Bundle args = super.getArguments();

        String idofCategory = args.getString(this.ARG_CONTENT_ID);

        Category current = null;
        try {
            current = catRepo.findById(idofCategory);
        } catch (Exception e) {
            e.printStackTrace();
        }

        List<ContentMetadata> eligible = null;
        try {
            eligible = contentRepo.findAllInCategory(current);
        } catch (Exception e) {
            e.printStackTrace();
        }

        ArrayList<ContentMetadata> converted = new ArrayList<>();
        for (int i = 0; i < eligible.size(); i++) {
            ContentMetadata cur = eligible.get(i);
            converted.add(cur);
        }

        ContentListAdapter adapt = new ContentListAdapter(getActivity(), eligible, true);

        listView.setAdapter(adapt);

        if (parentActivity instanceof ObservableScrollViewCallbacks) {
            // Scroll to the specified position after layout

            if (args != null && args.containsKey(ARG_INITIAL_POSITION)) {
                final int initialPosition = args.getInt(ARG_INITIAL_POSITION, 0);
                ScrollUtils.addOnGlobalLayoutListener(listView, new Runnable() {
                    @Override
                    public void run() {
                        // scrollTo() doesn't work, should use setSelection()
                        listView.setSelection(initialPosition);
                    }
                });
            }

            listView.setScrollViewCallbacks((ObservableScrollViewCallbacks) parentActivity);
        }
        return view;
    }
}