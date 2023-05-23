package com.example.kotlinapp.wetherApi

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

//интерфейс для взаимодействия
interface WeatherApiService {
    //HTTP запрос get
    @GET("weather")
    suspend fun getCurrentWeather(
        @Query("q") city: String,
        @Query("appid") apiKey: String
    ): WeatherResponse //мы возвращаем объект WeatherResponse

    @GET("weather")
    suspend fun getCurrentWeatherByCoordinates(
        @Query("lat") latitude: Double,
        @Query("lon") longitude: Double,
        @Query("appid") apiKey: String
    ): Response<WeatherResponse>

    @GET("forecast")
    suspend fun getHourlyForecast(
        @Query("q") city: String,
        @Query("appid") apiKey: String,
    ): Response<ForecastResponse>

    @GET("forecast")
    suspend fun getHourlyForecastByCoordinates(
        @Query("lat") latitude: Double,
        @Query("lon") longitude: Double,
        @Query("appid") apiKey: String
    ): Response<ForecastResponse>
}

