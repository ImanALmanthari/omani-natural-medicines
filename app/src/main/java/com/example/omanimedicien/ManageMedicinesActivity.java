package com.example.omanimedicien;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ManageMedicinesActivity extends AppCompatActivity {

    RecyclerView rvMedicines;
    Button btnAddMedicine;
    DbSQLlite db;
    MedicineAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_manage_medicines);

        db = new DbSQLlite(this);
        rvMedicines = findViewById(R.id.rvMedicines);
        btnAddMedicine = findViewById(R.id.btnAddMedicine);

        View root = findViewById(R.id.main_layout_medicines);
        if (root == null) root = rvMedicines.getRootView();
        
        ViewCompat.setOnApplyWindowInsetsListener(root, (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        rvMedicines.setLayoutManager(new LinearLayoutManager(this));
        loadMedicines();

        btnAddMedicine.setOnClickListener(v -> {
            startActivity(new Intent(ManageMedicinesActivity.this, AddMedicineActivity.class));
        });
    }

    private void loadMedicines() {
        try {
            List<Medicine> medicines = db.getAllMedicines();
            adapter = new MedicineAdapter(medicines);
            rvMedicines.setAdapter(adapter);
        } catch (Exception e) {
            Toast.makeText(this, "Error loading medicines: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadMedicines();
    }

    class MedicineAdapter extends RecyclerView.Adapter<MedicineAdapter.MedViewHolder> {
        List<Medicine> medList;

        MedicineAdapter(List<Medicine> list) { this.medList = list; }

        @NonNull
        @Override
        public MedViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_medicine, parent, false);
            return new MedViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull MedViewHolder holder, int position) {
            Medicine med = medList.get(position);
            holder.tvName.setText(med.getName());
            holder.tvCompound.setText(med.getCompound());
            holder.tvDesc.setText(med.getDescription());
            holder.tvPrice.setText(String.format("%.3f OMR", med.getPrice()));

            holder.ivThumb.setImageResource(R.drawable.logo);

            if (med.getImage() != null && !med.getImage().isEmpty()) {
                try {
                    Uri imageUri = Uri.parse(med.getImage());
                    holder.ivThumb.setImageURI(imageUri);
                } catch (Exception e) {
                    holder.ivThumb.setImageResource(R.drawable.logo);
                }
            }

            holder.btnDelete.setOnClickListener(v -> {
                new AlertDialog.Builder(ManageMedicinesActivity.this)
                        .setTitle("Delete Medicine")
                        .setMessage("Are you sure you want to delete " + med.getName() + "?")
                        .setPositiveButton("Yes", (dialog, which) -> {
                            db.deleteMedicine(med.getId());
                            loadMedicines();
                            Toast.makeText(ManageMedicinesActivity.this, "Medicine deleted", Toast.LENGTH_SHORT).show();
                        })
                        .setNegativeButton("No", null)
                        .show();
            });

            holder.btnView.setOnClickListener(v -> {
                Intent intent = new Intent(ManageMedicinesActivity.this, ViewMedicineActivity.class);
                intent.putExtra("MED_ID", med.getId());
                startActivity(intent);
            });
        }

        @Override
        public int getItemCount() { return medList.size(); }

        class MedViewHolder extends RecyclerView.ViewHolder {
            TextView tvName, tvCompound, tvDesc, tvPrice;
            ImageView ivThumb;
            ImageButton btnView, btnDelete;
            MedViewHolder(View v) {
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
