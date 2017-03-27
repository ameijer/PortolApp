package com.portol.fragment.clickr;

/**
 * Created by alex on 10/7/15.
 */

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.portol.R;
import com.portol.common.model.MovieFact;

public class BrowserFragment extends Fragment {

    WebView browser;

    MovieFact thisFact;

    // This is the method the pager adapter will use
    // to create a new fragment
    public static Fragment newInstance(MovieFact matching) {
        BrowserFragment f = new BrowserFragment();
        f.thisFact = matching;
        return f;
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(
                R.layout.fragment_browser,
                container,
                false);

        browser = (WebView) view.findViewById(R.id.my_browser);
        browser.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        browser.setWebViewClient(new WebViewClient());
        browser.loadUrl(thisFact.getUrl());
        // Just load whatever URL this fragment is
        // created with.
        return view;
    }

}
