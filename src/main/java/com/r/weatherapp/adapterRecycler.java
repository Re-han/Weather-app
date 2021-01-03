package com.r.weatherapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.motion.widget.MotionLayout;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class adapterRecycler extends RecyclerView.Adapter<adapterRecycler.ViewHolder> {
    Context context;
    
    List<String> Mylist;

    public adapterRecycler(Context context, List<String> mylist) {
        this.context = context;
        Mylist = mylist;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardviewdefaultcities, parent, false);
        ViewHolder v1 = new ViewHolder(v);
        return v1;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.defaultcity.setText(Mylist.get(position));
        holder.imageView.setBackground(ContextCompat.getDrawable(context,R.drawable.searchicon));
        holder.constraintLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences s = context.getSharedPreferences("cityName", Context.MODE_PRIVATE);
                SharedPreferences.Editor edit = s.edit();
                edit.putString("city", (String) holder.defaultcity.getText());
                edit.apply();
              context.startActivity(new Intent(context, MainActivity.class));
            }
        });
    }

    @Override
    public int getItemCount() {
        return Mylist.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView defaultcity;
        ImageView imageView;
        ConstraintLayout constraintLayout;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            constraintLayout = itemView.findViewById(R.id.layout);
            defaultcity = itemView.findViewById(R.id.defaultcity);
            imageView = itemView.findViewById(R.id.image);
        }
    }
}

