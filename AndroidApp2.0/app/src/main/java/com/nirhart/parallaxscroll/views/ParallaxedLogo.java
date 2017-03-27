package com.nirhart.parallaxscroll.views;

import android.view.View;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;

/**
 * Created by Aidan on 8/1/15.
 */
public class ParallaxedLogo extends ParallaxedView {
    int lastOffset = 0;

    public ParallaxedLogo(ImageView view) {
        super(view);
    }

    @Override
    protected void translatePreICS(View view, float offset) {
        view.offsetTopAndBottom((int) offset - lastOffset);
        lastOffset = (int) offset;
    }
}

