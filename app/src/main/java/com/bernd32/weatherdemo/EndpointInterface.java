package com.bernd32.weatherdemo;

import com.bernd32.weatherdemo.models.forecastdata.ForecastData;
import com.bernd32.weatherdemo.models.weatherdata.WeatherData;

import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Here we define the Retrofit endpoints with Observable return types to use it later
 * in RxJava.
 */

public interface EndpointInterface {

    @GET("data/2.5/weather")
    Observable<WeatherData> getCurrentConditions(@Query("units") String units,
                                                 @Query("APPID") String apiKey,
                                                 @Query("q") String city,
                                                 @Query("lang") String lang,
                                                 @Query("lat") String lat,
                                                 @Query("lon") String lon);

    @GET("data/2.5/forecast")
    Observable<ForecastData> getForecast(@Query("units") String units,
                                         @Query("APPID") String apiKey,
                                         @Query("q") String city,
                                         @Query("lang") String lang,
                                         @Query("lat") String lat,
                                         @Query("lon") String lon);
}
