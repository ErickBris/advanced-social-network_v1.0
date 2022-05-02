package com.atouchlab.socialnetwork.helpers;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.app.WallpaperManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Environment;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.util.Patterns;
import android.widget.ImageView;
import android.widget.Toast;

import com.atouchlab.socialnetwork.R;
import com.atouchlab.socialnetwork.app.AppConst;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Random;

public class M {
    static ProgressDialog pDialog;
    private static SharedPreferences mSharedPreferences;

    public static void showLoadingDialog(Context mContext) {
        pDialog = new ProgressDialog(mContext);
        pDialog.setMessage(mContext.getString(R.string.please_wait));
        pDialog.setIndeterminate(true);
        pDialog.setCancelable(false);
        pDialog.show();
    }

    public static void hideLoadingDialog() {
        if (pDialog.isShowing()) {
            pDialog.dismiss();
        }
    }

    public static void T(Context mContext, String Message) {
        Toast.makeText(mContext, Message, Toast.LENGTH_SHORT).show();
    }

    public static void L(String Message) {
        Log.e(AppConst.TAG, Message);
    }

    public static boolean setToken(String token, Context mContext) {
        mSharedPreferences = mContext.getSharedPreferences("settings", 0);
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putString("token", token);
        return editor.commit();
    }

    public static String getToken(Context mContext) {
        mSharedPreferences = mContext.getSharedPreferences("settings", 0);
        return mSharedPreferences.getString("token", null);
    }

    public static void logOut(Context mContext) {
        setID(0, mContext);
        setToken(null, mContext);
    }

    public static boolean setID(int ID, Context mContext) {
        mSharedPreferences = mContext.getSharedPreferences("settings", 0);
        SharedPreferences.Editor editor = mSharedPreferences.edit();

        editor.putInt("id", ID);
        return editor.commit();
    }

    public static int getID(Context mContext) {
        mSharedPreferences = mContext.getSharedPreferences("settings", 0);
        return mSharedPreferences.getInt("id", 0);
    }

    public static boolean isValidUrl(String text) {
        return Patterns.WEB_URL.matcher(text).matches();
    }

    public static void saveImageToSDCard(Context mContext, Bitmap bitmap) {
        File myDir = new File(
                Environment
                        .getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                mContext.getString(R.string.app_name) + " Images");

        myDir.mkdirs();
        Random generator = new Random();
        int n = 10000;
        n = generator.nextInt(n);
        String fname = "image-" + n + ".jpg";
        File file = new File(myDir, fname);
        if (file.exists())
            file.delete();
        try {
            FileOutputStream out = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);
            out.flush();
            out.close();
            T(mContext, "Image saved in " + mContext.getString(R.string.app_name) + " Images");
        } catch (Exception e) {
            e.printStackTrace();
            T(mContext, mContext.getString(R.string.image_failed));
        }
    }

    public static void setAsWallpaper(Context mContext, Bitmap bitmap) {
        try {
            WallpaperManager wm = WallpaperManager.getInstance(mContext);

            wm.setBitmap(bitmap);
            T(mContext, mContext.getString(R.string.set_as_wallpaper));
        } catch (Exception e) {
            e.printStackTrace();
            T(mContext, e.getMessage());
        }
    }
    public static Uri getLocalBitmapUri(ImageView imageView) {
        // Extract Bitmap from ImageView drawable
        Drawable drawable = imageView.getDrawable();
        Bitmap bmp = null;
        if (drawable instanceof BitmapDrawable){
            bmp = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
        } else {
            return null;
        }
        // Store image to default external storage directory
        Uri bmpUri = null;
        try {
            File file =  new File(Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_DOWNLOADS), "share_image_" + System.currentTimeMillis() + ".png");
            file.getParentFile().mkdirs();
            FileOutputStream out = new FileOutputStream(file);
            bmp.compress(Bitmap.CompressFormat.PNG, 90, out);
            out.close();
            bmpUri = Uri.fromFile(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bmpUri;
    }
    public static void showNotification(Context mContext, Intent resultIntent, String title, String text, int id) {
        PendingIntent resultPendingIntent = PendingIntent.getActivity(mContext, 0,
                resultIntent, PendingIntent.FLAG_ONE_SHOT);

        NotificationCompat.Builder mNotifyBuilder;
        NotificationManager mNotificationManager;

        mNotificationManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);

        mNotifyBuilder = new NotificationCompat.Builder(mContext)
                .setContentTitle(title)
                .setContentText(text)

                .setSmallIcon(R.mipmap.ic_launcher);

        mNotifyBuilder.setContentIntent(resultPendingIntent);

        int defaults = 0;
        defaults = defaults | Notification.DEFAULT_LIGHTS;
        defaults = defaults | Notification.DEFAULT_VIBRATE;
        defaults = defaults | Notification.DEFAULT_SOUND;

        mNotifyBuilder.setDefaults(defaults);
        mNotifyBuilder.setAutoCancel(true);

        mNotificationManager.notify(id, mNotifyBuilder.build());
    }

    public static SharedPreferences.Editor editSharedPref(Context mContext) {
        mSharedPreferences = mContext.getSharedPreferences("settings", 0);
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        return editor;
    }
    public static SharedPreferences getSharedPref(Context mContext) {
        return  mContext.getSharedPreferences("settings", 0);
    }

}
