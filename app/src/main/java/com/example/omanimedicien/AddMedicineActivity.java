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
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class AddMedicineActivity extends AppCompatActivity {

    EditText etName, etCompound, etPrice, etDesc, etInstructions, etTemp, etAge, etWarnings;
    ImageView ivMedImage;
    Button btnSelectImage, btnSave;
    Uri imageUri;
    DbSQLlite db;

    private final ActivityResultLauncher<Intent> imagePickerLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    imageUri = result.getData().getData();
                    try {
                        getContentResolver().takePersistableUriPermission(imageUri, Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    } catch (SecurityException e) {
                        e.printStackTrace();
                    }
                    ivMedImage.setImageURI(imageUri);
                }
            }
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_add_medicine);

        db = new DbSQLlite(this);

        View mainView = findViewById(R.id.main_scroll_add_medicine);
        ViewCompat.setOnApplyWindowInsetsListener(mainView, (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            Insets ime = insets.getInsets(WindowInsetsCompat.Type.ime());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, Math.max(systemBars.bottom, ime.bottom));
            return insets;
        });

        etName = findViewById(R.id.etMedName);
        etCompound = findViewById(R.id.etMedCompound);
        etPrice = findViewById(R.id.etMedPrice);
        etDesc = findViewById(R.id.etMedDesc);
        etInstructions = findViewById(R.id.etMedInstructions);
        etTemp = findViewById(R.id.etMedTemp);
        etAge = findViewById(R.id.etMedAge);
        etWarnings = findViewById(R.id.etMedWarnings);
        ivMedImage = findViewById(R.id.ivMedImage);
        btnSelectImage = findViewById(R.id.btnSelectImage);
        btnSave = findViewById(R.id.btnSaveMed);

        btnSelectImage.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            intent.setType("image/*");
            imagePickerLauncher.launch(intent);
        });

        btnSave.setOnClickListener(v -> {
            String name = etName.getText().toString().trim();
            String compound = etCompound.getText().toString().trim();
            String priceStr = etPrice.getText().toString().trim();
            String desc = etDesc.getText().toString().trim();
            String instructions = etInstructions.getText().toString().trim();
            String temp = etTemp.getText().toString().trim();
            String age = etAge.getText().toString().trim();
            String warnings = etWarnings.getText().toString().trim();
            String imagePath = (imageUri != null) ? imageUri.toString() : "";

            if (name.isEmpty() || priceStr.isEmpty()) {
                Toast.makeText(this, "Name and Price are required", Toast.LENGTH_SHORT).show();
                return;
            }

            try {
                double price = Double.parseDouble(priceStr);
                boolean inserted = db.addMedicine(name, imagePath, compound, desc, instructions, price, temp, age, warnings);
                if (inserted) {
                    Toast.makeText(this, "Medicine added successfully", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(this, "Failed to add medicine", Toast.LENGTH_SHORT).show();
                }
            } catch (NumberFormatException e) {
                Toast.makeText(this, "Invalid price", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
