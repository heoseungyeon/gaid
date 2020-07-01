package com.example.gaid.data;

import com.example.gaid.model.send_picture.SendPictureResponseDTO;
import com.example.gaid.util.RestApi;
import com.example.gaid.util.RestApiUtil;
import com.example.gaid.util.WeatherUtil;

import okhttp3.MultipartBody;
import retrofit2.Callback;
import retrofit2.Retrofit;

public class SendPictureRepository implements PictureRepository{

    private RestApiUtil mRestApiUtil;
    private MultipartBody.Part mImage;

    public SendPictureRepository(MultipartBody.Part image) {
        mRestApiUtil = new RestApiUtil();
        mImage = image;
    }

    @Override
    public boolean isAvailable() {
        return true;
    }

    @Override
    public void sendPictureData(Callback<SendPictureResponseDTO> callback) {
        Retrofit mRetrofit = mRestApiUtil.getRetrofitClient();
        RestApi restApi = mRetrofit.create(RestApi.class);
        restApi.send_picture(mImage).enqueue(callback);
    }
}
