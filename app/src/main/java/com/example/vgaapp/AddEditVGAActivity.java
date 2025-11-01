package com.example.vgaapp;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.vgaapp.data.database.DatabaseHelper;
import com.example.vgaapp.data.dao.VGADAO;
import com.example.vgaapp.data.model.Brand;
import com.example.vgaapp.data.model.Condition;
import com.example.vgaapp.databinding.ActivityAddEditVgaBinding;
import com.example.vgaapp.util.SharedPreferencesHelper;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

public class AddEditVGAActivity extends AppCompatActivity {
    private ActivityAddEditVgaBinding binding;
    private DatabaseHelper dbHelper;
    private VGADAO vgaDAO;
    private SharedPreferencesHelper prefsHelper;
    private long vgaId = -1;
    private long currentUserId = -1;
    private String imagePath = "";

    private static final int REQUEST_CODE_IMAGE = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddEditVgaBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        vgaId = getIntent().getLongExtra("VGA_ID", -1);
        dbHelper = new DatabaseHelper(this);
        vgaDAO = new VGADAO(dbHelper.getWritableDatabase());
        prefsHelper = new SharedPreferencesHelper(this);
        currentUserId = prefsHelper.getUserId();

        setupSpinners();

        if (vgaId != -1L) {
            loadVGAData();
            binding.btnSave.setText("Cập nhật");
        } else {
            binding.btnSave.setText("Thêm mới");
        }

        binding.btnSelectImage.setOnClickListener(v -> selectImage());

        binding.btnSave.setOnClickListener(v -> saveVGA());
    }

    private void setupSpinners() {
        String[] brands = {"AMD", "NVIDIA"};
        ArrayAdapter<String> brandAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, brands);
        brandAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.spinnerBrand.setAdapter(brandAdapter);

        String[] conditions = {"Mới", "Đã qua sử dụng"};
        ArrayAdapter<String> conditionAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, conditions);
        conditionAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.spinnerCondition.setAdapter(conditionAdapter);
    }

    private void selectImage() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, REQUEST_CODE_IMAGE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_IMAGE && resultCode == Activity.RESULT_OK && data != null) {
            Uri selectedImage = data.getData();
            if (selectedImage != null) {
                try {
                    InputStream inputStream = getContentResolver().openInputStream(selectedImage);
                    File file = new File(getExternalFilesDir(null), "vga_" + System.currentTimeMillis() + ".jpg");
                    FileOutputStream outputStream = new FileOutputStream(file);

                    byte[] buffer = new byte[1024];
                    int length;
                    if (inputStream != null) {
                        while ((length = inputStream.read(buffer)) > 0) {
                            outputStream.write(buffer, 0, length);
                        }
                        inputStream.close();
                    }
                    outputStream.close();

                    imagePath = file.getAbsolutePath();
                    binding.textImagePath.setText("Đã chọn ảnh: " + file.getName());
                    Toast.makeText(this, "Đã chọn ảnh", Toast.LENGTH_SHORT).show();
                } catch (Exception e) {
                    Toast.makeText(this, "Lỗi khi lưu ảnh: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    private void loadVGAData() {
        VGADAO.VGAData vga = vgaDAO.getVGAById(vgaId);
        if (vga != null) {
            binding.etName.setText(vga.name);
            binding.spinnerBrand.setSelection(vga.brand == Brand.AMD ? 0 : 1);
            binding.spinnerCondition.setSelection(vga.condition == Condition.NEW ? 0 : 1);
            binding.etDescription.setText(vga.description);
            binding.etPrice.setText(String.valueOf(vga.price));
            binding.etQuantity.setText(String.valueOf(vga.quantity));
            imagePath = vga.imagePath;
            if (imagePath != null && !imagePath.isEmpty()) {
                binding.textImagePath.setText("Ảnh: " + new File(imagePath).getName());
            }
        }
    }

    private void saveVGA() {
        String name = binding.etName.getText().toString().trim();
        String description = binding.etDescription.getText().toString().trim();
        String priceText = binding.etPrice.getText().toString().trim();
        String quantityText = binding.etQuantity.getText().toString().trim();

        if (name.isEmpty() || description.isEmpty() || priceText.isEmpty() || quantityText.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show();
            return;
        }

        if ((imagePath == null || imagePath.isEmpty()) && vgaId == -1L) {
            Toast.makeText(this, "Vui lòng chọn ảnh", Toast.LENGTH_SHORT).show();
            return;
        }

        double price;
        int quantity;
        try {
            price = Double.parseDouble(priceText);
            quantity = Integer.parseInt(quantityText);
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Giá hoặc số lượng không hợp lệ", Toast.LENGTH_SHORT).show();
            return;
        }

        if (price <= 0) {
            Toast.makeText(this, "Giá phải lớn hơn 0", Toast.LENGTH_SHORT).show();
            return;
        }

        if (quantity < 0) {
            Toast.makeText(this, "Số lượng phải lớn hơn hoặc bằng 0", Toast.LENGTH_SHORT).show();
            return;
        }

        Brand brand = binding.spinnerBrand.getSelectedItemPosition() == 0 ? Brand.AMD : Brand.NVIDIA;
        Condition condition = binding.spinnerCondition.getSelectedItemPosition() == 0 ? Condition.NEW : Condition.SECOND_HAND;

        VGADAO.VGAData vga;
        if (vgaId != -1L) {
            vga = new VGADAO.VGAData(vgaId, name, brand, condition, description, price, quantity, imagePath, currentUserId);
            vgaDAO.updateVGA(vga);
            Toast.makeText(this, "Cập nhật VGA thành công", Toast.LENGTH_SHORT).show();
        } else {
            vga = new VGADAO.VGAData(name, brand, condition, description, price, quantity, imagePath, currentUserId);
            vgaDAO.insertVGA(vga);
            Toast.makeText(this, "Thêm VGA thành công", Toast.LENGTH_SHORT).show();
        }
        finish();
    }
}
