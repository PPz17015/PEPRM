package com.example.vgaapp;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.vgaapp.data.database.DatabaseHelper;
import com.example.vgaapp.data.dao.CartDAO;
import com.example.vgaapp.data.dao.VGADAO;
import com.example.vgaapp.databinding.ActivityVgadetailBinding;
import com.example.vgaapp.util.SharedPreferencesHelper;

import java.io.File;

public class VGADetailActivity extends AppCompatActivity {
    private ActivityVgadetailBinding binding;
    private DatabaseHelper dbHelper;
    private VGADAO vgaDAO;
    private CartDAO cartDAO;
    private SharedPreferencesHelper prefsHelper;
    private long vgaId = -1;
    private long currentUserId = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityVgadetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        vgaId = getIntent().getLongExtra("VGA_ID", -1);
        if (vgaId == -1L) {
            Toast.makeText(this, "Không tìm thấy VGA", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        dbHelper = new DatabaseHelper(this);
        vgaDAO = new VGADAO(dbHelper.getReadableDatabase());
        cartDAO = new CartDAO(dbHelper.getWritableDatabase(), vgaDAO);
        prefsHelper = new SharedPreferencesHelper(this);
        currentUserId = prefsHelper.getUserId();

        loadVGADetails();

        binding.btnAddToCart.setOnClickListener(v -> {
            try {
                int quantity = Integer.parseInt(binding.etQuantity.getText().toString());
                if (quantity > 0) {
                    cartDAO.addToCart(currentUserId, vgaId, quantity);
                    Toast.makeText(this, "Đã thêm vào giỏ hàng", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "Số lượng phải lớn hơn 0", Toast.LENGTH_SHORT).show();
                }
            } catch (NumberFormatException e) {
                Toast.makeText(this, "Số lượng không hợp lệ", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadVGADetails() {
        VGADAO.VGAData vga = vgaDAO.getVGAById(vgaId);
        if (vga != null) {
            binding.textName.setText(vga.name);
            binding.textBrand.setText("Hãng: " + vga.brand.name());
            String conditionText = vga.condition.name().equals("NEW") ? "Mới" : "Đã qua sử dụng";
            binding.textCondition.setText("Tình trạng: " + conditionText);
            binding.textDescription.setText(vga.description);
            binding.textPrice.setText(String.valueOf((long) vga.price) + " VNĐ");
            binding.textQuantity.setText("Số lượng còn lại: " + vga.quantity);

            if (vga.imagePath != null && !vga.imagePath.isEmpty()) {
                File imageFile = new File(vga.imagePath);
                if (imageFile.exists()) {
                    android.graphics.Bitmap bitmap = android.graphics.BitmapFactory.decodeFile(imageFile.getAbsolutePath());
                    if (bitmap != null) {
                        binding.imageViewVGA.setImageBitmap(bitmap);
                    } else {
                        binding.imageViewVGA.setImageResource(android.R.color.darker_gray);
                    }
                } else {
                    binding.imageViewVGA.setImageResource(android.R.color.darker_gray);
                }
            } else {
                binding.imageViewVGA.setImageResource(android.R.color.darker_gray);
            }
        } else {
            Toast.makeText(this, "Không tìm thấy VGA", Toast.LENGTH_SHORT).show();
            finish();
        }
    }
}
