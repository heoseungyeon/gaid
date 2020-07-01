package com.example.gaid.data;

import com.example.gaid.model.send_picture.SendPictureResponseDTO;

import okhttp3.MultipartBody;
import retrofit2.Callback;

public interface PictureRepository {
    boolean isAvailable();
    void sendPictureData(Callback<SendPictureResponseDTO> callback);
}
