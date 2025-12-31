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

public class MainActivity extends AppCompatActivity {

    Button btnSignUp, btnAdminLogin, btnUserLogin;
    EditText etUsername, etPassword;
    DbSQLlite db;
    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Session Check
        sharedPreferences = getSharedPreferences("UserSession", Context.MODE_PRIVATE);
        String loggedInUser = sharedPreferences.getString("USER_EMAIL", null);
        String userType = sharedPreferences.getString("USER_TYPE", null);

        if (loggedInUser != null) {
            if ("ADMIN".equals(userType)) {
                startActivity(new Intent(MainActivity.this, AdminHomeActivity.class));
            } else {
                Intent intent = new Intent(MainActivity.this, HomeActivity.class);
                intent.putExtra("USER_EMAIL", loggedInUser);
                startActivity(intent);
            }
            finish();
            return;
        }

        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        db = new DbSQLlite(this);

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

        btnSignUp = findViewById(R.id.btnSignUp);
        btnAdminLogin = findViewById(R.id.btnAdminLogin);
        btnUserLogin = findViewById(R.id.btnUserLogin);
        etUsername = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);

        // User login logic
        btnUserLogin.setOnClickListener(v -> {
            String user = etUsername.getText().toString().trim();
            String pass = etPassword.getText().toString().trim();

            if (user.isEmpty() || pass.isEmpty()) {
                Toast.makeText(MainActivity.this, "Please enter all fields", Toast.LENGTH_SHORT).show();
            } else {
                boolean isValid = db.checkUserLogin(user, pass);
                if (isValid) {
                    // Save Session
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("USER_EMAIL", user);
                    editor.putString("USER_TYPE", "USER");
                    editor.apply();

                    Toast.makeText(MainActivity.this, "Login Successful!", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(MainActivity.this, HomeActivity.class);
                    intent.putExtra("USER_EMAIL", user);
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(MainActivity.this, "Invalid Credentials", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Open Sign Up page
        btnSignUp.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, SignUpActivity2.class);
            startActivity(intent);
        });

        // Open Admin Login page
        btnAdminLogin.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, AdminloginActivity.class);
            startActivity(intent);
        });
    }
}
