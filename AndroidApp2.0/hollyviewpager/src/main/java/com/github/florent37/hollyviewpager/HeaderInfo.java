package com.github.florent37.hollyviewpager;

import android.graphics.Rect;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.nineoldandroids.animation.Animator;
import com.nineoldandroids.animation.AnimatorListenerAdapter;
import com.nineoldandroids.animation.ObjectAnimator;
import com.nineoldandroids.view.ViewHelper;

import java.util.Comparator;

/**
 * Created by florentchampigny on 07/08/15.
 */
public class HeaderInfo implements Comparable<HeaderInfo>{


    public String name;
    public byte[]iconHeader;
    public String desc;


    public int position;


    @Override
    public int compareTo(HeaderInfo another) {

        if(another.position == position){
            return 0;
        }

        if(another.position > position){
            return -1;
        } else return 1;


    }
}
