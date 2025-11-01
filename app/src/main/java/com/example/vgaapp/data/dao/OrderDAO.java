package com.example.vgaapp.data.dao;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.example.vgaapp.data.database.DatabaseHelper;

import java.util.ArrayList;
import java.util.List;

public class OrderDAO {
    private SQLiteDatabase db;
    private VGADAO vgaDAO;

    public OrderDAO(SQLiteDatabase db, VGADAO vgaDAO) {
        this.db = db;
        this.vgaDAO = vgaDAO;
    }

    public long createOrder(long userId, List<OrderItemData> items) {
        double totalAmount = 0;
        for (OrderItemData item : items) {
            totalAmount += item.price * item.quantity;
        }

        ContentValues orderValues = new ContentValues();
        orderValues.put(DatabaseHelper.COL_ORDER_USER_ID, userId);
        orderValues.put(DatabaseHelper.COL_ORDER_TOTAL_AMOUNT, totalAmount);
        orderValues.put(DatabaseHelper.COL_ORDER_DATE, System.currentTimeMillis());
        long orderId = db.insert(DatabaseHelper.TABLE_ORDERS, null, orderValues);

        for (OrderItemData item : items) {
            ContentValues itemValues = new ContentValues();
            itemValues.put(DatabaseHelper.COL_ORDER_ITEM_ORDER_ID, orderId);
            itemValues.put(DatabaseHelper.COL_ORDER_ITEM_VGA_ID, item.vgaId);
            itemValues.put(DatabaseHelper.COL_ORDER_ITEM_QUANTITY, item.quantity);
            itemValues.put(DatabaseHelper.COL_ORDER_ITEM_PRICE, item.price);
            db.insert(DatabaseHelper.TABLE_ORDER_ITEMS, null, itemValues);
            vgaDAO.decreaseQuantity(item.vgaId, item.quantity);
        }

        return orderId;
    }

    public List<OrderData> getOrdersByUser(long userId) {
        Cursor cursor = db.query(
                DatabaseHelper.TABLE_ORDERS,
                null,
                DatabaseHelper.COL_ORDER_USER_ID + " = ?",
                new String[]{String.valueOf(userId)},
                null, null,
                DatabaseHelper.COL_ORDER_DATE + " DESC"
        );
        List<OrderData> orders = new ArrayList<>();
        while (cursor.moveToNext()) {
            long orderId = cursor.getLong(cursor.getColumnIndex(DatabaseHelper.COL_ORDER_ID));
            List<OrderItemData> orderItems = getOrderItems(orderId);
            orders.add(new OrderData(
                    orderId,
                    cursor.getLong(cursor.getColumnIndex(DatabaseHelper.COL_ORDER_USER_ID)),
                    cursor.getDouble(cursor.getColumnIndex(DatabaseHelper.COL_ORDER_TOTAL_AMOUNT)),
                    cursor.getLong(cursor.getColumnIndex(DatabaseHelper.COL_ORDER_DATE)),
                    orderItems
            ));
        }
        cursor.close();
        return orders;
    }

    private List<OrderItemData> getOrderItems(long orderId) {
        Cursor cursor = db.query(
                DatabaseHelper.TABLE_ORDER_ITEMS,
                null,
                DatabaseHelper.COL_ORDER_ITEM_ORDER_ID + " = ?",
                new String[]{String.valueOf(orderId)},
                null, null, null
        );
        List<OrderItemData> items = new ArrayList<>();
        while (cursor.moveToNext()) {
            items.add(new OrderItemData(
                    cursor.getLong(cursor.getColumnIndex(DatabaseHelper.COL_ORDER_ITEM_VGA_ID)),
                    cursor.getInt(cursor.getColumnIndex(DatabaseHelper.COL_ORDER_ITEM_QUANTITY)),
                    cursor.getDouble(cursor.getColumnIndex(DatabaseHelper.COL_ORDER_ITEM_PRICE))
            ));
        }
        cursor.close();
        return items;
    }

    public static class OrderData {
        public long id;
        public long userId;
        public double totalAmount;
        public long date;
        public List<OrderItemData> items;

        public OrderData(long id, long userId, double totalAmount, long date, List<OrderItemData> items) {
            this.id = id;
            this.userId = userId;
            this.totalAmount = totalAmount;
            this.date = date;
            this.items = items;
        }
    }

    public static class OrderItemData {
        public long vgaId;
        public int quantity;
        public double price;

        public OrderItemData(long vgaId, int quantity, double price) {
            this.vgaId = vgaId;
            this.quantity = quantity;
            this.price = price;
        }
    }
}
