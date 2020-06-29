package com.example.gaid.data;

import com.example.gaid.model.Weather;

import retrofit2.Callback;

public interface WeatherRepository {
    boolean isAvailable();
    void getWeatherData(Callback<Weather> callback);
}
