package com.atouchlab.socialnetwork.helpers;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.style.ClickableSpan;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.atouchlab.socialnetwork.activities.HashTagActivity;

/**
 * Created by Ben Cherif on 09/06/2015.
 */
public class HashTag extends ClickableSpan {
    Context context;
    TextPaint textPaint;
    public HashTag(Context ctx) {
        super();
        context = ctx;
    }

    @Override
    public void updateDrawState(TextPaint ds) {
        textPaint = ds;
        ds.setColor(ds.linkColor);
        ds.setARGB(255, 30, 144, 255);
        ds.setTypeface(Typeface.DEFAULT_BOLD);
    }

    @Override
    public void onClick(View widget) {
        TextView tv = (TextView) widget;
        Spanned s = (Spanned) tv.getText();
        int start = s.getSpanStart(this);
        int end = s.getSpanEnd(this);
        String theWord = s.subSequence(start + 1, end).toString();
        Intent intent = new Intent(context,HashTagActivity.class);
        intent.putExtra("hashtag",theWord);
        context.startActivity(intent);

    }
}