package com.example.gaid.util;

import com.example.gaid.model.Weather;

import retrofit2.Call;
import retrofit2.http.GET;

public interface WeatherApi {
    String BASE_URL = "https://api.darksky.net/forecast/";
    //String exclude = "hourly,daily";

    // 쿼리
    @GET("2d32bcfe938dc43f9f32db76ebf8c449/37.551119,127.075773?exclude=hourly,daily")
    Call<Weather> getWeatherData();
}
