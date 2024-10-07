package com.example.lab2;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Switch;

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
    private Switch themeSwitch;
    private static final String PREFS_NAME = "theme_prefs";
    private static final String KEY_IS_DARK_MODE = "is_dark_mode";
    private TextView weatherTextView;
    private EditText cityEditText;
    private Button getWeatherButton;

    // Твой ключ API
    private final String API_KEY = "2bba9b0a51e507ac78f51f61f46cddf1";

    // Сохранение состояния темы
    private void saveTheme(boolean isDarkMode) {
        SharedPreferences.Editor editor = getSharedPreferences(PREFS_NAME, MODE_PRIVATE).edit();
        editor.putBoolean(KEY_IS_DARK_MODE, isDarkMode);
        editor.apply();
    }

    // Загрузка темы
    private void loadTheme() {
        SharedPreferences preferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        boolean isDarkMode = preferences.getBoolean(KEY_IS_DARK_MODE, false);
        if (isDarkMode) {
            setTheme(R.style.Theme_Lab2_Dark);
        } else {
            setTheme(R.style.Theme_Lab2);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        // Загрузка сохраненной темы перед созданием UI
        loadTheme();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Инициализация элементов интерфейса
        themeSwitch = findViewById(R.id.themeSwitch);
        weatherTextView = findViewById(R.id.weatherTextView);
        cityEditText = findViewById(R.id.cityEditText);
        getWeatherButton = findViewById(R.id.getWeatherButton);

        // Чтение сохраненного состояния переключателя темы
        SharedPreferences preferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        boolean isDarkMode = preferences.getBoolean(KEY_IS_DARK_MODE, false);
        themeSwitch.setChecked(isDarkMode);

        // Обработчик изменения темы
        themeSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    setTheme(R.style.Theme_Lab2_Dark);
                    saveTheme(true);
                } else {
                    setTheme(R.style.Theme_Lab2);
                    saveTheme(false);
                }
                // Перезапуск activity для применения изменений темы
                recreate();
            }
        });

        // Обработчик нажатия кнопки для получения погоды
        getWeatherButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String city = cityEditText.getText().toString().trim();

                if (!city.isEmpty()) {
                    getWeatherData(city);  // Вызов метода для запроса погоды
                } else {
                    Toast.makeText(MainActivity.this, "Please enter a city name", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    // Метод для получения данных о погоде с помощью Retrofit
    private void getWeatherData(String city) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://api.openweathermap.org/data/2.5/") // Базовый URL для API
                .addConverterFactory(GsonConverterFactory.create()) // Конвертер для работы с JSON
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

                    weatherTextView.setText(weatherInfo); // Отображение данных о погоде
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
