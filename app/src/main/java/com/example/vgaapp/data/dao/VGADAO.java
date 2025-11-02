package com.example.vgaapp.data.dao;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.example.vgaapp.data.database.DatabaseHelper;
import com.example.vgaapp.data.model.Brand;
import com.example.vgaapp.data.model.Condition;

import java.util.ArrayList;
import java.util.List;

public class VGADAO {
    private SQLiteDatabase db;

    public VGADAO(SQLiteDatabase db) {
        this.db = db;
    }

    public List<VGAData> getAllVGA() {
        Cursor cursor = db.query(DatabaseHelper.TABLE_VGA, null, null, null, null, null, null);
        List<VGAData> vgaList = new ArrayList<>();
        while (cursor.moveToNext()) {
            vgaList.add(new VGAData(
                    cursor.getLong(cursor.getColumnIndex(DatabaseHelper.COL_VGA_ID)),
                    cursor.getString(cursor.getColumnIndex(DatabaseHelper.COL_VGA_NAME)),
                    Brand.valueOf(cursor.getString(cursor.getColumnIndex(DatabaseHelper.COL_VGA_BRAND))),
                    Condition.valueOf(cursor.getString(cursor.getColumnIndex(DatabaseHelper.COL_VGA_CONDITION))),
                    cursor.getString(cursor.getColumnIndex(DatabaseHelper.COL_VGA_DESCRIPTION)),
                    cursor.getDouble(cursor.getColumnIndex(DatabaseHelper.COL_VGA_PRICE)),
                    cursor.getInt(cursor.getColumnIndex(DatabaseHelper.COL_VGA_QUANTITY)),
                    cursor.getString(cursor.getColumnIndex(DatabaseHelper.COL_VGA_IMAGE_PATH)),
                    cursor.getLong(cursor.getColumnIndex(DatabaseHelper.COL_VGA_SELLER_ID))
            ));
        }
        cursor.close();
        return vgaList;
    }

    public VGAData getVGAById(long id) {
        Cursor cursor = db.query(
                DatabaseHelper.TABLE_VGA,
                null,
                DatabaseHelper.COL_VGA_ID + " = ?",
                new String[]{String.valueOf(id)},
                null, null, null
        );
        if (cursor.moveToFirst()) {
            VGAData vga = new VGAData(
                    cursor.getLong(cursor.getColumnIndex(DatabaseHelper.COL_VGA_ID)),
                    cursor.getString(cursor.getColumnIndex(DatabaseHelper.COL_VGA_NAME)),
                    Brand.valueOf(cursor.getString(cursor.getColumnIndex(DatabaseHelper.COL_VGA_BRAND))),
                    Condition.valueOf(cursor.getString(cursor.getColumnIndex(DatabaseHelper.COL_VGA_CONDITION))),
                    cursor.getString(cursor.getColumnIndex(DatabaseHelper.COL_VGA_DESCRIPTION)),
                    cursor.getDouble(cursor.getColumnIndex(DatabaseHelper.COL_VGA_PRICE)),
                    cursor.getInt(cursor.getColumnIndex(DatabaseHelper.COL_VGA_QUANTITY)),
                    cursor.getString(cursor.getColumnIndex(DatabaseHelper.COL_VGA_IMAGE_PATH)),
                    cursor.getLong(cursor.getColumnIndex(DatabaseHelper.COL_VGA_SELLER_ID))
            );
            cursor.close();
            return vga;
        }
        cursor.close();
        return null;
    }

    public List<VGAData> getVGABySeller(long sellerId) {
        Cursor cursor = db.query(
                DatabaseHelper.TABLE_VGA,
                null,
                DatabaseHelper.COL_VGA_SELLER_ID + " = ?",
                new String[]{String.valueOf(sellerId)},
                null, null, null
        );
        List<VGAData> vgaList = new ArrayList<>();
        while (cursor.moveToNext()) {
            vgaList.add(new VGAData(
                    cursor.getLong(cursor.getColumnIndex(DatabaseHelper.COL_VGA_ID)),
                    cursor.getString(cursor.getColumnIndex(DatabaseHelper.COL_VGA_NAME)),
                    Brand.valueOf(cursor.getString(cursor.getColumnIndex(DatabaseHelper.COL_VGA_BRAND))),
                    Condition.valueOf(cursor.getString(cursor.getColumnIndex(DatabaseHelper.COL_VGA_CONDITION))),
                    cursor.getString(cursor.getColumnIndex(DatabaseHelper.COL_VGA_DESCRIPTION)),
                    cursor.getDouble(cursor.getColumnIndex(DatabaseHelper.COL_VGA_PRICE)),
                    cursor.getInt(cursor.getColumnIndex(DatabaseHelper.COL_VGA_QUANTITY)),
                    cursor.getString(cursor.getColumnIndex(DatabaseHelper.COL_VGA_IMAGE_PATH)),
                    cursor.getLong(cursor.getColumnIndex(DatabaseHelper.COL_VGA_SELLER_ID))
            ));
        }
        cursor.close();
        return vgaList;
    }

