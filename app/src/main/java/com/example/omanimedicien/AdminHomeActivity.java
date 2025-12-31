package com.example.omanimedicien;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;

public class AdminHomeActivity extends AppCompatActivity {

    DrawerLayout drawerLayout;
    NavigationView navigationView;
    ImageView btnMenu;
    TextView tvTotalUsers, tvTotalMedicines, tvPendingOrders, tvCompletedOrders, tvRemovedOrders, tvTotalRevenue;
    DbSQLlite db;
    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_admin_home);

        db = new DbSQLlite(this);
        sharedPreferences = getSharedPreferences("UserSession", Context.MODE_PRIVATE);

        drawerLayout = findViewById(R.id.admin_drawer_layout);
        navigationView = findViewById(R.id.admin_nav_view);
        btnMenu = findViewById(R.id.btnAdminMenu);
        
        tvTotalUsers = findViewById(R.id.tvTotalUsers);
        tvTotalMedicines = findViewById(R.id.tvTotalMedicines);
        tvPendingOrders = findViewById(R.id.tvPendingOrders);
        tvCompletedOrders = findViewById(R.id.tvCompletedOrders);
        tvRemovedOrders = findViewById(R.id.tvRemovedOrders);
        tvTotalRevenue = findViewById(R.id.tvTotalRevenue);

        updateStats();

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.admin_drawer_layout), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        btnMenu.setOnClickListener(v -> drawerLayout.openDrawer(Gravity.LEFT));

        navigationView.setNavigationItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_manage_users) {
                startActivity(new Intent(AdminHomeActivity.this, ManageUsersActivity.class));
            } else if (id == R.id.nav_manage_medicines) {
                startActivity(new Intent(AdminHomeActivity.this, ManageMedicinesActivity.class));
            } else if (id == R.id.nav_manage_orders) {
                startActivity(new Intent(AdminHomeActivity.this, ManageOrdersActivity.class));
            } else if (id == R.id.nav_admin_logout) {
                logout();
            }
            drawerLayout.closeDrawers();
            return true;
        });
    }

    private void logout() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();
        startActivity(new Intent(AdminHomeActivity.this, MainActivity.class));
        finish();
    }

    private void updateStats() {
        tvTotalUsers.setText(String.valueOf(db.getUsersCount()));
        tvTotalMedicines.setText(String.valueOf(db.getMedicinesCount()));
        tvPendingOrders.setText(String.valueOf(db.getOrdersCountByStatus("Pending")));
        tvCompletedOrders.setText(String.valueOf(db.getOrdersCountByStatus("Completed")));
        tvRemovedOrders.setText(String.valueOf(db.getOrdersCountByStatus("Removed")));
        
        double revenue = db.getCompletedOrdersTotalAmount();
        tvTotalRevenue.setText(String.format("%.3f", revenue));
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateStats();
    }
}
