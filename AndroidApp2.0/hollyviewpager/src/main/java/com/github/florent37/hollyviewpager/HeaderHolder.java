package com.github.florent37.hollyviewpager;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.nineoldandroids.animation.Animator;
import com.nineoldandroids.animation.AnimatorListenerAdapter;
import com.nineoldandroids.animation.ObjectAnimator;
import com.nineoldandroids.view.ViewHelper;

import org.w3c.dom.Text;

import java.io.Serializable;

/**
 * Created by florentchampigny on 07/08/15.
 */
public class HeaderHolder {
    public void setView(View view) {
        this.view = view;
    }

    public View view;
    public TextView name;
    public TextView desc;

    public void setPosition(int position) {
        this.position = position;
    }

    private int position;

    ObjectAnimator animator = null;

    public HeaderHolder(View view, TextView name, TextView desc) {
        this.view = view;
        this.name = name;
        this.desc = desc;
    }

    public HeaderHolder(TextView name, TextView desc, int position) {
        this.view = null;
        this.name = name;
        this.desc = desc;
        this.position = position;
    }

    public HeaderHolder(View view) {
        this.view = view;
        this.name = (TextView) view.findViewById(R.id.category_name);
        this.desc = (TextView) view.findViewById(R.id.category_desc);
    }

    public void setTitle(CharSequence title) {
        this.name.setText(title);
    }

    public void setName(String name){this.name.setText(name);}
    public TextView getDesc() {
        return desc;
    }

    public void setDescContents(String desc) {
        this.desc.setText(desc);
    }

    public void enable() {
        setEnabled(true);
    }

    public void disable() {
        setEnabled(false);
    }

    public void setEnabled(boolean enabled) {
        if (enabled)
            ViewHelper.setAlpha(view, 1f);
        else
            ViewHelper.setAlpha(view, 0.8f);
    }

    public void animateEnabled(boolean enabled) {
        if(animator != null) {
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

    public boolean isVisible() {
        Rect rect = new Rect();
        return view.getGlobalVisibleRect(rect);
    }


    public int getPosition() {
        return position;
    }
}
