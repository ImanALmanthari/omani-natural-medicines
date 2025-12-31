package com.example.omanimedicien;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class ViewUserActivity extends AppCompatActivity {

    EditText etEmail, etPassword;
    Button btnUpdate, btnUpdatePass, btnDelete;
    DbSQLlite db;
    int userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_view_user);

        db = new DbSQLlite(this);

        View mainView = findViewById(R.id.main_scroll_view_user);
        ViewCompat.setOnApplyWindowInsetsListener(mainView, (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            Insets ime = insets.getInsets(WindowInsetsCompat.Type.ime());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, Math.max(systemBars.bottom, ime.bottom));
            return insets;
        });

        etEmail = findViewById(R.id.etUserEmailView);
        etPassword = findViewById(R.id.etNewPasswordView);
        btnUpdate = findViewById(R.id.btnUpdateUser);
        btnUpdatePass = findViewById(R.id.btnUpdatePassword);
        btnDelete = findViewById(R.id.btnDeleteUserView);

        userId = getIntent().getIntExtra("USER_ID", -1);

        if (userId != -1) {
            User user = db.getUserById(userId);
            if (user != null) {
                etEmail.setText(user.getEmail());
            }
        }

        // Update Email
        btnUpdate.setOnClickListener(v -> {
            String email = etEmail.getText().toString().trim();
            if (!email.isEmpty()) {
                if (db.updateUser(userId, email)) {
                    Toast.makeText(this, "User details updated", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Update Password
        btnUpdatePass.setOnClickListener(v -> {
            String pass = etPassword.getText().toString().trim();
            if (!pass.isEmpty()) {
                if (db.updatePassword(userId, pass)) {
                    Toast.makeText(this, "Password updated successfully", Toast.LENGTH_SHORT).show();
                    etPassword.setText("");
                }
            } else {
                Toast.makeText(this, "Please enter a new password", Toast.LENGTH_SHORT).show();
            }
        });

        // Delete User
        btnDelete.setOnClickListener(v -> {
            new AlertDialog.Builder(this)
                    .setTitle("Confirm Delete")
                    .setMessage("Are you sure you want to delete this user?")
                    .setPositiveButton("Yes", (dialog, which) -> {
                        db.deleteUser(userId);
                        Toast.makeText(this, "User deleted", Toast.LENGTH_SHORT).show();
                        finish();
                    })
                    .setNegativeButton("No", null)
                    .show();
        });
    }
}
