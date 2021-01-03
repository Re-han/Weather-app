package com.r.weatherapp;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.provider.Settings;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.motion.widget.MotionLayout;
import androidx.core.app.ActivityCompat;

import com.bumptech.glide.Glide;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.r.weatherapp.common.Main;
import com.r.weatherapp.common.Root;
import com.r.weatherapp.common.Weather;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;


public class MainActivity extends AppCompatActivity {
    TextView humi_press;
    TextView city_temp;
    TextView desc;
    Button b;
    Toolbar t;
    MotionLayout motionLayout;
    FrameLayout fl;
    LocationManager locationManager;
    String s = "";
    String units = "metric";
    FusedLocationProviderClient location1;
    int CODE = 1;
    ImageView i;
    String apiKey = "YOUR_API_KEY";
    private Retrofit retrofit;

    @SuppressLint("UseCompatLoadingForDrawables")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        motionLayout = findViewById(R.id.myLayout);
        t = findViewById(R.id.toolbar);
        setSupportActionBar(t);
        humi_press = findViewById(R.id.humidity);
        desc = findViewById(R.id.descrption);
        humi_press.setText(null);
        location1 = LocationServices.getFusedLocationProviderClient(this);
        city_temp = findViewById(R.id.city);
        fl = findViewById(R.id.searchfragmentframelayout);
        i = findViewById(R.id.image1);
        b = findViewById(R.id.button);
        b.setOnClickListener(v -> {
            Intent a = new Intent(this, search_fragment.class);

            startActivity(a);
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        //Runtime Permission
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            internetCheck();
            SharedPreferences sp = this.getSharedPreferences("editTextTransfer", Context.MODE_PRIVATE);
            SharedPreferences sh = this.getSharedPreferences("cityName", MODE_PRIVATE);
            s = sh.getString("city", "");
            if (s.length() != 0) {
                getWeather(s, units);
            } else if (sp.getString("edittexttransfer", "").length() != 0) {
                getWeather(sp.getString("edittexttransfer", ""), units);
            } else {
                getCords();
            }
            gpsStatus();


        } else {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION}, CODE);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        internetCheck();
        SharedPreferences sp = this.getSharedPreferences("editTextTransfer", Context.MODE_PRIVATE);
        SharedPreferences sh = this.getSharedPreferences("cityName", MODE_PRIVATE);
        s = sh.getString("city", "");
        if (s.length() != 0) {
            getWeather(s, units);
        } else if (sp.getString("edittexttransfer", "").length() != 0) {
            getWeather(sp.getString("edittexttransfer", ""), units);
        } else {
            getCords();
        }

    }

    @Override
    public void onBackPressed() {
        finish();
    }

    @Override
    protected void onStop() {
        super.onStop();
        SharedPreferences sp = this.getSharedPreferences("editTextTransfer", Context.MODE_PRIVATE);
        SharedPreferences sh = this.getSharedPreferences("cityName", MODE_PRIVATE);
        SharedPreferences.Editor edit = sh.edit();
        SharedPreferences.Editor editor = sp.edit();
        edit.clear().apply();
        editor.clear().apply();
        //onBackPressed();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        SharedPreferences sp = this.getSharedPreferences("editTextTransfer", Context.MODE_PRIVATE);
        SharedPreferences sh = this.getSharedPreferences("cityName", MODE_PRIVATE);
        SharedPreferences.Editor edit = sh.edit();
        SharedPreferences.Editor editor = sp.edit();
        edit.clear().apply();
        editor.clear().apply();
    }

    @SuppressLint("MissingPermission")
    private void getCords() {
        Task<Location> l = location1.getLastLocation();
        l.addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if (location != null) {
                    getCurrentLocTemp(location.getLatitude(), location.getLongitude(), units);
                } else {
                    LocationRequest lr = new LocationRequest();
                    lr.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
                    lr.setInterval(5);
                    lr.setFastestInterval(0);
                    lr.setNumUpdates(1);
                    location1 = LocationServices.getFusedLocationProviderClient(MainActivity.this);
                    location1.requestLocationUpdates(lr, mloc, Looper.myLooper());
                }
            }
        });
    }

    private final LocationCallback mloc = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            super.onLocationResult(locationResult);
            Location mlocation = locationResult.getLastLocation();
            getCurrentLocTemp(mlocation.getLatitude(), mlocation.getLongitude(), units);
        }
    };


    public void gpsStatus() {
        locationManager = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
        assert locationManager != null;
        boolean locationStatus = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        if (!locationStatus && Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Toast.makeText(this, "Location Is Disabled! Couldn't find current location", Toast.LENGTH_SHORT).show();

        }

    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Location permission granted", Toast.LENGTH_SHORT).show();
                gpsStatus();
                getCords();
            } else {
                if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) &&
                        (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION))) {
                    new AlertDialog.Builder(MainActivity.this)
                            .setTitle("Location permission")
                            .setMessage("Location permission is required to fetch weather details for current location")
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                                            Manifest.permission.ACCESS_COARSE_LOCATION}, CODE);
                                }
                            })
                            .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    finish();
                                }
                            })
                            .show();
                } else {
                    new AlertDialog.Builder(MainActivity.this)
                            .setTitle("Location permission")
                            .setMessage("Location permission is required to fetch weather details for current location")
                            .setNegativeButton("Close", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    finish();
                                }
                            })
                            .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            Intent i = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.fromParts("package", getPackageName(), null));
                                            startActivityForResult(i, CODE);
                                        }
                                    }
                            )
                            .show();
                }
            }
        }
    }


    public void getWeather(@NotNull String str, String units) {
        if (str.length() != 0) {

            ApiClient api = new ApiClient(retrofit);
            Call<Root> usersCall = api.weatherapi.weatherCalls(str, units, apiKey);
            usersCall.enqueue(new Callback<Root>() {
                @SuppressLint({"UseCompatLoadingForDrawables", "SetTextI18n"})
                @Override
                public void onResponse(Call<Root> call, Response<Root> response) {
                    if (response.code() == 404) {
                        Toast.makeText(MainActivity.this, "City/Area Not found!!", Toast.LENGTH_SHORT).show();
                        getCords();
                    }
                    if (response.isSuccessful()) {
                        Root r = response.body();
                        Main m = r.getMain();
                        List<Weather> weather = r.getWeather();
                        for (Weather w2 : weather) {
                            city_temp.setText(null);
                            humi_press.setText(null);
                            Double d = m.getTemp();
                            if (d < 15.0) {
                                motionLayout.setBackground(getDrawable(R.drawable.icy));
                            } else if (d > 16.0 && d < 28.0) {
                                motionLayout.setBackground(getDrawable(R.drawable.rainy));
                            } else if (d > 29.0) {
                                motionLayout.setBackground(getDrawable(R.drawable.sunny));
                            }
                            city_temp.setText(r.getName() + "\n\n" + ((m.getTemp()) + "\u2103"));
                            humi_press.setText(null);
                            desc.setText(w2.getDescription() + "\n");
                            humi_press.setText("Humidity : " + " " + (m.getHumidity()) + "%");
                            String s = w2.getIcon();
                            Glide.with(MainActivity.this).load("https://openweathermap.org/img/wn/" + s + "@2x.png").into(i);
                            motionLayout.transitionToEnd();
                            Log.i("1", "description" + w2.getDescription());
                        }
                    }
                }

                @Override
                public void onFailure(Call<Root> call, Throwable t) {

                    new AlertDialog.Builder(MainActivity.this)
                            .setTitle("Alert")
                            .setMessage("Unable to connect with the Server. Check your internet connection and try again")
                            .setCancelable(false)
                            .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.cancel();
                                    if (isOnline()) {
                                        onStart();
                                    }
                                }

                            })
                            .show();


                }
            });
        }
    }


    public void getCurrentLocTemp(Double lat, Double lon, String units) {
        ApiClient api = new ApiClient(retrofit);
        Call<Root> usersCall = api.weatherapi.Lat_Lon_calls(lat, lon, units, apiKey);
        usersCall.enqueue(new Callback<Root>() {
            @SuppressLint({"SetTextI18n", "UseCompatLoadingForDrawables"})
            @Override
            public void onResponse(@NotNull Call<Root> call, @NotNull Response<Root> response) {
                Root r = response.body();
                Main m = r.getMain();
                String cityName = r.getName();
                List<Weather> weather = r.getWeather();
                for (Weather w2 : weather) {
                    Double d = m.getTemp();
                    if (d < 15.0) {
                        motionLayout.setBackground(getDrawable(R.drawable.icy));
                    } else if (d > 15.0 && d < 28.0) {
                        motionLayout.setBackground(getDrawable(R.drawable.rainy));
                    } else if (d > 29.0) {
                        motionLayout.setBackground(getDrawable(R.drawable.sunny));
                    }
//                    SharedPreferences city= getSharedPreferences("city",MODE_PRIVATE);
//                    SharedPreferences.Editor editor = city.edit();
//                    editor.putString("cityName",cityName);
//                    editor.apply();
                    city_temp.setText(cityName + "\n\n" + ((m.getTemp()) + "\u2103"));
                    humi_press.setText(null);
                    desc.setText(w2.getDescription() + "\n");
                    humi_press.setText("Humidity : " + " " + (m.getHumidity()) + "%");
                    String s = w2.getIcon();
                    if (city_temp.length() > 0 && humi_press.length() > 0 && desc.length() > 0) {
                        i.setImageAlpha(255);
                        Glide.with(getApplicationContext()).load("https://openweathermap.org/img/wn/" + s + "@2x.png").into(i);
                    }
                    motionLayout.transitionToEnd();
                    Log.i("1", "description" + w2.getDescription());

                }
            }

            @Override
            public void onFailure(@NotNull Call<Root> call, @NotNull Throwable t) {

                new AlertDialog.Builder(MainActivity.this)
                        .setTitle("Alert")
                        .setMessage("Unable to connect with the Server. Check your internet connection and try again")
                        .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                                if (isOnline()) {
                                    onStart();
                                }
                            }
                        })
                        .setCancelable(false)
                        .show();


            }
        });
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

