package com.atouchlab.socialnetwork.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;

import com.atouchlab.socialnetwork.R;
import com.atouchlab.socialnetwork.api.APIService;
import com.atouchlab.socialnetwork.api.GlobalAPI;
import com.atouchlab.socialnetwork.data.ResponseModel;
import com.atouchlab.socialnetwork.helpers.M;
import com.squareup.picasso.Picasso;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class MapPreveiw extends Activity {
    public ImageView mapPreview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (M.getToken(this) == null) {
            Intent mIntent = new Intent(this, LoginActivity.class);
            startActivity(mIntent);
            finish();
        }
        setContentView(R.layout.activity_map_preveiw);
        mapPreview = (ImageView) findViewById(R.id.mapPreview);
        getMap(getIntent().getExtras().getString("place"));
    }

    private void getMap(String place) {
        M.showLoadingDialog(this);
        GlobalAPI mGlobalAPI = APIService.createService(GlobalAPI.class, M.getToken(this));
        mGlobalAPI.getMapImageUrl(place, new Callback<ResponseModel>() {
            @Override
            public void success(ResponseModel responseModel, Response response) {
                if (responseModel.isDone()) {
                    loadMapImage(responseModel.getMessage());
                } else {
                    M.T(MapPreveiw.this, responseModel.getMessage());
                }
                M.hideLoadingDialog();
            }

            @Override
            public void failure(RetrofitError error) {
                M.hideLoadingDialog();
                M.T(MapPreveiw.this, getString(R.string.ServerError));

            }
        });
    }

    private void loadMapImage(String url) {
        Picasso.with(this)
                .load(url + "size=400x400")
                .placeholder(R.drawable.image_holder)
                .error(R.drawable.image_holder)
                .into(mapPreview);
    }
}
