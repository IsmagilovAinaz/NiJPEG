package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class AchiveActivity extends MainActivity {
    private ArrayList<String> images;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_achive);
        GridView gallery = (GridView) findViewById(R.id.gridView);

        ImageAdapter adapter = new ImageAdapter(this);
        gallery.setAdapter(adapter);
        images = adapter.images;
        gallery.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
                if(null != images && !images.isEmpty()){
                    Toast.makeText(getApplicationContext(), "position " + position + " " + images.get(position), Toast.LENGTH_SHORT).show();
                }
            }
        });

    }


}