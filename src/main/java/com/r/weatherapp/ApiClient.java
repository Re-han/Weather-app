package com.r.weatherapp;

import com.r.weatherapp.Interface.weatherapi;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClient {

        public Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://api.openweathermap.org/data/2.5/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
      com.r.weatherapp.Interface.weatherapi weatherapi = retrofit.create(weatherapi.class);
        public ApiClient(Retrofit retrofit) {
                this.retrofit = retrofit;
        }
}
