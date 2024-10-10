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

        @SerializedName("wind")
        private Wind wind;

        public String getDateTime() {
            return dateTime;
        }

        public Main getMain() {
            return main;
        }

        public List<Weather> getWeatherList() {
            return weatherList;
        }

        public Wind getWind() {
            return wind;
        }
    }

    public static class Main {
        @SerializedName("temp")
        private double temp;

        @SerializedName("humidity")  // Поле для влажности
        private int humidity;

        public double getTemp() {
            return temp;
        }

        public int getHumidity() {
            return humidity;
        }
    }

    public static class Weather {
        @SerializedName("description")
        private String description;

        public String getDescription() {
            return description;
        }
    }

    public static class Wind {
        @SerializedName("speed")  // Поле для скорости ветра
        private double speed;

        public double getSpeed() {
            return speed;
        }
    }
}
