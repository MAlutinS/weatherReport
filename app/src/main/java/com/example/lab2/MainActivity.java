package com.example.lab2;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.lab2.api.WeatherApi;
import com.example.lab2.models.WeatherResponse;

import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {

    // Объявляем переменные для элементов интерфейса
    private TextView weatherTextView;
    private EditText cityEditText;
    private Button getWeatherButton;

    private final String API_KEY = "2bba9b0a51e507ac78f51f61f46cddf1"; // Вставь сюда свой API-ключ

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Инициализируем элементы интерфейса
        weatherTextView = findViewById(R.id.weatherTextView);
        cityEditText = findViewById(R.id.cityEditText);
        getWeatherButton = findViewById(R.id.getWeatherButton);

        // Устанавливаем обработчик для кнопки
        getWeatherButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Получаем введенный пользователем город
                String city = cityEditText.getText().toString().trim();

                // Проверяем, что город введен
                if (!city.isEmpty()) {
                    // Вызываем метод для получения погоды
                    getWeatherData(city);
                } else {
                    // Если поле пустое, выводим сообщение
                    Toast.makeText(MainActivity.this, "Please enter a city name", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    // Метод для получения данных о погоде с помощью Retrofit
    private void getWeatherData(String city) {
        // Создание объекта Retrofit
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://api.openweathermap.org/data/2.5/") // Базовый URL для API
                .addConverterFactory(GsonConverterFactory.create()) // Использование конвертера Gson
                .build();

        // Создание экземпляра API
        WeatherApi weatherApi = retrofit.create(WeatherApi.class);

        // Вызов метода для получения данных о погоде
        Call<WeatherResponse> call = weatherApi.getWeather(city, API_KEY, "metric");

        // Асинхронный запрос к API
        call.enqueue(new Callback<WeatherResponse>() {
            @Override
            public void onResponse(Call<WeatherResponse> call, Response<WeatherResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    // Получаем данные и отображаем их в TextView
                    WeatherResponse weatherResponse = response.body();
                    String weatherInfo = "City: " + Objects.requireNonNull(weatherResponse).getName() + "\n" +
                            "Temperature: " + weatherResponse.getMain().getTemp() + "°C\n" +
                            "Humidity: " + weatherResponse.getMain().getHumidity() + "%\n" +
                            "Wind Speed: " + weatherResponse.getWind().getSpeed() + " m/s";

                    weatherTextView.setText(weatherInfo); // Отображаем данные
                } else {
                    Toast.makeText(MainActivity.this, "Error in response", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<WeatherResponse> call, Throwable t) {
                Log.e("MainActivity", "API call failed: " + t.getMessage());
                Toast.makeText(MainActivity.this, "Failed to load weather data", Toast.LENGTH_SHORT).show();
            }
        });
    }
}