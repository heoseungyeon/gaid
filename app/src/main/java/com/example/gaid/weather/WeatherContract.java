package com.example.gaid.weather;

import com.example.gaid.model.Weather;

public interface WeatherContract {
    interface View {
        void showWeatherData(Weather weather);

        void showLoadError(String message);

        //void loadingStart();

        //void loadingEnd();

        //void reload();
    }

    interface UserActionsListener {
        void loadWeatherData();
    }
}
