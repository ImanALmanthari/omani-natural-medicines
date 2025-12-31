package com.example.omanimedicien;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ManageOrdersActivity extends AppCompatActivity {

    RecyclerView rvOrders;
    DbSQLlite db;
    AdminOrderAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_manage_orders);

        db = new DbSQLlite(this);
        rvOrders = findViewById(R.id.rvManageOrders);
        rvOrders.setLayoutManager(new LinearLayoutManager(this));

        loadOrders();
    }

    private void loadOrders() {
        List<Order> orders = db.getAllOrders();
        adapter = new AdminOrderAdapter(orders);
        rvOrders.setAdapter(adapter);
    }

    class AdminOrderAdapter extends RecyclerView.Adapter<AdminOrderAdapter.OrderViewHolder> {
        List<Order> orderList;

        AdminOrderAdapter(List<Order> list) { this.orderList = list; }

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
            holder.tvStatus.setText(order.getStatus());
            
            // Admin specific fields
            holder.tvUserEmail.setVisibility(View.VISIBLE);
            holder.tvUserEmail.setText("Customer: " + order.getUserEmail());
            holder.tvPhone.setVisibility(View.VISIBLE);
            holder.tvPhone.setText("Phone: " + order.getPhone());
            holder.layoutAdminActions.setVisibility(View.VISIBLE);

            // Hide actions if already completed or removed
            if (!order.getStatus().equals("Pending")) {
                holder.layoutAdminActions.setVisibility(View.GONE);
            }

            holder.btnComplete.setOnClickListener(v -> {
                new AlertDialog.Builder(ManageOrdersActivity.this)
                        .setTitle("Complete Order")
                        .setMessage("Mark this order as completed?")
                        .setPositiveButton("Yes", (dialog, which) -> {
                            if (db.updateOrderStatus(order.getId(), "Completed")) {
                                Toast.makeText(ManageOrdersActivity.this, "Order marked as Completed", Toast.LENGTH_SHORT).show();
                                loadOrders();
                            }
                        })
                        .setNegativeButton("No", null)
                        .show();
            });

            holder.btnRemove.setOnClickListener(v -> {
                new AlertDialog.Builder(ManageOrdersActivity.this)
                        .setTitle("Remove Order")
                        .setMessage("Are you sure you want to remove/cancel this order?")
                        .setPositiveButton("Yes", (dialog, which) -> {
                            if (db.updateOrderStatus(order.getId(), "Removed")) {
                                Toast.makeText(ManageOrdersActivity.this, "Order removed", Toast.LENGTH_SHORT).show();
                                loadOrders();
                            }
                        })
                        .setNegativeButton("No", null)
                        .show();
            });
        }

        @Override
        public int getItemCount() { return orderList.size(); }

        class OrderViewHolder extends RecyclerView.ViewHolder {
            TextView tvMedName, tvDate, tvQty, tvPayMode, tvAddress, tvStatus, tvUserEmail, tvPhone;
            LinearLayout layoutAdminActions;
            Button btnComplete, btnRemove;
            
            OrderViewHolder(View v) {
                super(v);
                tvMedName = v.findViewById(R.id.tvOrderMedName);
                tvDate = v.findViewById(R.id.tvOrderDate);
                tvQty = v.findViewById(R.id.tvOrderQty);
                tvPayMode = v.findViewById(R.id.tvOrderPayMode);
                tvAddress = v.findViewById(R.id.tvOrderAddress);
                tvStatus = v.findViewById(R.id.tvOrderStatus);
                tvUserEmail = v.findViewById(R.id.tvOrderUserEmail);
                tvPhone = v.findViewById(R.id.tvOrderPhone);
                layoutAdminActions = v.findViewById(R.id.layoutAdminActions);
                btnComplete = v.findViewById(R.id.btnCompleteOrder);
                btnRemove = v.findViewById(R.id.btnRemoveOrder);
            }
        }
    }
}