//        if (item.getItemId() == R.id.celsius) {
//            SharedPreferences sharedPreferences = getSharedPreferences("celsius", MODE_PRIVATE);
//            SharedPreferences.Editor editor = sharedPreferences.edit();
//            editor.putString("c", "metric");
//            editor.apply();
//            startActivity(new Intent(this, MainActivity.class));
//        }
//        if (item.getItemId() == R.id.fahrenheit) {
//            SharedPreferences sharedPreferences = getSharedPreferences("farenheit", MODE_PRIVATE);
//            SharedPreferences.Editor editor = sharedPreferences.edit();
//            editor.putString("f", "imperial");
//            editor.apply();
//            startActivity(new Intent(this, MainActivity.class));
//        }
        if (item.getItemId() == R.id.about) {
            new AlertDialog.Builder(MainActivity.this)
                    .setTitle("About")
                    .setMessage("Weather App is a simple application to get weather details for current location and other various countries")
                    .setNegativeButton("ok", null)
                    .show();
        }
        return true;
    }

    public boolean isOnline() {
//        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
//        return cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnectedOrConnecting();
        ConnectivityManager connMgr =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        boolean isWifiConn = false;
        boolean isMobileConn = false;
        for (Network network : connMgr.getAllNetworks()) {
            NetworkInfo networkInfo = connMgr.getNetworkInfo(network);
            if (networkInfo.getType() == ConnectivityManager.TYPE_WIFI) {
                isWifiConn |= networkInfo.isConnected();
                return isWifiConn = true;
            }
            if (networkInfo.getType() == ConnectivityManager.TYPE_MOBILE) {
                isMobileConn |= networkInfo.isConnected();
                return isMobileConn = true;
            }
        }

        return (isMobileConn || isWifiConn) ? false : true;
    }

    public boolean internetCheck() {
        if (!isOnline()) {
            new AlertDialog.Builder(MainActivity.this)
                    .setTitle("Alert")
                    .setMessage("Unable to connect with the Server. Check your internet connection and try again")
                    .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            if (isOnline()) {
                                dialog.cancel();
                                dialog.dismiss();
                            }
                        }
                    })
                    .setCancelable(false)
                    .show();

        }
        return true;
    }
}