    public List<VGAData> getVGAByBrand(Brand brand) {
        Cursor cursor = db.query(
                DatabaseHelper.TABLE_VGA,
                null,
                DatabaseHelper.COL_VGA_BRAND + " = ?",
                new String[]{brand.name()},
                null, null, null
        );
        List<VGAData> vgaList = new ArrayList<>();
        while (cursor.moveToNext()) {
            vgaList.add(new VGAData(
                    cursor.getLong(cursor.getColumnIndex(DatabaseHelper.COL_VGA_ID)),
                    cursor.getString(cursor.getColumnIndex(DatabaseHelper.COL_VGA_NAME)),
                    Brand.valueOf(cursor.getString(cursor.getColumnIndex(DatabaseHelper.COL_VGA_BRAND))),
                    Condition.valueOf(cursor.getString(cursor.getColumnIndex(DatabaseHelper.COL_VGA_CONDITION))),
                    cursor.getString(cursor.getColumnIndex(DatabaseHelper.COL_VGA_DESCRIPTION)),
                    cursor.getDouble(cursor.getColumnIndex(DatabaseHelper.COL_VGA_PRICE)),
                    cursor.getInt(cursor.getColumnIndex(DatabaseHelper.COL_VGA_QUANTITY)),
                    cursor.getString(cursor.getColumnIndex(DatabaseHelper.COL_VGA_IMAGE_PATH)),
                    cursor.getLong(cursor.getColumnIndex(DatabaseHelper.COL_VGA_SELLER_ID))
            ));
        }
        cursor.close();
        return vgaList;
    }

    public List<VGAData> searchVGAByName(String searchQuery) {
        Cursor cursor = db.query(
                DatabaseHelper.TABLE_VGA,
                null,
                DatabaseHelper.COL_VGA_NAME + " LIKE ?",
                new String[]{"%" + searchQuery + "%"},
                null, null, null
        );
        List<VGAData> vgaList = new ArrayList<>();
        while (cursor.moveToNext()) {
            vgaList.add(new VGAData(
                    cursor.getLong(cursor.getColumnIndex(DatabaseHelper.COL_VGA_ID)),
                    cursor.getString(cursor.getColumnIndex(DatabaseHelper.COL_VGA_NAME)),
                    Brand.valueOf(cursor.getString(cursor.getColumnIndex(DatabaseHelper.COL_VGA_BRAND))),
                    Condition.valueOf(cursor.getString(cursor.getColumnIndex(DatabaseHelper.COL_VGA_CONDITION))),
                    cursor.getString(cursor.getColumnIndex(DatabaseHelper.COL_VGA_DESCRIPTION)),
                    cursor.getDouble(cursor.getColumnIndex(DatabaseHelper.COL_VGA_PRICE)),
                    cursor.getInt(cursor.getColumnIndex(DatabaseHelper.COL_VGA_QUANTITY)),
                    cursor.getString(cursor.getColumnIndex(DatabaseHelper.COL_VGA_IMAGE_PATH)),
                    cursor.getLong(cursor.getColumnIndex(DatabaseHelper.COL_VGA_SELLER_ID))
            ));
        }
        cursor.close();
        return vgaList;
    }

