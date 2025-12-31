package com.example.omanimedicien;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class UserOrdersActivity extends AppCompatActivity {

    RecyclerView rvOrders;
    DbSQLlite db;
    String userEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_user_orders);

        db = new DbSQLlite(this);
        userEmail = getIntent().getStringExtra("USER_EMAIL");

        rvOrders = findViewById(R.id.rvUserOrders);
        rvOrders.setLayoutManager(new LinearLayoutManager(this));

        loadOrders();
    }

    private void loadOrders() {
        List<Order> orders = db.getOrdersByUser(userEmail);
        OrderAdapter adapter = new OrderAdapter(orders);
        rvOrders.setAdapter(adapter);
    }

    class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.OrderViewHolder> {
        List<Order> orderList;

        OrderAdapter(List<Order> list) { this.orderList = list; }

        @NonNull
        @Override
        public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_order, parent, false);
            return new OrderViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull OrderViewHolder holder, int position) {
            Order order = orderList.get(position);
            holder.tvMedName.setText(order.getMedicineName());
            holder.tvDate.setText(order.getDate());
            holder.tvQty.setText("Qty: " + order.getQuantity());
            holder.tvPayMode.setText(order.getPaymentMode());
            holder.tvAddress.setText(order.getAddress());
            holder.tvPhone.setText("Phone: " + order.getPhone());
            holder.tvStatus.setText(order.getStatus());
        }

        @Override
        public int getItemCount() { return orderList.size(); }

        class OrderViewHolder extends RecyclerView.ViewHolder {
            TextView tvMedName, tvDate, tvQty, tvPayMode, tvAddress, tvPhone, tvStatus;
            OrderViewHolder(View v) {
                super(v);
                tvMedName = v.findViewById(R.id.tvOrderMedName);
                tvDate = v.findViewById(R.id.tvOrderDate);
                tvQty = v.findViewById(R.id.tvOrderQty);
                tvPayMode = v.findViewById(R.id.tvOrderPayMode);
                tvAddress = v.findViewById(R.id.tvOrderAddress);
                tvPhone = v.findViewById(R.id.tvOrderPhone);
                tvStatus = v.findViewById(R.id.tvOrderStatus);
            }
        }
    }
}
