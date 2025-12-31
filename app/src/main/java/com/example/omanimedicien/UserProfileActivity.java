package com.example.omanimedicien;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

public class UserProfileActivity extends AppCompatActivity {

    EditText etEmail, etPassword;
    Button btnUpdate;
    DbSQLlite db;
    String userEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_user_profile);

        db = new DbSQLlite(this);
        userEmail = getIntent().getStringExtra("USER_EMAIL");

        etEmail = findViewById(R.id.etProfileEmail);
        etPassword = findViewById(R.id.etProfilePassword);
        btnUpdate = findViewById(R.id.btnUpdateProfile);

        loadUserData();

        btnUpdate.setOnClickListener(v -> {
            String newEmail = etEmail.getText().toString().trim();
            String newPassword = etPassword.getText().toString().trim();

            if (newEmail.isEmpty() || newPassword.isEmpty()) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            if (db.updateUserProfile(userEmail, newEmail, newPassword)) {
                Toast.makeText(this, "Profile updated successfully!", Toast.LENGTH_SHORT).show();
                // If email changed, we might need to update the session or go back to login
                if (!newEmail.equals(userEmail)) {
                    startActivity(new Intent(UserProfileActivity.this, MainActivity.class));
                    finishAffinity();
                } else {
                    finish();
                }
            } else {
                Toast.makeText(this, "Update failed", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadUserData() {
        User user = db.getUserByEmail(userEmail);
        if (user != null) {
            etEmail.setText(user.getEmail());
            etPassword.setText(user.getPassword());
        }
    }
}
