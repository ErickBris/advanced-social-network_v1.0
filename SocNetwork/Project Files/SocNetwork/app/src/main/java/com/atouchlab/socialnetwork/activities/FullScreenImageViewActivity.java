package com.atouchlab.socialnetwork.activities;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.Display;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;

import com.atouchlab.socialnetwork.R;
import com.atouchlab.socialnetwork.app.AppConst;
import com.atouchlab.socialnetwork.helpers.M;
import com.squareup.picasso.Picasso;

public class FullScreenImageViewActivity extends Activity implements View.OnClickListener {
    private LinearLayout llSetWallpaper, llDownloadWallpaper;

    private ImageView fullImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_screen_image_view);
        fullImageView = (ImageView) findViewById(R.id.imgFullscreen);
        llSetWallpaper = (LinearLayout) findViewById(R.id.llSetWallpaper);
        llDownloadWallpaper = (LinearLayout) findViewById(R.id.llDownloadWallpaper);
        llDownloadWallpaper.setOnClickListener(this);
        llSetWallpaper.setOnClickListener(this);
        if (getIntent().hasExtra("image")) {
            loadImage(getIntent().getExtras().getString("image"));
        } else {
            finish();
        }
    }

    private void loadImage(String image) {
        Picasso.with(this)
                .load(AppConst.IMAGE_URL + image)
                .into(fullImageView);
        Bitmap bitmap = ((BitmapDrawable) fullImageView.getDrawable())
                .getBitmap();
        adjustImageAspect(bitmap.getWidth(), bitmap.getHeight());
    }

    private void adjustImageAspect(int bWidth, int bHeight) {
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);

        if (bWidth == 0 || bHeight == 0)
            return;

        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int sHeight = size.y;

        int new_width = (int) Math.floor((double) bWidth * (double) sHeight
                / (double) bHeight);
        params.width = new_width;
        params.height = sHeight;

        fullImageView.setLayoutParams(params);
    }

    @Override
    public void onClick(View v) {
        Bitmap bitmap = ((BitmapDrawable) fullImageView.getDrawable())
                .getBitmap();
        switch (v.getId()) {

            case R.id.llDownloadWallpaper:
                M.saveImageToSDCard(this, bitmap);
                break;

            case R.id.llSetWallpaper:
                M.setAsWallpaper(this, bitmap);
                break;
            default:
                break;
        }
    }
}
