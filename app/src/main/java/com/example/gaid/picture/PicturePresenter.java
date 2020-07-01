package com.example.gaid.picture;

import com.example.gaid.data.PictureRepository;
import com.example.gaid.model.send_picture.SendPictureResponseDTO;
import com.example.gaid.util.RestApi;
import com.example.gaid.util.RestApiUtil;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class PicturePresenter implements PictureContract.Presenter {

    private PictureContract.View mView;
    private PictureRepository mPictureRepository;

    public PicturePresenter(PictureRepository pictureRepository, PictureContract.View view) {
        mView = view;
        mPictureRepository = pictureRepository;
    }

    @Override
    public void sendPictureDataToServer(MultipartBody.Part image) {
        if(mPictureRepository.isAvailable()) {
            mPictureRepository.sendPictureData(new Callback<SendPictureResponseDTO>() {
                @Override
                public void onResponse(Call<SendPictureResponseDTO> call, Response<SendPictureResponseDTO> response) {
                    mView.showServerResponse(response.body());
                }

                @Override
                public void onFailure(Call<SendPictureResponseDTO> call, Throwable t) {
                    mView.showError(t.getMessage());
                }
            });
        }

    }
}
