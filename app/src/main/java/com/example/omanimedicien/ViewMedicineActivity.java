package com.example.omanimedicien;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class ViewMedicineActivity extends AppCompatActivity {

    EditText etName, etCompound, etPrice, etDesc, etInstructions, etTemp, etAge, etWarnings;
    ImageView ivMedImage;
    Button btnChangeImage, btnUpdate, btnDelete;
    Uri imageUri;
    DbSQLlite db;
    int medId;
    String currentImagePath;

    private final ActivityResultLauncher<Intent> imagePickerLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    imageUri = result.getData().getData();
                    ivMedImage.setImageURI(imageUri);
                    currentImagePath = imageUri.toString();
                }
            }
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_view_medicine);

        db = new DbSQLlite(this);
        medId = getIntent().getIntExtra("MED_ID", -1);

        // Handle window insets for Edge-to-Edge
        View mainView = findViewById(android.R.id.content);
        ViewCompat.setOnApplyWindowInsetsListener(mainView, (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        etName = findViewById(R.id.etViewMedName);
        etCompound = findViewById(R.id.etViewMedCompound);
        etPrice = findViewById(R.id.etViewMedPrice);
        etDesc = findViewById(R.id.etViewMedDesc);
        etInstructions = findViewById(R.id.etViewMedInstructions);
        etTemp = findViewById(R.id.etViewMedTemp);
        etAge = findViewById(R.id.etViewMedAge);
        etWarnings = findViewById(R.id.etViewMedWarnings);
        ivMedImage = findViewById(R.id.ivViewMedImage);
        btnChangeImage = findViewById(R.id.btnChangeMedImage);
        btnUpdate = findViewById(R.id.btnUpdateMed);
        btnDelete = findViewById(R.id.btnDeleteMedView);

        if (medId == -1) {
            Toast.makeText(this, "Error: Medicine ID not found", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        loadMedicineData();

        btnChangeImage.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            imagePickerLauncher.launch(intent);
        });

        btnUpdate.setOnClickListener(v -> {
            String name = etName.getText().toString().trim();
            String compound = etCompound.getText().toString().trim();
            String priceStr = etPrice.getText().toString().trim();
            String desc = etDesc.getText().toString().trim();
            String instructions = etInstructions.getText().toString().trim();
            String temp = etTemp.getText().toString().trim();
            String age = etAge.getText().toString().trim();
            String warnings = etWarnings.getText().toString().trim();

            if (name.isEmpty() || priceStr.isEmpty()) {
                Toast.makeText(this, "Name and Price are required", Toast.LENGTH_SHORT).show();
                return;
            }

            try {
                double price = Double.parseDouble(priceStr);
                boolean updated = db.updateMedicine(medId, name, currentImagePath, compound, desc, instructions, price, temp, age, warnings);
                if (updated) {
                    Toast.makeText(this, "Medicine updated successfully", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(this, "Update failed", Toast.LENGTH_SHORT).show();
                }
            } catch (NumberFormatException e) {
                Toast.makeText(this, "Invalid price format", Toast.LENGTH_SHORT).show();
            }
        });

        btnDelete.setOnClickListener(v -> {
            new AlertDialog.Builder(this)
                    .setTitle("Confirm Delete")
                    .setMessage("Are you sure you want to delete this medicine?")
                    .setPositiveButton("Yes", (dialog, which) -> {
                        db.deleteMedicine(medId);
                        Toast.makeText(this, "Medicine deleted", Toast.LENGTH_SHORT).show();
                        finish();
                    })
                    .setNegativeButton("No", null)
                    .show();
        });
    }

    private void loadMedicineData() {
        try {
            Medicine med = db.getMedicineById(medId);
            if (med != null) {
                etName.setText(med.getName());
                etCompound.setText(med.getCompound());
                etPrice.setText(String.valueOf(med.getPrice()));
                etDesc.setText(med.getDescription());
                etInstructions.setText(med.getInstructions());
                etTemp.setText(med.getTempRange());
                etAge.setText(med.getAgeLimit());
                etWarnings.setText(med.getWarnings());
                currentImagePath = med.getImage();
                
                if (currentImagePath != null && !currentImagePath.isEmpty()) {
                    try {
                        ivMedImage.setImageURI(Uri.parse(currentImagePath));
                    } catch (Exception e) {
                        ivMedImage.setImageResource(R.drawable.logo);
                    }
                }
            } else {
                Toast.makeText(this, "Medicine not found in database", Toast.LENGTH_SHORT).show();
                finish();
            }
        } catch (Exception e) {
            Toast.makeText(this, "Error loading data: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            finish();
        }
    }
}
