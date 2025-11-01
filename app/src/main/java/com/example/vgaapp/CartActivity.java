package com.example.vgaapp;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.vgaapp.data.database.DatabaseHelper;
import com.example.vgaapp.data.dao.CartDAO;
import com.example.vgaapp.data.dao.OrderDAO;
import com.example.vgaapp.data.dao.VGADAO;
import com.example.vgaapp.databinding.ActivityCartBinding;
import com.example.vgaapp.ui.adapter.CartAdapter;
import com.example.vgaapp.util.SharedPreferencesHelper;

import java.util.ArrayList;
import java.util.List;

public class CartActivity extends AppCompatActivity {
    private ActivityCartBinding binding;
    private DatabaseHelper dbHelper;
    private CartDAO cartDAO;
    private OrderDAO orderDAO;
    private SharedPreferencesHelper prefsHelper;
    private CartAdapter cartAdapter;
    private long currentUserId = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCartBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        dbHelper = new DatabaseHelper(this);
        VGADAO vgaDAO = new VGADAO(dbHelper.getReadableDatabase());
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
        loadCartItems();

        binding.btnCheckout.setOnClickListener(v -> checkout());
    }

    private void setupRecyclerView() {
        cartAdapter = new CartAdapter(new ArrayList<>(), cartItem -> {
            new AlertDialog.Builder(this)
                    .setTitle("Xóa sản phẩm")
                    .setMessage("Bạn có chắc muốn xóa sản phẩm này khỏi giỏ hàng?")
                    .setPositiveButton("Xóa", (dialog, which) -> {
                        cartDAO.removeFromCart(currentUserId, cartItem.vgaId);
                        loadCartItems();
                    })
                    .setNegativeButton("Hủy", null)
                    .show();
        });
        binding.recyclerViewCart.setLayoutManager(new LinearLayoutManager(this));
        binding.recyclerViewCart.setAdapter(cartAdapter);
    }

    private void loadCartItems() {
        List<CartDAO.CartItemData> cartItems = cartDAO.getCartItems(currentUserId);
        cartAdapter.updateList(cartItems);

        double total = 0;
        for (CartDAO.CartItemData item : cartItems) {
            if (item.vga != null) {
                total += item.vga.price * item.quantity;
            }
        }
        binding.textTotal.setText("Tổng tiền: " + (long) total + " VNĐ");
    }

    private void checkout() {
        List<CartDAO.CartItemData> cartItems = cartDAO.getCartItems(currentUserId);
        if (cartItems.isEmpty()) {
            Toast.makeText(this, "Giỏ hàng trống", Toast.LENGTH_SHORT).show();
            return;
        }

        for (CartDAO.CartItemData cartItem : cartItems) {
            VGADAO.VGAData vga = cartItem.vga;
            if (vga == null || vga.quantity < cartItem.quantity) {
                Toast.makeText(this, "Sản phẩm " + (vga != null ? vga.name : "") + " không đủ số lượng", Toast.LENGTH_SHORT).show();
                return;
            }
        }

        new AlertDialog.Builder(this)
                .setTitle("Xác nhận thanh toán")
                .setMessage("Bạn có chắc muốn thanh toán đơn hàng này?")
                .setPositiveButton("Thanh toán", (dialog, which) -> {
                    List<OrderDAO.OrderItemData> orderItems = new ArrayList<>();
                    for (CartDAO.CartItemData item : cartItems) {
                        orderItems.add(new OrderDAO.OrderItemData(
                                item.vgaId,
                                item.quantity,
                                item.vga != null ? item.vga.price : 0.0
                        ));
                    }
                    orderDAO.createOrder(currentUserId, orderItems);
                    cartDAO.clearCart(currentUserId);
                    Toast.makeText(this, "Thanh toán thành công", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .setNegativeButton("Hủy", null)
                .show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadCartItems();
    }
}
