package com.example.vgaapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.vgaapp.data.database.DatabaseHelper;
import com.example.vgaapp.data.dao.UserDAO;
import com.example.vgaapp.data.model.UserRole;
import com.example.vgaapp.databinding.ActivityMainBinding;
import com.example.vgaapp.util.SharedPreferencesHelper;

public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding binding;
    private DatabaseHelper dbHelper;
    private UserDAO userDAO;
    private SharedPreferencesHelper prefsHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        dbHelper = new DatabaseHelper(this);
        userDAO = new UserDAO(dbHelper.getWritableDatabase());
        prefsHelper = new SharedPreferencesHelper(this);

        // Check if user is already logged in
        if (prefsHelper.isLoggedIn()) {
            String role = prefsHelper.getUserRole();
            if (role != null && role.equals(UserRole.SELLER.name())) {
                startActivity(new Intent(this, SellerHomeActivity.class));
            } else {
                startActivity(new Intent(this, UserHomeActivity.class));
            }
            finish();
            return;
        }

        binding.btnLogin.setOnClickListener(v -> {
            String email = binding.etEmail.getText().toString().trim();
            String password = binding.etPassword.getText().toString().trim();

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show();
                return;
            }

            Long userId = userDAO.login(email, password);
            if (userId != null) {
                UserDAO.UserData user = userDAO.getUserById(userId);
                if (user != null) {
                    prefsHelper.saveUserId(user.id);
                    prefsHelper.saveUserRole(user.role.name());
                    Toast.makeText(this, "Đăng nhập thành công", Toast.LENGTH_SHORT).show();

                    if (user.role == UserRole.SELLER) {
                        startActivity(new Intent(this, SellerHomeActivity.class));
                    } else {
                        startActivity(new Intent(this, UserHomeActivity.class));
                    }
                    finish();
                }
            } else {
                Toast.makeText(this, "Email hoặc mật khẩu không đúng", Toast.LENGTH_SHORT).show();
            }
        });

        binding.btnRegister.setOnClickListener(v -> {
            startActivity(new Intent(this, RegisterActivity.class));
        });
    }
}
