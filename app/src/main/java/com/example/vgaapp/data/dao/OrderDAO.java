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

    public List<OrderData> getAllOrders() {
        Cursor cursor = db.query(
                DatabaseHelper.TABLE_ORDERS,
                null,
                null,
                null,
                null, null,
                DatabaseHelper.COL_ORDER_DATE + " DESC"
        );
        List<OrderData> orders = new ArrayList<>();
        while (cursor.moveToNext()) {
            long orderId = cursor.getLong(cursor.getColumnIndex(DatabaseHelper.COL_ORDER_ID));
            List<OrderItemData> orderItems = getOrderItems(orderId);
            orders.add(new OrderData(
                    cursor.getLong(cursor.getColumnIndex(DatabaseHelper.COL_ORDER_ID)),
                    cursor.getLong(cursor.getColumnIndex(DatabaseHelper.COL_ORDER_USER_ID)),
                    cursor.getDouble(cursor.getColumnIndex(DatabaseHelper.COL_ORDER_TOTAL_AMOUNT)),
                    cursor.getLong(cursor.getColumnIndex(DatabaseHelper.COL_ORDER_DATE)),
                    orderItems
            ));
        }
        cursor.close();
        return orders;
    }

    public List<OrderData> getOrdersBySeller(long sellerId) {
        // Get all orders and filter by seller's products
        List<OrderData> allOrders = getAllOrders();
        List<OrderData> sellerOrders = new ArrayList<>();
        
        for (OrderData order : allOrders) {
            for (OrderItemData item : order.items) {
                VGADAO.VGAData vga = vgaDAO.getVGAById(item.vgaId);
                if (vga != null && vga.sellerId == sellerId) {
                    sellerOrders.add(order);
                    break;
                }
            }
        }
        return sellerOrders;
    }

    public double getTotalRevenue(long sellerId) {
        List<OrderData> orders = getOrdersBySeller(sellerId);
        double total = 0;
        for (OrderData order : orders) {
            for (OrderItemData item : order.items) {
                VGADAO.VGAData vga = vgaDAO.getVGAById(item.vgaId);
                if (vga != null && vga.sellerId == sellerId) {
                    total += item.price * item.quantity;
                }
            }
        }
        return total;
    }

    public double getRevenueByDay(long sellerId, long dayTimestamp) {
        List<OrderData> orders = getOrdersBySeller(sellerId);
        double total = 0;
        long dayStart = getStartOfDay(dayTimestamp);
        long dayEnd = getEndOfDay(dayTimestamp);
        
        for (OrderData order : orders) {
            if (order.date >= dayStart && order.date <= dayEnd) {
                for (OrderItemData item : order.items) {
                    VGADAO.VGAData vga = vgaDAO.getVGAById(item.vgaId);
                    if (vga != null && vga.sellerId == sellerId) {
                        total += item.price * item.quantity;
                    }
                }
            }
        }
        return total;
    }

    public double getRevenueByMonth(long sellerId, int year, int month) {
        List<OrderData> orders = getOrdersBySeller(sellerId);
        double total = 0;
        
        for (OrderData order : orders) {
            java.util.Calendar cal = java.util.Calendar.getInstance();
            cal.setTimeInMillis(order.date);
            if (cal.get(java.util.Calendar.YEAR) == year && 
                cal.get(java.util.Calendar.MONTH) == month - 1) {
                for (OrderItemData item : order.items) {
                    VGADAO.VGAData vga = vgaDAO.getVGAById(item.vgaId);
                    if (vga != null && vga.sellerId == sellerId) {
                        total += item.price * item.quantity;
                    }
                }
            }
        }
        return total;
    }

    public double getRevenueByYear(long sellerId, int year) {
        List<OrderData> orders = getOrdersBySeller(sellerId);
        double total = 0;
        
        for (OrderData order : orders) {
            java.util.Calendar cal = java.util.Calendar.getInstance();
            cal.setTimeInMillis(order.date);
            if (cal.get(java.util.Calendar.YEAR) == year) {
                for (OrderItemData item : order.items) {
                    VGADAO.VGAData vga = vgaDAO.getVGAById(item.vgaId);
                    if (vga != null && vga.sellerId == sellerId) {
                        total += item.price * item.quantity;
                    }
                }
            }
        }
        return total;
    }

    private long getStartOfDay(long timestamp) {
        java.util.Calendar cal = java.util.Calendar.getInstance();
        cal.setTimeInMillis(timestamp);
        cal.set(java.util.Calendar.HOUR_OF_DAY, 0);
        cal.set(java.util.Calendar.MINUTE, 0);
        cal.set(java.util.Calendar.SECOND, 0);
        cal.set(java.util.Calendar.MILLISECOND, 0);
        return cal.getTimeInMillis();
    }

    private long getEndOfDay(long timestamp) {
        java.util.Calendar cal = java.util.Calendar.getInstance();
        cal.setTimeInMillis(timestamp);
        cal.set(java.util.Calendar.HOUR_OF_DAY, 23);
        cal.set(java.util.Calendar.MINUTE, 59);
        cal.set(java.util.Calendar.SECOND, 59);
        cal.set(java.util.Calendar.MILLISECOND, 999);
        return cal.getTimeInMillis();
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
