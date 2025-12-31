package com.example.omanimedicien;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ManageUsersActivity extends AppCompatActivity {

    RecyclerView rvUsers;
    Button btnAddUser;
    DbSQLlite db;
    UserAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_manage_users);

        db = new DbSQLlite(this);
        rvUsers = findViewById(R.id.rvUsers);
        btnAddUser = findViewById(R.id.btnAddUser);

        rvUsers.setLayoutManager(new LinearLayoutManager(this));
        loadUsers();

        btnAddUser.setOnClickListener(v -> {
            startActivity(new Intent(ManageUsersActivity.this, SignUpActivity2.class));
        });
    }

    private void loadUsers() {
        List<User> users = db.getAllUsers();
        adapter = new UserAdapter(users);
        rvUsers.setAdapter(adapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadUsers();
    }

    class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserViewHolder> {
        List<User> userList;

        UserAdapter(List<User> list) { this.userList = list; }

        @NonNull
        @Override
        public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_user, parent, false);
            return new UserViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
            User user = userList.get(position);
            holder.tvEmail.setText(user.getEmail());

            holder.btnDelete.setOnClickListener(v -> {
                new AlertDialog.Builder(ManageUsersActivity.this)
                        .setTitle("Delete User")
                        .setMessage("Are you sure you want to delete " + user.getEmail() + "?")
                        .setPositiveButton("Yes", (dialog, which) -> {
                            db.deleteUser(user.getId());
                            loadUsers();
                            Toast.makeText(ManageUsersActivity.this, "User deleted", Toast.LENGTH_SHORT).show();
                        })
                        .setNegativeButton("No", null)
                        .show();
            });

            holder.btnView.setOnClickListener(v -> {
                Intent intent = new Intent(ManageUsersActivity.this, ViewUserActivity.class);
                intent.putExtra("USER_ID", user.getId());
                startActivity(intent);
            });
        }

        @Override
        public int getItemCount() { return userList.size(); }

        class UserViewHolder extends RecyclerView.ViewHolder {
            TextView tvEmail;
            ImageButton btnView, btnDelete;
            UserViewHolder(View v) {
                super(v);
                tvEmail = v.findViewById(R.id.tvUserEmailItem);
                btnView = v.findViewById(R.id.btnViewUser);
                btnDelete = v.findViewById(R.id.btnDeleteUser);
            }
        }
    }
}
