package com.example.kotlinapp.wetherApi

import com.google.gson.annotations.SerializedName

data class ForecastResponse(
    @SerializedName("cod")
    val cod: String,
    @SerializedName("message")
    val message: Double,
    @SerializedName("cnt")
    val count: Int,
    @SerializedName("list")
    val hourlyForecasts: List<WeatherResponse>,
)
