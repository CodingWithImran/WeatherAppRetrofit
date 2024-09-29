package com.example.weatherappusingretrofitapi

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weatherappusingretrofitapi.Modals.WeatherData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
class WeatherViewModel : ViewModel() {
    // Private mutable LiveData for internal use
    private val _weatherData = MutableLiveData<WeatherData>()

    // Expose immutable LiveData for observing in UI
    val weatherData: LiveData<WeatherData> get() = _weatherData

    fun fetchWeatherData() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = RetrofitBuilder.api.getWeatherData("Khushab", Utils.API_ID, "metric")
                if (response.isSuccessful) {
                    response.body()?.let { weatherInfo ->
                        withContext(Dispatchers.Main) {
                            // Set value on the main thread
                            _weatherData.value = weatherInfo
                        }
                    }
                }
            } catch (exception: Exception) {
                Log.e("WeatherViewModel", "Error fetching weather data", exception)
            }
        }
    }
}
