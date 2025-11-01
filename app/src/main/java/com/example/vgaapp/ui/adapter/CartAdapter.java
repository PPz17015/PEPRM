package com.example.vgaapp.ui.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.vgaapp.R;
import com.example.vgaapp.data.dao.CartDAO;
import com.example.vgaapp.data.dao.VGADAO;

import java.util.List;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartViewHolder> {
    private List<CartDAO.CartItemData> cartItems;
    private OnItemClickListener onItemClickListener;

    public interface OnItemClickListener {
        void onItemClick(CartDAO.CartItemData cartItem);
    }

    public CartAdapter(List<CartDAO.CartItemData> cartItems, OnItemClickListener listener) {
        this.cartItems = cartItems;
        this.onItemClickListener = listener;
    }

    @NonNull
    @Override
    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_cart, parent, false);
        return new CartViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CartViewHolder holder, int position) {
        CartDAO.CartItemData cartItem = cartItems.get(position);
        VGADAO.VGAData vga = cartItem.vga;

        holder.textName.setText(vga != null ? vga.name : "Unknown");
        long price = vga != null ? (long) vga.price : 0;
        holder.textPrice.setText(price + " VNĐ");
        holder.textQuantity.setText("Số lượng: " + cartItem.quantity);
        double total = (vga != null ? vga.price : 0.0) * cartItem.quantity;
        holder.textTotal.setText("Tổng: " + (long) total + " VNĐ");

        holder.itemView.setOnClickListener(v -> {
            if (onItemClickListener != null) {
                onItemClickListener.onItemClick(cartItem);
            }
        });
    }

    @Override
    public int getItemCount() {
        return cartItems.size();
    }

    public void updateList(List<CartDAO.CartItemData> newList) {
        this.cartItems = newList;
        notifyDataSetChanged();
    }

    static class CartViewHolder extends RecyclerView.ViewHolder {
        TextView textName;
        TextView textPrice;
        TextView textQuantity;
        TextView textTotal;

        CartViewHolder(@NonNull View itemView) {
            super(itemView);
            textName = itemView.findViewById(R.id.textCartItemName);
            textPrice = itemView.findViewById(R.id.textCartItemPrice);
            textQuantity = itemView.findViewById(R.id.textCartItemQuantity);
            textTotal = itemView.findViewById(R.id.textCartItemTotal);
        }
    }
}
