package com.example.vgaapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;

import com.example.vgaapp.data.database.DatabaseHelper;
import com.example.vgaapp.data.dao.CartDAO;
import com.example.vgaapp.data.dao.OrderDAO;
import com.example.vgaapp.data.dao.VGADAO;
import com.example.vgaapp.databinding.ActivityUserHomeBinding;
import com.example.vgaapp.ui.adapter.VGAAdapter;
import com.example.vgaapp.util.SharedPreferencesHelper;

import java.util.ArrayList;

public class UserHomeActivity extends AppCompatActivity {
    private ActivityUserHomeBinding binding;
    private DatabaseHelper dbHelper;
    private VGADAO vgaDAO;
    private CartDAO cartDAO;
    private OrderDAO orderDAO;
    private SharedPreferencesHelper prefsHelper;
    private VGAAdapter vgaAdapter;
    private long currentUserId = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityUserHomeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        dbHelper = new DatabaseHelper(this);
        vgaDAO = new VGADAO(dbHelper.getReadableDatabase());
        cartDAO = new CartDAO(dbHelper.getWritableDatabase(), vgaDAO);
        orderDAO = new OrderDAO(dbHelper.getWritableDatabase(), vgaDAO);
        prefsHelper = new SharedPreferencesHelper(this);

        currentUserId = prefsHelper.getUserId();
        if (currentUserId == -1L) {
            Toast.makeText(this, "Vui lòng đăng nhập", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        setupRecyclerView();
        loadVGA();

        binding.btnCart.setOnClickListener(v -> {
            startActivity(new Intent(this, CartActivity.class));
        });

        binding.btnLogout.setOnClickListener(v -> {
            prefsHelper.clearUserData();
            startActivity(new Intent(this, MainActivity.class));
            finish();
        });
    }

    private void setupRecyclerView() {
        vgaAdapter = new VGAAdapter(new ArrayList<>(), vga -> {
            Intent intent = new Intent(this, VGADetailActivity.class);
            intent.putExtra("VGA_ID", vga.id);
            startActivity(intent);
        });
        binding.recyclerViewVGA.setLayoutManager(new GridLayoutManager(this, 2));
        binding.recyclerViewVGA.setAdapter(vgaAdapter);
    }

    private void loadVGA() {
        vgaAdapter.updateList(vgaDAO.getAllVGA());
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadVGA();
    }
}
