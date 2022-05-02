package com.atouchlab.socialnetwork.animation;

import android.animation.ObjectAnimator;
import android.support.v7.widget.RecyclerView;

/**
 * Created by BeN_CheriF on 06/04/2015.
 */
public class AnimUtils {

    public static void animate(RecyclerView.ViewHolder holder, boolean goesDown) {

        ObjectAnimator animatorTranslateY = ObjectAnimator.ofFloat(holder.itemView, "translationY", goesDown == true ? 100 : -100, 0);
        animatorTranslateY.setDuration(500);
        animatorTranslateY.start();
    }
}
