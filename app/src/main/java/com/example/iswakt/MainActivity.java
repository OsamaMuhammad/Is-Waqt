package com.example.iswakt;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //getSupportActionBar().hide();
        final Context context=getApplication().getBaseContext();


        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                ConnectivityManager cm =
                        (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);

                NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
                boolean isConnected = activeNetwork != null &&
                        activeNetwork.isConnectedOrConnecting();



                if(!isConnected){
                    Toast.makeText(context,"Please connect to the internet",Toast.LENGTH_SHORT).show();
                    finish();
                }
                else{
                    startActivity(new Intent(MainActivity.this,LoginActivity.class));
                    finish();
                }
            }
        },2000);

    }
}
