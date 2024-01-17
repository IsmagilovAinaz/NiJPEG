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
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class MainActivity extends AppCompatActivity {
    final String SERVER_ADDRESS_PATH = "ServerAddress.txt";
    final String LOG_FILE_NAME = "AppLog%g.log";
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

    private String getStringFromFile(String path) {
        try {
            FileInputStream fin = openFileInput(path);
            byte[] bytes = new byte[fin.available()];
            fin.read(bytes);
            fin.close();
            return new String(bytes);
        } catch (FileNotFoundException ex) {
            setStringToFile("none", path);
        } catch (IOException ex) {
            logger.log(Level.SEVERE, String.format("read file %s : input/output", path));
        }
        catch (Exception ex) {
            logger.log(Level.SEVERE, String.format("read file %s : unexpected", path));
        }
        return "none";
    }

    private void setStringToFile(String data, String path) {
        try {
            FileOutputStream fos = openFileOutput(path, MODE_PRIVATE);
            fos.write(data.getBytes());
            fos.close();
        } catch (IOException ex) {
            logger.log(Level.SEVERE, String.format("write file %s : input/output", path));
        }
        catch (Exception ex) {
            logger.log(Level.SEVERE, String.format("write file %s : unexpected", path));
        }
    }

    @SuppressLint("StaticFieldLeak")
    class RequestSender extends AsyncTask<Void, Integer, Void> {

        @SuppressLint("AllowAllHostnameVerifier")
        @Override
        protected Void doInBackground(Void... voids) {
            try {
                CODE_HTTP = ConnectionHttp.start(getStringFromFile(SERVER_ADDRESS_PATH));
            } catch (Exception e) {
                logger.log(Level.SEVERE, "timeout: unexpected");
            }
            return null;
        }
    }
}