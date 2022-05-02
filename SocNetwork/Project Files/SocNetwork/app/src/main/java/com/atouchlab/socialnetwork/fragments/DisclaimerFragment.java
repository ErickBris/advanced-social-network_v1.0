package com.atouchlab.socialnetwork.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.atouchlab.socialnetwork.R;
import com.atouchlab.socialnetwork.api.APIService;
import com.atouchlab.socialnetwork.api.GlobalAPI;
import com.atouchlab.socialnetwork.data.ResponseModel;
import com.atouchlab.socialnetwork.helpers.M;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class DisclaimerFragment extends Fragment {
    private TextView disclaimer;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View mView = inflater.inflate(R.layout.activity_disclaimer, container,
                false);
        disclaimer = (TextView) mView.findViewById(R.id.disclaimer);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(R.string.title_disclaimer);

        disclaimer.setMovementMethod(new ScrollingMovementMethod());
        getDisclaimer();
        return mView;
    }

    public void getDisclaimer() {
        M.showLoadingDialog(getActivity());
        GlobalAPI mGlobalAPI = APIService.createService(GlobalAPI.class, M.getToken(getActivity()));
        mGlobalAPI.getTerms(new Callback<ResponseModel>() {
            @Override
            public void success(ResponseModel responseModel, Response response) {
                disclaimer.setText(Html.fromHtml(responseModel.getMessage()));
                disclaimer.setMovementMethod(LinkMovementMethod.getInstance());
                M.hideLoadingDialog();
            }

            @Override
            public void failure(RetrofitError error) {
                M.hideLoadingDialog();
                disclaimer.setText(getString(R.string.ServerError));
            }
        });
    }
}
