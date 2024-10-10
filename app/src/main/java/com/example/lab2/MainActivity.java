package com.example.lab2;

import java.util.List;

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

import android.net.ConnectivityManager;
import android.content.Context;
import android.net.NetworkInfo;

import androidx.appcompat.app.AppCompatActivity;

import com.example.lab2.api.WeatherApi;
import com.example.lab2.models.ForecastResponse;

import okhttp3.OkHttpClient;
import java.util.concurrent.TimeUnit;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {

    private Switch themeSwitch;
    private Switch unitsSwitch;
    private static final String PREFS_NAME = "theme_prefs";
    private static final String KEY_IS_DARK_MODE = "is_dark_mode";
    private static final String KEY_UNITS = "units";
    private TextView weatherTextView;
    private EditText cityEditText;
    private Button getWeatherButton;

    private final String API_KEY = "2bba9b0a51e507ac78f51f61f46cddf1";  // Проверь, что API-ключ правильный
    private String units = "metric";  // По умолчанию используем Цельсий

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loadTheme();  // Загрузка сохраненной темы перед созданием UI
        setContentView(R.layout.activity_main);

        // Инициализация элементов интерфейса
        themeSwitch = findViewById(R.id.themeSwitch);
        unitsSwitch = findViewById(R.id.unitsSwitch);  // Инициализируем переключатель для единиц измерения
        weatherTextView = findViewById(R.id.weatherTextView);
        cityEditText = findViewById(R.id.cityEditText);
        getWeatherButton = findViewById(R.id.getWeatherButton);

        // Восстановление сохраненной темы
        SharedPreferences preferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        boolean isDarkMode = preferences.getBoolean(KEY_IS_DARK_MODE, false);
        themeSwitch.setChecked(isDarkMode);

        // Проверка сохраненной системы измерений
        units = preferences.getString(KEY_UNITS, "metric");
        unitsSwitch.setChecked(units.equals("imperial"));

        // Обработчик переключателя для смены единиц измерения
        unitsSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    units = "imperial";  // Если переключатель включен, используем Фаренгейты
                    Toast.makeText(MainActivity.this, "Using Fahrenheit", Toast.LENGTH_SHORT).show();
                } else {
                    units = "metric";  // Иначе используем Цельсий
                    Toast.makeText(MainActivity.this, "Using Celsius", Toast.LENGTH_SHORT).show();
                }
                // Сохраняем выбранную систему измерений
                saveUnits(units);
            }
        });

        // Обработчик для изменения темы
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
                recreate();
            }
        });

        // Обработчик кнопки получения прогноза
        getWeatherButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String city = cityEditText.getText().toString().trim();
                if (!city.isEmpty()) {
                    if (isNetworkAvailable()) {
                        getForecastData(city);
                    } else {
                        Toast.makeText(MainActivity.this, "No internet connection", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(MainActivity.this, "Please enter a city name", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    // Сохранение выбранной системы измерений
    private void saveUnits(String units) {
        SharedPreferences.Editor editor = getSharedPreferences(PREFS_NAME, MODE_PRIVATE).edit();
        editor.putString(KEY_UNITS, units);
        editor.apply();
    }

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

    // Метод для проверки интернет-соединения
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null) {
            android.net.NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo();
            return activeNetwork != null && activeNetwork.isConnected();
        }
        return false;
    }

    // Метод для получения данных о погоде
    private void getForecastData(String city) {
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://api.openweathermap.org/data/2.5/")
                .addConverterFactory(GsonConverterFactory.create())
                .client(okHttpClient)
                .build();

        WeatherApi weatherApi = retrofit.create(WeatherApi.class);

        Call<ForecastResponse> call = weatherApi.getForecast(city, API_KEY, units);

        call.enqueue(new Callback<ForecastResponse>() {
            @Override
            public void onResponse(Call<ForecastResponse> call, Response<ForecastResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    StringBuilder forecastInfo = new StringBuilder();
                    List<ForecastResponse.ForecastItem> forecastList = response.body().getForecastList();

                    // Фильтруем прогнозы, оставляя только каждые 12 часов
                    for (int i = 0; i < forecastList.size(); i += 4) {  // 4 интервала по 3 часа = 12 часов
                        ForecastResponse.ForecastItem item = forecastList.get(i);

                        String date = item.getDateTime();
                        double temp = item.getMain().getTemp();
                        String description = item.getWeatherList().get(0).getDescription();
                        String unitLabel = units.equals("metric") ? "°C" : "°F";

                        forecastInfo.append("Date: ").append(date).append("\n")
                                .append("Temp: ").append(temp).append(unitLabel).append("\n")
                                .append("Weather: ").append(description).append("\n\n");
                    }

                    weatherTextView.setText(forecastInfo.toString());
                } else {
                    Log.e("MainActivity", "Error in response: " + response.message());
                    Toast.makeText(MainActivity.this, "Error in forecast response: " + response.message(), Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<ForecastResponse> call, Throwable t) {
                Log.e("MainActivity", "API call failed: " + t.getMessage());
                Toast.makeText(MainActivity.this, "Failed to load forecast data: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
}
