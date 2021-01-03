package com.r.weatherapp.Interface;

import com.r.weatherapp.common.Root;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface weatherapi {
    @GET("weather")
    Call<Root> weatherCalls(@Query("q") CharSequence countryName, @Query("units") String units, @Query("appid") String id);
    @GET("weather")
    Call<Root> Lat_Lon_calls(@Query("lat") Double Lat, @Query("lon") Double Long,@Query("units") String units, @Query("appid") String id);
}
