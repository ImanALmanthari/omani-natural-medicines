package com.example.omanimedicien;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class AdminloginActivity extends AppCompatActivity {

    Button btnBackToUserLogin, btnAdminLogin;
    EditText etAdminUsername, etAdminPassword;
    DbSQLlite db;
    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_adminlogin);

        db = new DbSQLlite(this);
        sharedPreferences = getSharedPreferences("UserSession", Context.MODE_PRIVATE);

        // Store original padding from XML
        final int paddingLeft = findViewById(R.id.main).getPaddingLeft();
        final int paddingTop = findViewById(R.id.main).getPaddingTop();
        final int paddingRight = findViewById(R.id.main).getPaddingRight();
        final int paddingBottom = findViewById(R.id.main).getPaddingBottom();

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(
                systemBars.left + paddingLeft,
                systemBars.top + paddingTop,
                systemBars.right + paddingRight,
                systemBars.bottom + paddingBottom
            );
            return WindowInsetsCompat.CONSUMED;
        });

        btnBackToUserLogin = findViewById(R.id.btnBackToUserLogin);
        btnAdminLogin = findViewById(R.id.btnAdminLogin);
        etAdminUsername = findViewById(R.id.etAdminUsername);
        etAdminPassword = findViewById(R.id.etAdminPassword);

        // Admin login logic
        btnAdminLogin.setOnClickListener(v -> {
            String user = etAdminUsername.getText().toString().trim();
            String pass = etAdminPassword.getText().toString().trim();

            if (user.isEmpty() || pass.isEmpty()) {
                Toast.makeText(AdminloginActivity.this, "Please enter all fields", Toast.LENGTH_SHORT).show();
            } else {
                boolean isValid = db.checkAdminLogin(user, pass);
                if (isValid) {
                    // Save Admin Session
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("USER_EMAIL", user);
                    editor.putString("USER_TYPE", "ADMIN");
                    editor.apply();

                    Toast.makeText(AdminloginActivity.this, "Admin Login Successful!", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(AdminloginActivity.this, AdminHomeActivity.class);
                    startActivity(intent);
                    finish(); // Close login page
                } else {
                    Toast.makeText(AdminloginActivity.this, "Invalid Admin Credentials", Toast.LENGTH_SHORT).show();
                }
            }
        });

        btnBackToUserLogin.setOnClickListener(v -> {
            Intent intent = new Intent(AdminloginActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        });
    }
}
