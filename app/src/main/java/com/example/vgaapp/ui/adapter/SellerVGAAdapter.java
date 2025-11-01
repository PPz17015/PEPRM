package com.example.vgaapp.ui.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.vgaapp.R;
import com.example.vgaapp.data.dao.VGADAO;

import java.util.List;

public class SellerVGAAdapter extends RecyclerView.Adapter<SellerVGAAdapter.SellerVGAViewHolder> {
    private List<VGADAO.VGAData> vgaList;
    private OnItemActionListener onItemActionListener;

    public interface OnItemActionListener {
        void onItemAction(VGADAO.VGAData vga, String action);
    }

    public SellerVGAAdapter(List<VGADAO.VGAData> vgaList, OnItemActionListener listener) {
        this.vgaList = vgaList;
        this.onItemActionListener = listener;
    }

    @NonNull
    @Override
    public SellerVGAViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_seller_vga, parent, false);
        return new SellerVGAViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SellerVGAViewHolder holder, int position) {
        VGADAO.VGAData vga = vgaList.get(position);
        holder.textName.setText(vga.name);
        holder.textBrand.setText("Hãng: " + vga.brand.name());
        holder.textPrice.setText(String.valueOf((long) vga.price) + " VNĐ");
        holder.textQuantity.setText("Số lượng: " + vga.quantity);

        holder.btnEdit.setOnClickListener(v -> {
            if (onItemActionListener != null) {
                onItemActionListener.onItemAction(vga, "edit");
            }
        });

        holder.btnDelete.setOnClickListener(v -> {
            if (onItemActionListener != null) {
                onItemActionListener.onItemAction(vga, "delete");
            }
        });
    }

    @Override
    public int getItemCount() {
        return vgaList.size();
    }

    public void updateList(List<VGADAO.VGAData> newList) {
        this.vgaList = newList;
        notifyDataSetChanged();
    }

    static class SellerVGAViewHolder extends RecyclerView.ViewHolder {
        TextView textName;
        TextView textBrand;
        TextView textPrice;
        TextView textQuantity;
        TextView btnEdit;
        TextView btnDelete;

        SellerVGAViewHolder(@NonNull View itemView) {
            super(itemView);
            textName = itemView.findViewById(R.id.textSellerVGAName);
            textBrand = itemView.findViewById(R.id.textSellerVGABrand);
            textPrice = itemView.findViewById(R.id.textSellerVGAPrice);
            textQuantity = itemView.findViewById(R.id.textSellerVGAQuantity);
            btnEdit = itemView.findViewById(R.id.btnEditVGA);
            btnDelete = itemView.findViewById(R.id.btnDeleteVGA);
        }
    }
}
