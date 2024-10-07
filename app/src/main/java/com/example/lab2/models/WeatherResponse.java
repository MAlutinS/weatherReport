package com.example.lab2.models;

import com.google.gson.annotations.SerializedName;

public class WeatherResponse {

    @SerializedName("name")
    private String name;

    @SerializedName("main")
    private Main main;

    @SerializedName("wind")
    private Wind wind;

    public String getName() {
        return name;
    }

    public Main getMain() {
        return main;
    }

    public Wind getWind() {
        return wind;
    }

    // Класс для хранения температуры и влажности
    public class Main {
        @SerializedName("temp")
        private double temp;

        @SerializedName("humidity")
        private int humidity;

        public double getTemp() {
            return temp;
        }

        public int getHumidity() {
            return humidity;
        }
    }

    // Класс для хранения скорости ветра
    public class Wind {
        @SerializedName("speed")
        private double speed;

        public double getSpeed() {
            return speed;
        }
    }
}