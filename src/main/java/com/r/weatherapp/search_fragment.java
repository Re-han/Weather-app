package com.r.weatherapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class search_fragment extends AppCompatActivity {
    EditText t;
    RecyclerView r;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_fragment);
        t = findViewById(R.id.editText);

        r = findViewById(R.id.defaultdata);
        r.setLayoutManager(new GridLayoutManager(this, 2));
        r.hasFixedSize();
        defaultWeather();
        t.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if ((actionId & EditorInfo.IME_MASK_ACTION) == EditorInfo.IME_ACTION_DONE) {
                    if (t.getText().toString().length() == 0) {
                        Toast t = Toast.makeText(getApplicationContext(), "Search field cannot be Empty", Toast.LENGTH_SHORT);
                        t.setGravity(Gravity.CENTER, 0, 0);
                        t.show();
                    } else {
                        SharedPreferences sp = getSharedPreferences("editTextTransfer", Context.MODE_PRIVATE);
                        SharedPreferences.Editor edit = sp.edit();
                        edit.putString("edittexttransfer", t.getText().toString());
                        edit.apply();
                        startActivity(new Intent(getApplicationContext(), MainActivity.class));
                    }

                }
                return true;
            }

        });
    }


    public void defaultWeather() {
        List<String> Mylist = new ArrayList<>();

        Mylist.add("Mumbai");
        Mylist.add("Kerala");
        Mylist.add("Shimla");
        Mylist.add("Goa");
        Mylist.add("Delhi");
        Mylist.add("Indore");
        Mylist.add("Lucknow");
        Mylist.add("Manali");
        r.setAdapter(new adapterRecycler(this, Mylist));

    }


}