package com.example.omanimedicien;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.activity.EdgeToEdge;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class SignUpActivity2 extends AppCompatActivity {

    EditText etEmail, etPassword, etConfirmPassword;
    Button btnCreateAccount;
    TextView tvBackToLogin;
    DbSQLlite db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_sign_up2);

        db = new DbSQLlite(this);

        // Store original padding from XML
        final int paddingLeft = findViewById(R.id.main).getPaddingLeft();
        final int paddingTop = findViewById(R.id.main).getPaddingTop();
        final int paddingRight = findViewById(R.id.main).getPaddingRight();
        final int paddingBottom = findViewById(R.id.main).getPaddingBottom();

        // Handle window insets for Edge-to-Edge without losing XML padding
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

        // Initialize Views
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        etConfirmPassword = findViewById(R.id.etConfirmPassword);
        btnCreateAccount = findViewById(R.id.btnCreateAccount);
        tvBackToLogin = findViewById(R.id.tvBackToLogin);

        // Click listener for Sign Up button
        btnCreateAccount.setOnClickListener(v -> {
            String email = etEmail.getText().toString().trim();
            String password = etPassword.getText().toString().trim();
            String confirmPassword = etConfirmPassword.getText().toString().trim();

            if(email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
                Toast.makeText(SignUpActivity2.this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            } else if(!password.equals(confirmPassword)) {
                Toast.makeText(SignUpActivity2.this, "Passwords do not match", Toast.LENGTH_SHORT).show();
            } else {
                if (db.checkEmailExists(email)) {
                    Toast.makeText(SignUpActivity2.this, "User already exists!", Toast.LENGTH_SHORT).show();
                } else {
                    boolean inserted = db.addUser(email, password);
                    if (inserted) {
                        Toast.makeText(SignUpActivity2.this, "Account Created Successfully!", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(SignUpActivity2.this, MainActivity.class));
                        finish();
                    } else {
                        Toast.makeText(SignUpActivity2.this, "Registration failed", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        // Click listener to go back to Sign In page
        tvBackToLogin.setOnClickListener(v -> {
            Intent i = new Intent(SignUpActivity2.this, MainActivity.class);
            startActivity(i);
            finish();
        });
    }
}
