package com.example.lab2.models;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class ForecastResponse {

    @SerializedName("list")
    private List<ForecastItem> forecastList;

    public List<ForecastItem> getForecastList() {
        return forecastList;
    }

    public static class ForecastItem {
        @SerializedName("dt_txt")
        private String dateTime;

        @SerializedName("main")
        private Main main;

        @SerializedName("weather")
        private List<Weather> weatherList;

        public String getDateTime() {
            return dateTime;
        }

        public Main getMain() {
            return main;
        }

        public List<Weather> getWeatherList() {
            return weatherList;
        }
    }

    public static class Main {
        @SerializedName("temp")
        private double temp;

        public double getTemp() {
            return temp;
        }
    }

    public static class Weather {
        @SerializedName("description")
        private String description;

        public String getDescription() {
            return description;
        }
    }
}
