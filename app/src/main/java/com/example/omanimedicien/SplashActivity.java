package com.example.omanimedicien;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import androidx.appcompat.app.AppCompatActivity;

public class SplashActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        new Handler().postDelayed(() -> {
            SharedPreferences sp = getSharedPreferences("UserSession", MODE_PRIVATE);
            String email = sp.getString("USER_EMAIL", null);
            String type = sp.getString("USER_TYPE", null);

            if (email != null) {
                if ("ADMIN".equals(type)) {
                    startActivity(new Intent(SplashActivity.this, AdminHomeActivity.class));
                } else {
                    Intent intent = new Intent(SplashActivity.this, HomeActivity.class);
                    intent.putExtra("USER_EMAIL", email);
                    startActivity(intent);
                }
            } else {
                startActivity(new Intent(SplashActivity.this, MainActivity.class));
            }
            finish();
        }, 2000); // 2 Second Delay
    }
}
