package com.example.omanimedicien;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;
import java.util.List;

public class HomeActivity extends AppCompatActivity {

    DrawerLayout drawerLayout;
    NavigationView navigationView;
    ImageView btnMenu;
    AutoCompleteTextView searchMedicine;
    Button btnSearch;
    TextView tvWelcome, tvUserEmail, tvResultsTitle;
    RecyclerView rvMedicinesHome;
    DbSQLlite db;
    List<Medicine> allMedicines;
    HomeMedicineAdapter adapter;
    String loggedInUserEmail;
    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_home);

        db = new DbSQLlite(this);
        sharedPreferences = getSharedPreferences("UserSession", Context.MODE_PRIVATE);
        
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        btnMenu = findViewById(R.id.btnMenu);
        searchMedicine = findViewById(R.id.searchMedicine);
        btnSearch = findViewById(R.id.btnSearch);
        tvWelcome = findViewById(R.id.tvWelcome);
        tvUserEmail = findViewById(R.id.tvUserEmail);
        tvResultsTitle = findViewById(R.id.tvResultsTitle);
        rvMedicinesHome = findViewById(R.id.rvMedicinesHome);

        rvMedicinesHome.setLayoutManager(new LinearLayoutManager(this));

        // Sidebar Email Header
        View headerView = navigationView.getHeaderView(0);
        TextView tvUserEmailHeader = headerView.findViewById(R.id.tvUserEmailHeader);

        loggedInUserEmail = getIntent().getStringExtra("USER_EMAIL");
        if (loggedInUserEmail == null) {
            loggedInUserEmail = sharedPreferences.getString("USER_EMAIL", "User");
        }
        
        tvUserEmail.setText(loggedInUserEmail);
        tvUserEmailHeader.setText(loggedInUserEmail);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.drawer_layout), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        btnMenu.setOnClickListener(v -> drawerLayout.openDrawer(Gravity.LEFT));

        navigationView.setNavigationItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_orders) {
                Intent intent = new Intent(HomeActivity.this, UserOrdersActivity.class);
                intent.putExtra("USER_EMAIL", loggedInUserEmail);
                startActivity(intent);
            } else if (id == R.id.nav_profile) {
                Intent intent = new Intent(HomeActivity.this, UserProfileActivity.class);
                intent.putExtra("USER_EMAIL", loggedInUserEmail);
                startActivity(intent);
            } else if (id == R.id.nav_about_us) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://omani-natural-medicine.vercel.app/"));
                startActivity(browserIntent);
            } else if (id == R.id.nav_logout) {
                logout();
            }
            drawerLayout.closeDrawers();
            return true;
        });

        setupSearch();
        loadAllMedicines();
    }

    private void logout() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();
        startActivity(new Intent(HomeActivity.this, MainActivity.class));
        finish();
    }

    private void loadAllMedicines() {
        allMedicines = db.getAllMedicines();
        adapter = new HomeMedicineAdapter(allMedicines);
        rvMedicinesHome.setAdapter(adapter);
    }

    private void setupSearch() {
        allMedicines = db.getAllMedicines();
        List<String> medNames = new ArrayList<>();
        for (Medicine m : allMedicines) {
            medNames.add(m.getName());
        }

        ArrayAdapter<String> searchAdapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, medNames);
        searchMedicine.setAdapter(searchAdapter);

        // Open Medicine directly if exact match or first result found on button click
        btnSearch.setOnClickListener(v -> {
            String query = searchMedicine.getText().toString().trim();
            if (!query.isEmpty()) {
                List<Medicine> results = db.searchMedicines(query);
                if (!results.isEmpty()) {
                    // Open the first matching medicine details
                    Intent intent = new Intent(HomeActivity.this, MedicineDetailUserActivity.class);
                    intent.putExtra("MED_ID", results.get(0).getId());
                    intent.putExtra("USER_EMAIL", loggedInUserEmail);
                    startActivity(intent);
                } else {
                    Toast.makeText(this, "No medicine found", Toast.LENGTH_SHORT).show();
                }
            }
        });

        searchMedicine.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() == 0) {
                    loadAllMedicines();
                    tvResultsTitle.setText("All Medicines");
                } else {
                    List<Medicine> filtered = db.searchMedicines(s.toString());
                    adapter = new HomeMedicineAdapter(filtered);
                    rvMedicinesHome.setAdapter(adapter);
                    tvResultsTitle.setText("Searching...");
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    class HomeMedicineAdapter extends RecyclerView.Adapter<HomeMedicineAdapter.HomeMedViewHolder> {
        List<Medicine> medList;

        HomeMedicineAdapter(List<Medicine> list) { this.medList = list; }

        @NonNull
        @Override
        public HomeMedViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_medicine, parent, false);
            return new HomeMedViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull HomeMedViewHolder holder, int position) {
            Medicine med = medList.get(position);
            holder.tvName.setText(med.getName());
            holder.tvCompound.setText(med.getCompound());
            holder.tvDesc.setText(med.getDescription());
            holder.tvPrice.setText(String.format("%.3f OMR", med.getPrice()));

            holder.btnDelete.setVisibility(View.GONE);

            if (med.getImage() != null && !med.getImage().isEmpty()) {
                try {
                    holder.ivThumb.setImageURI(Uri.parse(med.getImage()));
                } catch (Exception e) {
                    holder.ivThumb.setImageResource(R.drawable.logo);
                }
            } else {
                holder.ivThumb.setImageResource(R.drawable.logo);
            }

            holder.itemView.setOnClickListener(v -> {
                Intent intent = new Intent(HomeActivity.this, MedicineDetailUserActivity.class);
                intent.putExtra("MED_ID", med.getId());
                intent.putExtra("USER_EMAIL", loggedInUserEmail);
                startActivity(intent);
            });
            
            holder.btnView.setOnClickListener(v -> {
                Intent intent = new Intent(HomeActivity.this, MedicineDetailUserActivity.class);
                intent.putExtra("MED_ID", med.getId());
                intent.putExtra("USER_EMAIL", loggedInUserEmail);
                startActivity(intent);
            });
        }

        @Override
        public int getItemCount() { return medList.size(); }

        class HomeMedViewHolder extends RecyclerView.ViewHolder {
            TextView tvName, tvCompound, tvDesc, tvPrice;
            ImageView ivThumb;
            ImageButton btnView, btnDelete;
            HomeMedViewHolder(View v) {
                super(v);
                tvName = v.findViewById(R.id.tvMedNameItem);
                tvCompound = v.findViewById(R.id.tvMedCompoundItem);
                tvDesc = v.findViewById(R.id.tvMedDescItem);
                tvPrice = v.findViewById(R.id.tvMedPriceItem);
                ivThumb = v.findViewById(R.id.ivMedicineThumb);
                btnView = v.findViewById(R.id.btnViewMed);
                btnDelete = v.findViewById(R.id.btnDeleteMed);
            }
        }
    }
}
