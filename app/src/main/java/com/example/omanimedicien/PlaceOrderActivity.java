package com.example.omanimedicien;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class PlaceOrderActivity extends AppCompatActivity {

    TextView tvMedName;
    EditText etQuantity, etAddress, etPhone;
    RadioGroup rgPayment;
    RadioButton rbCard;
    LinearLayout layoutCardDetails;
    EditText etCardName, etCardNumber, etCardExpiry, etCardCVV;
    Button btnPlaceOrder;

    DbSQLlite db;
    String medicineName;
    String userEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_place_order);

        db = new DbSQLlite(this);
        medicineName = getIntent().getStringExtra("MED_NAME");
        userEmail = getIntent().getStringExtra("USER_EMAIL");

        View mainView = findViewById(R.id.main_scroll_place_order);
        ViewCompat.setOnApplyWindowInsetsListener(mainView, (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            Insets ime = insets.getInsets(WindowInsetsCompat.Type.ime());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, Math.max(systemBars.bottom, ime.bottom));
            return insets;
        });

        tvMedName = findViewById(R.id.tvOrderMedName);
        etQuantity = findViewById(R.id.etOrderQuantity);
        etAddress = findViewById(R.id.etOrderAddress);
        etPhone = findViewById(R.id.etOrderPhone);
        rgPayment = findViewById(R.id.rgPaymentMethod);
        rbCard = findViewById(R.id.rbCard);
        layoutCardDetails = findViewById(R.id.layoutCardDetails);
        etCardName = findViewById(R.id.etCardName);
        etCardNumber = findViewById(R.id.etCardNumber);
        etCardExpiry = findViewById(R.id.etCardExpiry);
        etCardCVV = findViewById(R.id.etCardCVV);
        btnPlaceOrder = findViewById(R.id.btnFinalPlaceOrder);

        tvMedName.setText(medicineName);

        rgPayment.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.rbCard) {
                layoutCardDetails.setVisibility(View.VISIBLE);
                btnPlaceOrder.setText("Pay & Place Order");
            } else {
                layoutCardDetails.setVisibility(View.GONE);
                btnPlaceOrder.setText("Place Order");
            }
        });

        btnPlaceOrder.setOnClickListener(v -> {
            processOrder();
        });
    }

    private void processOrder() {
        String qtyStr = etQuantity.getText().toString().trim();
        String address = etAddress.getText().toString().trim();
        String phoneInput = etPhone.getText().toString().trim();
        String payMode = (rgPayment.getCheckedRadioButtonId() == R.id.rbCard) ? "Card" : "COD";

        if (qtyStr.isEmpty() || address.isEmpty() || phoneInput.isEmpty()) {
            Toast.makeText(this, "Please fill all delivery details", Toast.LENGTH_SHORT).show();
            return;
        }

        if (phoneInput.length() != 8) {
            Toast.makeText(this, "Phone number must be exactly 8 digits", Toast.LENGTH_SHORT).show();
            return;
        }

        if (payMode.equals("Card")) {
            if (etCardName.getText().toString().isEmpty() || etCardNumber.getText().toString().isEmpty() ||
                etCardExpiry.getText().toString().isEmpty() || etCardCVV.getText().toString().isEmpty()) {
                Toast.makeText(this, "Please fill card details", Toast.LENGTH_SHORT).show();
                return;
            }
        }

        int quantity = Integer.parseInt(qtyStr);
        String fullPhoneNumber = "+968 " + phoneInput;
        String payStatus = payMode.equals("Card") ? "Received" : "Pending";
        String orderStatus = "Pending";
        String date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date());

        boolean success = db.addOrder(userEmail, medicineName, quantity, address, fullPhoneNumber, payMode, payStatus, orderStatus, date);

        if (success) {
            Toast.makeText(this, "Order placed successfully!", Toast.LENGTH_LONG).show();
            finish();
        } else {
            Toast.makeText(this, "Failed to place order", Toast.LENGTH_SHORT).show();
        }
    }
}
