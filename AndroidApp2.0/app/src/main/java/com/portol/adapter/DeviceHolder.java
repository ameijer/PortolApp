package com.portol.adapter;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.support.v7.widget.CardView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.nineoldandroids.animation.Animator;
import com.nineoldandroids.animation.AnimatorListenerAdapter;
import com.nineoldandroids.animation.ObjectAnimator;
import com.nineoldandroids.view.ViewHelper;
import com.portol.R;

/**
 * Created by florentchampigny on 07/08/15.
 */
public class DeviceHolder {
    public View view;
    public TextView platformName;
    public ImageView platformIcon;
    public TextView status;
    public CardView card;
    ObjectAnimator animator = null;
    private int position;

    public DeviceHolder(View view, TextView platformName, ImageView iconHeader, TextView status, CardView card) {
        this.view = view;
        this.platformName = platformName;
        this.platformIcon = iconHeader;
        this.status = status;
        this.card = card;
    }

    public DeviceHolder(View view, TextView platformName, ImageView iconHeader, TextView status) {
        this.view = view;
        this.platformName = platformName;
        this.platformIcon = iconHeader;
        this.status = status;
        this.card = null;
    }

    public DeviceHolder(View view) {
        this.view = view;
        this.platformName = (TextView) view.findViewById(R.id.device_name);
        this.card = (CardView) view.findViewById(R.id.devicecard);
        this.platformIcon = (ImageView) view.findViewById(R.id.device_icon);
        this.status = (TextView) view.findViewById(R.id.device_status);

    }

    public void setView(View view) {
        this.view = view;
    }

    public void setCard(CardView card) {
        this.card = card;
    }

    public TextView getPlatformName() {
        return this.platformName;
    }

    public void setPlatformName(CharSequence title) {
        this.platformName.setText(title);
    }

    public void setStatus(String status) {
        this.status.setText(status);
    }

    public ImageView getPlatformIcon() {
        return this.platformIcon;
    }

    public void setPlatformIcon(byte[] encoded) {
        Bitmap bmp = BitmapFactory.decodeByteArray(encoded, 0, encoded.length);

        this.platformIcon.setImageBitmap(bmp);
    }

    public void setHeightPercent(float percent) {
        ViewGroup.LayoutParams layoutParams = card.getLayoutParams();
        layoutParams.height = (int) (view.getLayoutParams().height * percent);
        card.setLayoutParams(layoutParams);
    }

    public void enable() {
        setEnabled(true);
    }

    public void disable() {
        setEnabled(false);
    }

    public void animateEnabled(boolean enabled) {
        if (animator != null) {
            animator.cancel();
            animator = null;
        }

        if (enabled) {
            animator = ObjectAnimator.ofFloat(view, "alpha", 1f);
        } else
            animator = ObjectAnimator.ofFloat(view, "alpha", 0.5f);

        animator.setDuration(300);
        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                animator = null;
            }
        });
        animator.start();
    }

    public boolean isEnabled() {
        return ViewHelper.getAlpha(view) != 0.5f;
    }

    public void setEnabled(boolean enabled) {
        if (enabled)
            ViewHelper.setAlpha(view, 1f);
        else
            ViewHelper.setAlpha(view, 0.8f);
    }

    public boolean isVisible() {
        Rect rect = new Rect();
        return view.getGlobalVisibleRect(rect);
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }
}
