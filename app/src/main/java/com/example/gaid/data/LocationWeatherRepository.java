package com.example.gaid.data;

import com.example.gaid.model.Weather;
import com.example.gaid.util.WeatherUtil;

import retrofit2.Callback;

public class LocationWeatherRepository implements WeatherRepository {

    private WeatherUtil mWeatherUtil;

    public LocationWeatherRepository() {
        mWeatherUtil = new WeatherUtil();
    }

    @Override
    public boolean isAvailable() {
        return true;
    }

    @Override
    public void getWeatherData(Callback<Weather> callback) {
        mWeatherUtil.getApi().getWeatherData().enqueue(callback);
    }
}
