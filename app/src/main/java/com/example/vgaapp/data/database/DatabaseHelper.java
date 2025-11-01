package com.example.vgaapp.data.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {
    
    private static final String DATABASE_NAME = "VGAApp.db";
    private static final int DATABASE_VERSION = 1;

    // Table Users
    public static final String TABLE_USERS = "users";
    public static final String COL_USER_ID = "id";
    public static final String COL_USER_USERNAME = "username";
    public static final String COL_USER_EMAIL = "email";
    public static final String COL_USER_PASSWORD = "password";
    public static final String COL_USER_ROLE = "role";

    // Table VGA
    public static final String TABLE_VGA = "vga";
    public static final String COL_VGA_ID = "id";
    public static final String COL_VGA_NAME = "name";
    public static final String COL_VGA_BRAND = "brand";
    public static final String COL_VGA_CONDITION = "condition";
    public static final String COL_VGA_DESCRIPTION = "description";
    public static final String COL_VGA_PRICE = "price";
    public static final String COL_VGA_QUANTITY = "quantity";
    public static final String COL_VGA_IMAGE_PATH = "image_path";
    public static final String COL_VGA_SELLER_ID = "seller_id";

    // Table Cart Items
    public static final String TABLE_CART = "cart_items";
    public static final String COL_CART_ID = "id";
    public static final String COL_CART_USER_ID = "user_id";
    public static final String COL_CART_VGA_ID = "vga_id";
    public static final String COL_CART_QUANTITY = "quantity";

    // Table Orders
    public static final String TABLE_ORDERS = "orders";
    public static final String COL_ORDER_ID = "id";
    public static final String COL_ORDER_USER_ID = "user_id";
    public static final String COL_ORDER_TOTAL_AMOUNT = "total_amount";
    public static final String COL_ORDER_DATE = "date";

    // Table Order Items
    public static final String TABLE_ORDER_ITEMS = "order_items";
    public static final String COL_ORDER_ITEM_ID = "id";
    public static final String COL_ORDER_ITEM_ORDER_ID = "order_id";
    public static final String COL_ORDER_ITEM_VGA_ID = "vga_id";
    public static final String COL_ORDER_ITEM_QUANTITY = "quantity";
    public static final String COL_ORDER_ITEM_PRICE = "price";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createUsersTable = "CREATE TABLE " + TABLE_USERS + " (" +
                COL_USER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_USER_USERNAME + " TEXT NOT NULL, " +
                COL_USER_EMAIL + " TEXT UNIQUE NOT NULL, " +
                COL_USER_PASSWORD + " TEXT NOT NULL, " +
                COL_USER_ROLE + " TEXT NOT NULL" +
                ")";

        String createVGATable = "CREATE TABLE " + TABLE_VGA + " (" +
                COL_VGA_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_VGA_NAME + " TEXT NOT NULL, " +
                COL_VGA_BRAND + " TEXT NOT NULL, " +
                COL_VGA_CONDITION + " TEXT NOT NULL, " +
                COL_VGA_DESCRIPTION + " TEXT, " +
                COL_VGA_PRICE + " REAL NOT NULL, " +
                COL_VGA_QUANTITY + " INTEGER NOT NULL, " +
                COL_VGA_IMAGE_PATH + " TEXT, " +
                COL_VGA_SELLER_ID + " INTEGER NOT NULL, " +
                "FOREIGN KEY (" + COL_VGA_SELLER_ID + ") REFERENCES " + TABLE_USERS + "(" + COL_USER_ID + ")" +
                ")";

        String createCartTable = "CREATE TABLE " + TABLE_CART + " (" +
                COL_CART_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_CART_USER_ID + " INTEGER NOT NULL, " +
                COL_CART_VGA_ID + " INTEGER NOT NULL, " +
                COL_CART_QUANTITY + " INTEGER NOT NULL, " +
                "FOREIGN KEY (" + COL_CART_USER_ID + ") REFERENCES " + TABLE_USERS + "(" + COL_USER_ID + "), " +
                "FOREIGN KEY (" + COL_CART_VGA_ID + ") REFERENCES " + TABLE_VGA + "(" + COL_VGA_ID + ")" +
                ")";

        String createOrdersTable = "CREATE TABLE " + TABLE_ORDERS + " (" +
                COL_ORDER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_ORDER_USER_ID + " INTEGER NOT NULL, " +
                COL_ORDER_TOTAL_AMOUNT + " REAL NOT NULL, " +
                COL_ORDER_DATE + " INTEGER NOT NULL, " +
                "FOREIGN KEY (" + COL_ORDER_USER_ID + ") REFERENCES " + TABLE_USERS + "(" + COL_USER_ID + ")" +
                ")";

        String createOrderItemsTable = "CREATE TABLE " + TABLE_ORDER_ITEMS + " (" +
                COL_ORDER_ITEM_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_ORDER_ITEM_ORDER_ID + " INTEGER NOT NULL, " +
                COL_ORDER_ITEM_VGA_ID + " INTEGER NOT NULL, " +
                COL_ORDER_ITEM_QUANTITY + " INTEGER NOT NULL, " +
                COL_ORDER_ITEM_PRICE + " REAL NOT NULL, " +
                "FOREIGN KEY (" + COL_ORDER_ITEM_ORDER_ID + ") REFERENCES " + TABLE_ORDERS + "(" + COL_ORDER_ID + "), " +
                "FOREIGN KEY (" + COL_ORDER_ITEM_VGA_ID + ") REFERENCES " + TABLE_VGA + "(" + COL_VGA_ID + ")" +
                ")";

        db.execSQL(createUsersTable);
        db.execSQL(createVGATable);
        db.execSQL(createCartTable);
        db.execSQL(createOrdersTable);
        db.execSQL(createOrderItemsTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ORDER_ITEMS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ORDERS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CART);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_VGA);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        onCreate(db);
    }
}