    public List<VGAData> getAllVGASortedByPrice(boolean ascending) {
        String orderBy = DatabaseHelper.COL_VGA_PRICE + (ascending ? " ASC" : " DESC");
        Cursor cursor = db.query(DatabaseHelper.TABLE_VGA, null, null, null, null, null, orderBy);
        List<VGAData> vgaList = new ArrayList<>();
        while (cursor.moveToNext()) {
            vgaList.add(new VGAData(
                    cursor.getLong(cursor.getColumnIndex(DatabaseHelper.COL_VGA_ID)),
                    cursor.getString(cursor.getColumnIndex(DatabaseHelper.COL_VGA_NAME)),
                    Brand.valueOf(cursor.getString(cursor.getColumnIndex(DatabaseHelper.COL_VGA_BRAND))),
                    Condition.valueOf(cursor.getString(cursor.getColumnIndex(DatabaseHelper.COL_VGA_CONDITION))),
                    cursor.getString(cursor.getColumnIndex(DatabaseHelper.COL_VGA_DESCRIPTION)),
                    cursor.getDouble(cursor.getColumnIndex(DatabaseHelper.COL_VGA_PRICE)),
                    cursor.getInt(cursor.getColumnIndex(DatabaseHelper.COL_VGA_QUANTITY)),
                    cursor.getString(cursor.getColumnIndex(DatabaseHelper.COL_VGA_IMAGE_PATH)),
                    cursor.getLong(cursor.getColumnIndex(DatabaseHelper.COL_VGA_SELLER_ID))
            ));
        }
        cursor.close();
        return vgaList;
    }

    public long insertVGA(VGAData vga) {
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COL_VGA_NAME, vga.name);
        values.put(DatabaseHelper.COL_VGA_BRAND, vga.brand.name());
        values.put(DatabaseHelper.COL_VGA_CONDITION, vga.condition.name());
        values.put(DatabaseHelper.COL_VGA_DESCRIPTION, vga.description);
        values.put(DatabaseHelper.COL_VGA_PRICE, vga.price);
        values.put(DatabaseHelper.COL_VGA_QUANTITY, vga.quantity);
        values.put(DatabaseHelper.COL_VGA_IMAGE_PATH, vga.imagePath);
        values.put(DatabaseHelper.COL_VGA_SELLER_ID, vga.sellerId);
        return db.insert(DatabaseHelper.TABLE_VGA, null, values);
    }

    public int updateVGA(VGAData vga) {
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COL_VGA_NAME, vga.name);
        values.put(DatabaseHelper.COL_VGA_BRAND, vga.brand.name());
        values.put(DatabaseHelper.COL_VGA_CONDITION, vga.condition.name());
        values.put(DatabaseHelper.COL_VGA_DESCRIPTION, vga.description);
        values.put(DatabaseHelper.COL_VGA_PRICE, vga.price);
        values.put(DatabaseHelper.COL_VGA_QUANTITY, vga.quantity);
        values.put(DatabaseHelper.COL_VGA_IMAGE_PATH, vga.imagePath);
        return db.update(DatabaseHelper.TABLE_VGA, values,
                DatabaseHelper.COL_VGA_ID + " = ?",
                new String[]{String.valueOf(vga.id)});
    }

    public int deleteVGA(long id) {
        return db.delete(DatabaseHelper.TABLE_VGA,
                DatabaseHelper.COL_VGA_ID + " = ?",
                new String[]{String.valueOf(id)});
    }

    public void decreaseQuantity(long id, int amount) {
        VGAData vga = getVGAById(id);
        if (vga != null && vga.quantity >= amount) {
            int newQuantity = vga.quantity - amount;
            ContentValues values = new ContentValues();
            values.put(DatabaseHelper.COL_VGA_QUANTITY, newQuantity);
            db.update(DatabaseHelper.TABLE_VGA, values,
                    DatabaseHelper.COL_VGA_ID + " = ?",
                    new String[]{String.valueOf(id)});
        }
    }

    public static class VGAData {
        public long id;
        public String name;
        public Brand brand;
        public Condition condition;
        public String description;
        public double price;
        public int quantity;
        public String imagePath;
        public long sellerId;

        public VGAData(long id, String name, Brand brand, Condition condition,
                      String description, double price, int quantity,
                      String imagePath, long sellerId) {
            this.id = id;
            this.name = name;
            this.brand = brand;
            this.condition = condition;
            this.description = description;
            this.price = price;
            this.quantity = quantity;
            this.imagePath = imagePath;
            this.sellerId = sellerId;
        }

        public VGAData(String name, Brand brand, Condition condition,
                      String description, double price, int quantity,
                      String imagePath, long sellerId) {
            this(0, name, brand, condition, description, price, quantity, imagePath, sellerId);
        }
    }
}
