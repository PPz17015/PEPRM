package com.example.vgaapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.vgaapp.data.database.DatabaseHelper;
import com.example.vgaapp.data.dao.VGADAO;
import com.example.vgaapp.databinding.ActivitySellerHomeBinding;
import com.example.vgaapp.ui.adapter.SellerVGAAdapter;
import com.example.vgaapp.util.SharedPreferencesHelper;

import java.util.ArrayList;

public class SellerHomeActivity extends AppCompatActivity {
    private ActivitySellerHomeBinding binding;
    private DatabaseHelper dbHelper;
    private VGADAO vgaDAO;
    private SharedPreferencesHelper prefsHelper;
    private SellerVGAAdapter sellerVGAAdapter;
    private long currentUserId = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySellerHomeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        dbHelper = new DatabaseHelper(this);
        vgaDAO = new VGADAO(dbHelper.getReadableDatabase());
        prefsHelper = new SharedPreferencesHelper(this);

        currentUserId = prefsHelper.getUserId();
        if (currentUserId == -1L) {
            Toast.makeText(this, "Vui lòng đăng nhập", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        setupRecyclerView();
        loadVGA();

        binding.btnAddVGA.setOnClickListener(v -> {
            startActivity(new Intent(this, AddEditVGAActivity.class));
        });

        binding.btnRevenue.setOnClickListener(v -> {
            startActivity(new Intent(this, RevenueStatisticsActivity.class));
        });

        binding.btnLogout.setOnClickListener(v -> {
            prefsHelper.clearUserData();
            startActivity(new Intent(this, MainActivity.class));
            finish();
        });
    }

    private void setupRecyclerView() {
        sellerVGAAdapter = new SellerVGAAdapter(new ArrayList<>(), (vga, action) -> {
            if ("edit".equals(action)) {
                Intent intent = new Intent(this, AddEditVGAActivity.class);
                intent.putExtra("VGA_ID", vga.id);
                startActivity(intent);
            } else if ("delete".equals(action)) {
                new AlertDialog.Builder(this)
                        .setTitle("Xóa VGA")
                        .setMessage("Bạn có chắc muốn xóa VGA này?")
                        .setPositiveButton("Xóa", (dialog, which) -> {
                            vgaDAO.deleteVGA(vga.id);
                            Toast.makeText(this, "Đã xóa VGA", Toast.LENGTH_SHORT).show();
                            loadVGA();
                        })
                        .setNegativeButton("Hủy", null)
                        .show();
            }
        });
        binding.recyclerViewVGA.setLayoutManager(new LinearLayoutManager(this));
        binding.recyclerViewVGA.setAdapter(sellerVGAAdapter);
    }

    private void loadVGA() {
        sellerVGAAdapter.updateList(vgaDAO.getVGABySeller(currentUserId));
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadVGA();
    }
}
