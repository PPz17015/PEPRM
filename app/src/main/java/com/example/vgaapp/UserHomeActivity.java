package com.example.vgaapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
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
import java.util.List;

public class UserHomeActivity extends AppCompatActivity {
    private ActivityUserHomeBinding binding;
    private DatabaseHelper dbHelper;
    private VGADAO vgaDAO;
    private CartDAO cartDAO;
    private OrderDAO orderDAO;
    private SharedPreferencesHelper prefsHelper;
    private VGAAdapter vgaAdapter;
    private long currentUserId = -1;
    private List<VGADAO.VGAData> allVGAList = new ArrayList<>();

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

        // Search functionality
        binding.etSearch.addTextChangedListener(new android.text.TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(android.text.Editable s) {
                String query = s.toString().trim();
                if (query.isEmpty()) {
                    loadVGA();
                } else {
                    List<VGADAO.VGAData> searchResults = vgaDAO.searchVGAByName(query);
                    vgaAdapter.updateList(searchResults);
                }
            }
        });

        // Sort functionality
        binding.btnSort.setOnClickListener(v -> {
            String[] sortOptions = {"Giá tăng dần", "Giá giảm dần", "Bỏ sắp xếp"};
            new AlertDialog.Builder(this)
                    .setTitle("Sắp xếp sản phẩm")
                    .setItems(sortOptions, (dialog, which) -> {
                        String searchQuery = binding.etSearch.getText().toString().trim();
                        List<VGADAO.VGAData> listToSort;
                        
                        if (!searchQuery.isEmpty()) {
                            listToSort = vgaDAO.searchVGAByName(searchQuery);
                        } else {
                            listToSort = allVGAList;
                        }
                        
                        switch (which) {
                            case 0: // Ascending
                                listToSort.sort((v1, v2) -> Double.compare(v1.price, v2.price));
                                break;
                            case 1: // Descending
                                listToSort.sort((v1, v2) -> Double.compare(v2.price, v1.price));
                                break;
                            case 2: // Reset
                                if (searchQuery.isEmpty()) {
                                    loadVGA();
                                } else {
                                    listToSort = vgaDAO.searchVGAByName(searchQuery);
                                }
                                break;
                        }
                        vgaAdapter.updateList(listToSort);
                    })
                    .show();
        });

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
        allVGAList = vgaDAO.getAllVGA();
        vgaAdapter.updateList(allVGAList);
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadVGA();
    }
}
