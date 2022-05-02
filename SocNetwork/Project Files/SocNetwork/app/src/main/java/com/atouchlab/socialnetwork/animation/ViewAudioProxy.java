/*
 * This is the source code of Telegram for Android v. 1.7.x.
 * It is licensed under GNU GPL v. 2 or later.
 * You should have received a copy of the license in this archive (see LICENSE).
 *
 * Copyright Nikolai Kudashov, 2013-2014.
 */

package com.atouchlab.socialnetwork.animation;

import android.view.View;

public class ViewAudioProxy {

    public static float getAlpha(View view) {
        if (ViewAudio1.NEED_PROXY) {
            return ViewAudio1.wrap(view).getAlpha();
        } else {
            return view.getAlpha();
        }
    }

    public static void setAlpha(View view, float alpha) {
        if (ViewAudio1.NEED_PROXY) {
            ViewAudio1.wrap(view).setAlpha(alpha);
        } else {
            view.setAlpha(alpha);
        }
    }

    public static float getPivotX(View view) {
        if (ViewAudio1.NEED_PROXY) {
            return ViewAudio1.wrap(view).getPivotX();
        } else {
            return view.getPivotX();
        }
    }

    public static void setPivotX(View view, float pivotX) {
        if (ViewAudio1.NEED_PROXY) {
            ViewAudio1.wrap(view).setPivotX(pivotX);
        } else {
            view.setPivotX(pivotX);
        }
    }

    public static float getPivotY(View view) {
        if (ViewAudio1.NEED_PROXY) {
            return ViewAudio1.wrap(view).getPivotY();
        } else {
            return view.getPivotY();
        }
    }

    public static void setPivotY(View view, float pivotY) {
        if (ViewAudio1.NEED_PROXY) {
            ViewAudio1.wrap(view).setPivotY(pivotY);
        } else {
            view.setPivotY(pivotY);
        }
    }

    public static float getRotation(View view) {
        if (ViewAudio1.NEED_PROXY) {
            return ViewAudio1.wrap(view).getRotation();
        } else {
            return view.getRotation();
        }
    }

    public static void setRotation(View view, float rotation) {
        if (ViewAudio1.NEED_PROXY) {
            ViewAudio1.wrap(view).setRotation(rotation);
        } else {
            view.setRotation(rotation);
        }
    }

    public static float getRotationX(View view) {
        if (ViewAudio1.NEED_PROXY) {
            return ViewAudio1.wrap(view).getRotationX();
        } else {
            return view.getRotationX();
        }
    }

    public void setRotationX(View view, float rotationX) {
        if (ViewAudio1.NEED_PROXY) {
            ViewAudio1.wrap(view).setRotationX(rotationX);
        } else {
            view.setRotationX(rotationX);
        }
    }

    public static float getRotationY(View view) {
        if (ViewAudio1.NEED_PROXY) {
            return ViewAudio1.wrap(view).getRotationY();
        } else {
            return view.getRotationY();
        }
    }

    public void setRotationY(View view, float rotationY) {
        if (ViewAudio1.NEED_PROXY) {
            ViewAudio1.wrap(view).setRotationY(rotationY);
        } else {
            view.setRotationY(rotationY);
        }
    }

    public static float getScaleX(View view) {
        if (ViewAudio1.NEED_PROXY) {
            return ViewAudio1.wrap(view).getScaleX();
        } else {
            return view.getScaleX();
        }
    }

    public static void setScaleX(View view, float scaleX) {
        if (ViewAudio1.NEED_PROXY) {
            ViewAudio1.wrap(view).setScaleX(scaleX);
        } else {
            view.setScaleX(scaleX);
        }
    }

    public static float getScaleY(View view) {
        if (ViewAudio1.NEED_PROXY) {
            return ViewAudio1.wrap(view).getScaleY();
        } else {
            return view.getScaleY();
        }
    }

    public static void setScaleY(View view, float scaleY) {
        if (ViewAudio1.NEED_PROXY) {
            ViewAudio1.wrap(view).setScaleY(scaleY);
        } else {
            view.setScaleY(scaleY);
        }
    }

    public static int getScrollX(View view) {
        if (ViewAudio1.NEED_PROXY) {
            return ViewAudio1.wrap(view).getScrollX();
        } else {
            return view.getScrollX();
        }
    }

    public static void setScrollX(View view, int value) {
        if (ViewAudio1.NEED_PROXY) {
            ViewAudio1.wrap(view).setScrollX(value);
        } else {
            view.setScrollX(value);
        }
    }

    public static int getScrollY(View view) {
        if (ViewAudio1.NEED_PROXY) {
            return ViewAudio1.wrap(view).getScrollY();
        } else {
            return view.getScrollY();
        }
    }

    public static void setScrollY(View view, int value) {
        if (ViewAudio1.NEED_PROXY) {
            ViewAudio1.wrap(view).setScrollY(value);
        } else {
            view.setScrollY(value);
        }
    }

    public static float getTranslationX(View view) {
        if (ViewAudio1.NEED_PROXY) {
            return ViewAudio1.wrap(view).getTranslationX();
        } else {
            return view.getTranslationX();
        }
    }

    public static void setTranslationX(View view, float translationX) {
        if (ViewAudio1.NEED_PROXY) {
            ViewAudio1.wrap(view).setTranslationX(translationX);
        } else {
            view.setTranslationX(translationX);
        }
    }

    public static float getTranslationY(View view) {
        if (ViewAudio1.NEED_PROXY) {
            return ViewAudio1.wrap(view).getTranslationY();
        } else {
            return view.getTranslationY();
        }
    }

    public static void setTranslationY(View view, float translationY) {
        if (ViewAudio1.NEED_PROXY) {
            ViewAudio1.wrap(view).setTranslationY(translationY);
        } else {
            view.setTranslationY(translationY);
        }
    }

    public static float getX(View view) {
        if (ViewAudio1.NEED_PROXY) {
            return ViewAudio1.wrap(view).getX();
        } else {
            return view.getX();
        }
    }

    public static void setX(View view, float x) {
        if (ViewAudio1.NEED_PROXY) {
            ViewAudio1.wrap(view).setX(x);
        } else {
            view.setX(x);
        }
    }

    public static float getY(View view) {
        if (ViewAudio1.NEED_PROXY) {
            return ViewAudio1.wrap(view).getY();
        } else {
            return view.getY();
        }
    }

    public static void setY(View view, float y) {
        if (ViewAudio1.NEED_PROXY) {
            ViewAudio1.wrap(view).setY(y);
        } else {
            view.setY(y);
        }
    }

    public static Object wrap(View view) {
        if (ViewAudio1.NEED_PROXY) {
            return ViewAudio1.wrap(view);
        } else {
            return view;
        }
    }
}
