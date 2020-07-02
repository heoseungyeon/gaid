package com.example.gaid.util;

import com.example.gaid.model.get_case.GetCaseRequestDTO;
import com.example.gaid.model.get_case.GetCaseResponseDTO;
import com.example.gaid.model.get_info.GetInfoRequestDTO;
import com.example.gaid.model.get_info.GetInfoResponseDTO;
import com.example.gaid.model.send_picture.SendPictureResponseDTO;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface RestApi {
    String BASE_URL = "http://172.16.16.136:8080/";

    @Multipart
    @POST("upload") //여기다가 HTTP 통신 방식 넣어야 함.
    Call<SendPictureResponseDTO> send_picture(@Part MultipartBody.Part files);

    @Multipart
    @POST("getmorph")
    Call<GetCaseResponseDTO> get_case(@Part("morph") RequestBody requestBody);

    @Multipart
    @POST("room")
    Call<GetInfoResponseDTO> get_info(@Part("roomNo") RequestBody requestBody);
}