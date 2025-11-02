package com.example.vgaapp.ui.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.vgaapp.R;
import com.example.vgaapp.data.dao.VGADAO;

import java.io.File;
import java.util.List;

public class VGAAdapter extends RecyclerView.Adapter<VGAAdapter.VGAViewHolder> {
    private List<VGADAO.VGAData> vgaList;
    private OnItemClickListener onItemClickListener;

    public interface OnItemClickListener {
        void onItemClick(VGADAO.VGAData vga);
    }

    public VGAAdapter(List<VGADAO.VGAData> vgaList, OnItemClickListener listener) {
        this.vgaList = vgaList;
        this.onItemClickListener = listener;
    }

    @NonNull
    @Override
    public VGAViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_vga, parent, false);
        return new VGAViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull VGAViewHolder holder, int position) {
        VGADAO.VGAData vga = vgaList.get(position);
        holder.textName.setText(vga.name);
        holder.textPrice.setText(String.valueOf((long) vga.price) + " VNĐ");
        holder.textBrand.setText(vga.brand.name());

        if (vga.imagePath != null && !vga.imagePath.isEmpty()) {
            File imageFile = new File(vga.imagePath);
            if (imageFile.exists()) {
                android.graphics.Bitmap bitmap = android.graphics.BitmapFactory.decodeFile(imageFile.getAbsolutePath());
                if (bitmap != null) {
                    holder.imageView.setImageBitmap(bitmap);
                } else {
                    holder.imageView.setImageResource(android.R.color.darker_gray);
                }
            } else {
                holder.imageView.setImageResource(android.R.color.darker_gray);
            }
        } else {
            holder.imageView.setImageResource(android.R.color.darker_gray);
        }

        holder.itemView.setOnClickListener(v -> {
            if (onItemClickListener != null) {
                onItemClickListener.onItemClick(vga);
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

    static class VGAViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView textName;
        TextView textPrice;
        TextView textBrand;

        VGAViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageViewVGA);
            textName = itemView.findViewById(R.id.textName);
            textPrice = itemView.findViewById(R.id.textPrice);
            textBrand = itemView.findViewById(R.id.textBrand);
        }
    }
}
