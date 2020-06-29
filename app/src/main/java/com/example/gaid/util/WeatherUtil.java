package com.example.gaid.util;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class WeatherUtil {
    private WeatherApi mGetApi;

    public WeatherUtil() {
        Retrofit mRetrofit = new Retrofit.Builder()
                                    .baseUrl(WeatherApi.BASE_URL)
                                    .addConverterFactory(GsonConverterFactory.create())
                                    .build();
        mGetApi = mRetrofit.create(WeatherApi.class);
    }

    public WeatherApi getApi() {
        return mGetApi;
    }
}
