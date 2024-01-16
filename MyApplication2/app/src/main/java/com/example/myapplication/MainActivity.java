package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
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
        }
        return false;
    }

    @SuppressLint("StaticFieldLeak")
    class RequestSender extends AsyncTask<Void, Integer, Void> {

        @SuppressLint("AllowAllHostnameVerifier")
        @Override
        protected Void doInBackground(Void... voids) {
            try {
                if(ConnectionHttp.start()==200){
                    //Online
                    ImageView imageView = findViewById(R.id.imageView);
                    imageView.setImageBitmap(ConnectionHttp.getImage());
                    TextView textView = findViewById(R.id.statusId);
                    textView.setText("Online");
                    textView.setTextColor(Color.parseColor("#39FF14"));
                }
                else{
                    //Offline
                    TextView textView = findViewById(R.id.statusId);
                    textView.setText("Offline");
                    textView.setTextColor(Color.parseColor("#FF0000"));
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            return null;
        }
    }
}