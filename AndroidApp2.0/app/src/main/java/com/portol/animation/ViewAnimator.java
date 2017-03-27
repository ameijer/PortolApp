package com.portol.animation;

import android.os.Handler;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import yalantis.com.sidemenu.animation.FlipAnimation;
import yalantis.com.sidemenu.interfaces.Resourceble;
import yalantis.com.sidemenu.interfaces.ScreenShotable;

public class ViewAnimator<T extends Resourceble> {
    public static final int CIRCULAR_REVEAL_ANIMATION_DURATION = 500;
    private final int ANIMATION_DURATION = 175;
    private AppCompatActivity actionBarActivity;
    private List<T> list;
    private List<View> viewList = new ArrayList();
    private ScreenShotable screenShotable;
    private DrawerLayout drawerLayout;
    private ViewAnimator.ViewAnimatorListener animatorListener;

    public ViewAnimator(AppCompatActivity activity, List<T> items, ScreenShotable screenShotable, DrawerLayout drawerLayout, ViewAnimator.ViewAnimatorListener animatorListener) {
        this.actionBarActivity = activity;
        this.list = items;
        this.screenShotable = screenShotable;
        this.drawerLayout = drawerLayout;
        this.animatorListener = animatorListener;
    }

    public void showMenuContent() {
        this.setViewsClickable(false);
        this.viewList.clear();
        double size = (double) this.list.size();

        for (int i = 0; (double) i < size; ++i) {
            final int j = i;
            View viewMenu = this.actionBarActivity.getLayoutInflater().inflate(yalantis.com.sidemenu.R.layout.menu_list_item, (ViewGroup) null);
            viewMenu.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    int[] location = new int[]{0, 0};
                    v.getLocationOnScreen(location);

                    ViewAnimator.this.switchItem((Resourceble) ViewAnimator.this.list.get(j), location[1] + v.getHeight() / 2);
                }
            });
            ((ImageView) viewMenu.findViewById(yalantis.com.sidemenu.R.id.menu_item_image)).setImageResource(((Resourceble) this.list.get(i)).getImageRes());
            viewMenu.setVisibility(View.GONE);
            viewMenu.setEnabled(false);
            this.viewList.add(viewMenu);
            this.animatorListener.addViewToContainer(viewMenu);
            final double position = (double) i;
            double delay = 525.0D * (position / size);
            (new Handler()).postDelayed(new Runnable() {
                public void run() {
                    if (position < (double) ViewAnimator.this.viewList.size()) {
                        ViewAnimator.this.animateView((int) position);
                    }

                    if (position == (double) (ViewAnimator.this.viewList.size() - 1)) {
                        ViewAnimator.this.screenShotable.takeScreenShot();
                        ViewAnimator.this.setViewsClickable(true);
                    }

                }
            }, (long) delay);
        }

    }

    private void hideMenuContent() {
        this.setViewsClickable(false);
        double size = (double) this.list.size();

        for (int i = this.list.size(); i >= 0; --i) {
            final double position = (double) i;
            double delay = 525.0D * (position / size);
            (new Handler()).postDelayed(new Runnable() {
                public void run() {
                    if (position < (double) ViewAnimator.this.viewList.size()) {
                        ViewAnimator.this.animateHideView((int) position);
                    }

                }
            }, (long) delay);
        }

    }

    private void setViewsClickable(boolean clickable) {
        this.animatorListener.disableHomeButton();
        Iterator i$ = this.viewList.iterator();

        while (i$.hasNext()) {
            View view = (View) i$.next();
            view.setEnabled(clickable);
        }

    }

    private void animateView(int position) {
        final View view = (View) this.viewList.get(position);
        view.setVisibility(View.VISIBLE);
        FlipAnimation rotation = new FlipAnimation(90.0F, 0.0F, 0.0F, (float) view.getHeight() / 2.0F);
        rotation.setDuration(175L);
        rotation.setFillAfter(true);
        rotation.setInterpolator(new AccelerateInterpolator());
        rotation.setAnimationListener(new Animation.AnimationListener() {
            public void onAnimationStart(Animation animation) {
            }

            public void onAnimationEnd(Animation animation) {
                view.clearAnimation();
            }

            public void onAnimationRepeat(Animation animation) {
            }
        });
        view.startAnimation(rotation);
    }

    private void animateHideView(final int position) {
        final View view = (View) this.viewList.get(position);
        FlipAnimation rotation = new FlipAnimation(0.0F, 90.0F, 0.0F, (float) view.getHeight() / 2.0F);
        rotation.setDuration(175L);
        rotation.setFillAfter(true);
        rotation.setInterpolator(new AccelerateInterpolator());
        rotation.setAnimationListener(new Animation.AnimationListener() {
            public void onAnimationStart(Animation animation) {
            }

            public void onAnimationEnd(Animation animation) {
                view.clearAnimation();
                view.setVisibility(View.INVISIBLE);
                if (position == ViewAnimator.this.viewList.size() - 1) {
                    ViewAnimator.this.animatorListener.enableHomeButton();
                    ViewAnimator.this.drawerLayout.closeDrawers();
                }

            }

            public void onAnimationRepeat(Animation animation) {
            }
        });
        view.startAnimation(rotation);
    }

    private void switchItem(Resourceble slideMenuItem, int topPosition) {
        this.screenShotable = this.animatorListener.onSwitch(slideMenuItem, this.screenShotable, topPosition);
        this.hideMenuContent();
    }

    public interface ViewAnimatorListener {
        ScreenShotable onSwitch(Resourceble var1, ScreenShotable var2, int var3);

        void disableHomeButton();

        void enableHomeButton();

        void addViewToContainer(View var1);
    }
}