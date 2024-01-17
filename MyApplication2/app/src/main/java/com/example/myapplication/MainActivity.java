package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class MainActivity extends AppCompatActivity {

    final String LOG_FILE_NAME =  "AppLog%g.log";
    final int LOG_FILE_C = 5;
    final int LOG_SIZE = 102400;
    final boolean LOG_APPEND = true;
    Logger logger;
    int CODE_HTTP = 0;
    int REQUEST_OK = 200;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        logger = Logger.getLogger(MainActivity.class.getName());
        logger.log(Level.INFO, "application start");

        try {
            String logName = Environment.getExternalStorageDirectory() +
                    File.separator +
                    LOG_FILE_NAME;
            FileHandler logHandler = new FileHandler(logName, LOG_SIZE, LOG_FILE_C, LOG_APPEND);
            logHandler.setFormatter(new SimpleFormatter());
            logger.addHandler(logHandler);
            logger.log(Level.INFO, "Logger is ready");
        }
        catch (Exception e) {
            logger.log(Level.SEVERE, "Logger is not ready: unexpected", e);
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if(id == R.id.itemHome){
            Toast.makeText(this, "Home selected", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(MainActivity.this, MainActivity.class);
            startActivity(intent);
            return true;
        }
        else if(id == R.id.itemArchive){
            Toast.makeText(this, "Archive selected", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(MainActivity.this, AchiveActivity.class);
            startActivity(intent);
            return true;
        }
        else if(id == R.id.itemRecon){
            RequestSender requestSender = new RequestSender();
            requestSender.execute();
            if(CODE_HTTP==REQUEST_OK){
                //Online
                ImageView imageView = findViewById(R.id.imageView);
                imageView.setImageBitmap(ConnectionHttp.getImage());
                TextView textView = findViewById(R.id.statusId);
                textView.setText("Online");
                textView.setTextColor(Color.parseColor("#39FF14"));
            }
            else{
                //Offline
                logger.log(Level.SEVERE, String.format("connection error: code %d",CODE_HTTP));
                TextView textView = findViewById(R.id.statusId);
                textView.setText("Offline");
                textView.setTextColor(Color.parseColor("#FF0000"));
            }
        }
        return false;
    }

    @SuppressLint("StaticFieldLeak")
    class RequestSender extends AsyncTask<Void, Integer, Void> {

        @SuppressLint("AllowAllHostnameVerifier")
        @Override
        protected Void doInBackground(Void... voids) {
            try {
                CODE_HTTP = ConnectionHttp.start();
            } catch (Exception e) {
                logger.log(Level.SEVERE, "timeout: unexpected");
            }
            return null;
        }
    }
}