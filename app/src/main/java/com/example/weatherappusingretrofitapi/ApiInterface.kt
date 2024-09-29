package com.example.weatherappusingretrofitapi

import com.example.weatherappusingretrofitapi.Modals.WeatherData
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiInterface {
//    @GET("data/2.5/weather")
//    fun getWeatherData(@Query("q") city_name : String,
//                       @Query("appid") app_id : String,
//                       @Query("units") units : String) : Call<WeatherData>

    @GET("data/2.5/weather")
    suspend fun getWeatherData(@Query("q") city_name : String,
                       @Query("appid") app_id : String,
                       @Query("units") units : String) : Response<WeatherData>
}