package com.example.gaid.picture;

import com.example.gaid.model.send_picture.SendPictureResponseDTO;

import okhttp3.MultipartBody;

public interface PictureContract {

    interface View {
        void showServerResponse(SendPictureResponseDTO sendPictureResponseDTO);

        void showError(String message);
    }

    interface Presenter {
        void sendPictureDataToServer(MultipartBody.Part image);
    }
}
