package com.example.gaid.weather;

import com.example.gaid.data.WeatherRepository;
import com.example.gaid.model.Weather;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class WeatherPresenter implements WeatherContract.UserActionsListener {

    private WeatherRepository mRepository;
    private WeatherContract.View mView;

    public WeatherPresenter(WeatherRepository repository, WeatherContract.View view) {
        mRepository = repository;
        mView = view;
    }


    @Override
    public void loadWeatherData() {
        if(mRepository.isAvailable()) {
            mRepository.getWeatherData(new Callback<Weather>() {
                @Override
                public void onResponse(Call<Weather> call, Response<Weather> response) {
                    mView.showWeatherData(response.body());
                }

                @Override
                public void onFailure(Call<Weather> call, Throwable t) {
                    mView.showLoadError(t.getMessage());
                }
            });
        }
    }
}
