package com.example.rentavehicle;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Navigate to LoginActivity after a 3-second delay
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                // Command to open the new screen (LoginActivity)
                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(intent);

                // Close this activity to prevent returning to the Splash Screen when pressing the Back button
                finish();
            }
        }, 3000); // 3000 milliseconds (3 seconds)
    }
}