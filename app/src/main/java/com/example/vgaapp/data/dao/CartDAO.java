package com.example.vgaapp.data.dao;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.example.vgaapp.data.database.DatabaseHelper;

import java.util.ArrayList;
import java.util.List;

public class CartDAO {
    private SQLiteDatabase db;
    private VGADAO vgaDAO;

    public CartDAO(SQLiteDatabase db, VGADAO vgaDAO) {
        this.db = db;
        this.vgaDAO = vgaDAO;
    }

    public List<CartItemData> getCartItems(long userId) {
        Cursor cursor = db.query(
                DatabaseHelper.TABLE_CART,
                null,
                DatabaseHelper.COL_CART_USER_ID + " = ?",
                new String[]{String.valueOf(userId)},
                null, null, null
        );
        List<CartItemData> cartItems = new ArrayList<>();
        while (cursor.moveToNext()) {
            long vgaId = cursor.getLong(cursor.getColumnIndex(DatabaseHelper.COL_CART_VGA_ID));
            VGADAO.VGAData vga = vgaDAO.getVGAById(vgaId);
            cartItems.add(new CartItemData(
                    cursor.getLong(cursor.getColumnIndex(DatabaseHelper.COL_CART_ID)),
                    cursor.getLong(cursor.getColumnIndex(DatabaseHelper.COL_CART_USER_ID)),
                    vgaId,
                    cursor.getInt(cursor.getColumnIndex(DatabaseHelper.COL_CART_QUANTITY)),
                    vga
            ));
        }
        cursor.close();
        return cartItems;
    }

    public CartItemData getCartItem(long userId, long vgaId) {
        Cursor cursor = db.query(
                DatabaseHelper.TABLE_CART,
                null,
                DatabaseHelper.COL_CART_USER_ID + " = ? AND " + DatabaseHelper.COL_CART_VGA_ID + " = ?",
                new String[]{String.valueOf(userId), String.valueOf(vgaId)},
                null, null, null
        );
        if (cursor.moveToFirst()) {
            VGADAO.VGAData vga = vgaDAO.getVGAById(vgaId);
            CartItemData cartItem = new CartItemData(
                    cursor.getLong(cursor.getColumnIndex(DatabaseHelper.COL_CART_ID)),
                    cursor.getLong(cursor.getColumnIndex(DatabaseHelper.COL_CART_USER_ID)),
                    vgaId,
                    cursor.getInt(cursor.getColumnIndex(DatabaseHelper.COL_CART_QUANTITY)),
                    vga
            );
            cursor.close();
            return cartItem;
        }
        cursor.close();
        return null;
    }

    public void addToCart(long userId, long vgaId, int quantity) {
        CartItemData existingItem = getCartItem(userId, vgaId);
        if (existingItem != null) {
            ContentValues values = new ContentValues();
            values.put(DatabaseHelper.COL_CART_QUANTITY, existingItem.quantity + quantity);
            db.update(DatabaseHelper.TABLE_CART, values,
                    DatabaseHelper.COL_CART_ID + " = ?",
                    new String[]{String.valueOf(existingItem.id)});
        } else {
            ContentValues values = new ContentValues();
            values.put(DatabaseHelper.COL_CART_USER_ID, userId);
            values.put(DatabaseHelper.COL_CART_VGA_ID, vgaId);
            values.put(DatabaseHelper.COL_CART_QUANTITY, quantity);
            db.insert(DatabaseHelper.TABLE_CART, null, values);
        }
    }

    public void updateQuantity(long userId, long vgaId, int quantity) {
        CartItemData existingItem = getCartItem(userId, vgaId);
        if (existingItem != null) {
            ContentValues values = new ContentValues();
            values.put(DatabaseHelper.COL_CART_QUANTITY, quantity);
            db.update(DatabaseHelper.TABLE_CART, values,
                    DatabaseHelper.COL_CART_ID + " = ?",
                    new String[]{String.valueOf(existingItem.id)});
        }
    }

    public void removeFromCart(long userId, long vgaId) {
        db.delete(DatabaseHelper.TABLE_CART,
                DatabaseHelper.COL_CART_USER_ID + " = ? AND " + DatabaseHelper.COL_CART_VGA_ID + " = ?",
                new String[]{String.valueOf(userId), String.valueOf(vgaId)});
    }

    public void clearCart(long userId) {
        db.delete(DatabaseHelper.TABLE_CART,
                DatabaseHelper.COL_CART_USER_ID + " = ?",
                new String[]{String.valueOf(userId)});
    }

    public static class CartItemData {
        public long id;
        public long userId;
        public long vgaId;
        public int quantity;
        public VGADAO.VGAData vga;

        public CartItemData(long id, long userId, long vgaId, int quantity, VGADAO.VGAData vga) {
            this.id = id;
            this.userId = userId;
            this.vgaId = vgaId;
            this.quantity = quantity;
            this.vga = vga;
        }
    }
}
