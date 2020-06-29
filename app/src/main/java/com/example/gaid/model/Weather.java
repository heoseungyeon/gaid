package com.example.gaid.model;

import com.example.gaid.model.weather_material.Currently;

public class Weather {

    private Currently currently;

    public Currently getCurrently() {
        return currently;
    }

    public void setCurrently(Currently currently) {
        this.currently = currently;
    }
}
