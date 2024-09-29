package com.example.weatherappusingretrofitapi.Activities

import android.os.Bundle
import android.text.Html
import android.widget.SearchView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.weatherappusingretrofitapi.Modals.WeatherData
import com.example.weatherappusingretrofitapi.WeatherViewModel
import com.example.weatherappusingretrofitapi.databinding.ActivityMainBinding
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MainActivity : AppCompatActivity() {

    // View binding
    val binding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        // Initialize SearchView and handle text submit
        var viewModal = ViewModelProvider(this@MainActivity).get(WeatherViewModel::class.java)
        viewModal.fetchWeatherData()

        val searchView = binding.searchView
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                // Safely handle null query input
                query?.let {
                    viewModal.weatherData.observe(this@MainActivity, Observer {WeatherData->
                        updateUI(WeatherData)
                    })
                } ?: run {
                    showError("Please enter a valid city name")
                }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return true
            }
        })



    }

    // Function to make API call and fetch weather data

  /* Call API using Retrofit with Callback Functions:
    private fun getRetrofitData(cityName: String) {
        val api: ApiInterface = Retrofit.Builder()
            .baseUrl("https://api.openweathermap.org/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiInterface::class.java)

        val response = api.getWeatherData(cityName, Utils.API_ID, "metric")

        // Enqueue the API call
        response.enqueue(object : Callback<WeatherData?> {
            override fun onResponse(call: Call<WeatherData?>, response: Response<WeatherData?>) {
                if (response.isSuccessful && response.body() != null) {
                    val weatherData = response.body()!!
                    // Safely access data and update UI
                    updateUI(weatherData)
                } else {
                    showError("Failed to retrieve weather data. Please try again.")
                }
            }

            override fun onFailure(call: Call<WeatherData?>, t: Throwable) {
                // Handle network failure or other issues
                showError("Unable to connect. Please check your internet connection.")
            }
        })
    }
    */

    // using Retrofit with coroutines


    // Function to update the UI with the fetched weather data
    private fun updateUI(weatherData: WeatherData) {
        // Safely set data to views
        binding.cityName.text = weatherData.name ?: "Unknown City"

        val temperature = weatherData.main?.temp?.toInt() ?: 0  // Handle null-safety for temperature
        val tempText = "$temperature<sup>°C</sup>"
        binding.tempToday.text = Html.fromHtml(tempText, Html.FROM_HTML_MODE_LEGACY)

        // Handle minimum and maximum temperature safely
        val minTemp = weatherData.main?.temp_min?.toInt() ?: 0
        val maxTemp = weatherData.main?.temp_max?.toInt() ?: 0
        binding.minTemp.text = Html.fromHtml("Min: $minTemp°C")
        binding.maxTemp2.text = Html.fromHtml("Max: $maxTemp°C")

        // Update additional weather information
        binding.today.text = today()
        binding.todayDate.text = currentDate()

        binding.humidity.text = weatherData.main?.humidity?.toString() ?: "N/A"
        binding.windspeed.text = weatherData.wind?.speed?.toString() ?: "N/A"

// Convert Unix timestamps for sunrise/sunset to human-readable time
        binding.sunrise.text = weatherData.sys?.sunrise.toString()
        binding.sunset.text = weatherData.sys?.sunset.toString()

        // Handle sea level
        binding.sea.text = weatherData.main?.sea_level?.toString() ?: "N/A"
    }

    // Function to format Unix timestamp into human-readable time
    private fun formatUnixTime(unixTime: Long): String {
        return if (unixTime != 0L) {
            val sdf = SimpleDateFormat("hh:mm a", Locale.getDefault())
            sdf.format(Date(unixTime * 1000))  // Convert Unix time to milliseconds
        } else {
            "N/A"
        }
    }

    // Function to display error messages
    private fun showError(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    // Function to get current day (e.g., Monday)
    private fun today(): String {
        val sdf = SimpleDateFormat("EEEE", Locale.getDefault())
        return sdf.format(Date())
    }

    // Function to get current date (e.g., 26 September 2024)
    private fun currentDate(): String {
        val sdf = SimpleDateFormat("dd MMMM yyyy", Locale.getDefault())
        return sdf.format(Date())
    }
}
