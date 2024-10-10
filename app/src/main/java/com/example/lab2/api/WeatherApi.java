package com.example.lab2.api;

import com.example.lab2.models.ForecastResponse;


import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface WeatherApi {

    // Новый метод для прогноза
    @GET("forecast")
    Call<ForecastResponse> getForecast(@Query("q") String city, @Query("appid") String apiKey, @Query("units") String units);
}
