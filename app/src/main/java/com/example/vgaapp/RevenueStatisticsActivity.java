package com.example.vgaapp;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.vgaapp.data.database.DatabaseHelper;
import com.example.vgaapp.data.dao.OrderDAO;
import com.example.vgaapp.data.dao.VGADAO;
import com.example.vgaapp.databinding.ActivityRevenueStatisticsBinding;
import com.example.vgaapp.util.SharedPreferencesHelper;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class RevenueStatisticsActivity extends AppCompatActivity {
    private ActivityRevenueStatisticsBinding binding;
    private DatabaseHelper dbHelper;
    private OrderDAO orderDAO;
    private SharedPreferencesHelper prefsHelper;
    private long currentUserId = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRevenueStatisticsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        dbHelper = new DatabaseHelper(this);
        VGADAO vgaDAO = new VGADAO(dbHelper.getReadableDatabase());
        orderDAO = new OrderDAO(dbHelper.getReadableDatabase(), vgaDAO);
        prefsHelper = new SharedPreferencesHelper(this);

        currentUserId = prefsHelper.getUserId();
        if (currentUserId == -1L) {
            Toast.makeText(this, "Vui lòng đăng nhập", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        loadTotalRevenue();

        binding.btnFilterDay.setOnClickListener(v -> {
            showDayPicker();
        });

        binding.btnFilterMonth.setOnClickListener(v -> {
            showMonthPicker();
        });

        binding.btnFilterYear.setOnClickListener(v -> {
            showYearPicker();
        });

        binding.btnShowAll.setOnClickListener(v -> {
            loadTotalRevenue();
        });
    }

    private void loadTotalRevenue() {
        double totalRevenue = orderDAO.getTotalRevenue(currentUserId);
        binding.textRevenue.setText("Tổng doanh thu: " + formatCurrency(totalRevenue));
        binding.textFilterInfo.setText("Tất cả thời gian");
    }

    private void showDayPicker() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        new android.app.DatePickerDialog(this, (view, selectedYear, selectedMonth, selectedDay) -> {
            Calendar selectedDate = Calendar.getInstance();
            selectedDate.set(selectedYear, selectedMonth, selectedDay);
            long timestamp = selectedDate.getTimeInMillis();
            
            double revenue = orderDAO.getRevenueByDay(currentUserId, timestamp);
            binding.textRevenue.setText("Doanh thu: " + formatCurrency(revenue));
            
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            binding.textFilterInfo.setText("Ngày: " + sdf.format(selectedDate.getTime()));
        }, year, month, day).show();
    }

    private void showMonthPicker() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);

        new android.app.DatePickerDialog(this, (view, selectedYear, selectedMonth, selectedDay) -> {
            double revenue = orderDAO.getRevenueByMonth(currentUserId, selectedYear, selectedMonth + 1);
            binding.textRevenue.setText("Doanh thu: " + formatCurrency(revenue));
            
            String[] monthNames = {"Tháng 1", "Tháng 2", "Tháng 3", "Tháng 4", "Tháng 5", "Tháng 6",
                    "Tháng 7", "Tháng 8", "Tháng 9", "Tháng 10", "Tháng 11", "Tháng 12"};
            binding.textFilterInfo.setText("Tháng: " + monthNames[selectedMonth] + "/" + selectedYear);
        }, year, month, 1).show();
    }

    private void showYearPicker() {
        Calendar calendar = Calendar.getInstance();
        int currentYear = calendar.get(Calendar.YEAR);
        
        String[] years = new String[10];
        for (int i = 0; i < 10; i++) {
            years[i] = String.valueOf(currentYear - i);
        }
        
        new androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle("Chọn năm")
                .setItems(years, (dialog, which) -> {
                    int selectedYear = Integer.parseInt(years[which]);
                    double revenue = orderDAO.getRevenueByYear(currentUserId, selectedYear);
                    binding.textRevenue.setText("Doanh thu: " + formatCurrency(revenue));
                    binding.textFilterInfo.setText("Năm: " + selectedYear);
                })
                .show();
    }

    private String formatCurrency(double amount) {
        return String.format(Locale.getDefault(), "%,.0f VNĐ", amount);
    }
}

