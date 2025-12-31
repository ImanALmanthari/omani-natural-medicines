package com.example.omanimedicien;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

public class MedicineDetailUserActivity extends AppCompatActivity {

    ImageView ivImage;
    TextView tvName, tvCompound, tvPrice, tvDesc, tvInstructions, tvSafety;
    Button btnOrder;
    DbSQLlite db;
    int medId;
    String userEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_medicine_detail_user);

        db = new DbSQLlite(this);
        medId = getIntent().getIntExtra("MED_ID", -1);
        userEmail = getIntent().getStringExtra("USER_EMAIL");

        ivImage = findViewById(R.id.ivUserMedImage);
        tvName = findViewById(R.id.tvUserMedName);
        tvCompound = findViewById(R.id.tvUserMedCompound);
        tvPrice = findViewById(R.id.tvUserMedPrice);
        tvDesc = findViewById(R.id.tvUserMedDesc);
        tvInstructions = findViewById(R.id.tvUserMedInstructions);
        tvSafety = findViewById(R.id.tvUserMedSafety);
        btnOrder = findViewById(R.id.btnPlaceOrder);

        loadMedicineData();

        btnOrder.setOnClickListener(v -> {
            Intent intent = new Intent(this, PlaceOrderActivity.class);
            intent.putExtra("MED_NAME", tvName.getText().toString());
            intent.putExtra("USER_EMAIL", userEmail);
            startActivity(intent);
        });
    }

    private void loadMedicineData() {
        Medicine med = db.getMedicineById(medId);
        if (med != null) {
            tvName.setText(med.getName());
            tvCompound.setText(med.getCompound());
            tvPrice.setText(String.format("%.3f OMR", med.getPrice()));
            tvDesc.setText(med.getDescription());
            tvInstructions.setText(med.getInstructions());
            tvSafety.setText("Age Limit: " + med.getAgeLimit() + "\nStorage: " + med.getTempRange() + "\nWarnings: " + med.getWarnings());
            
            if (med.getImage() != null && !med.getImage().isEmpty()) {
                try {
                    ivImage.setImageURI(Uri.parse(med.getImage()));
                } catch (Exception e) {
                    ivImage.setImageResource(R.drawable.logo);
                }
            }
        }
    }
}
