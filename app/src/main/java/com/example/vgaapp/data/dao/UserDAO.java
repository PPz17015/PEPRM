package com.example.vgaapp.data.dao;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.example.vgaapp.data.database.DatabaseHelper;
import com.example.vgaapp.data.model.UserRole;

public class UserDAO {
    private SQLiteDatabase db;

    public UserDAO(SQLiteDatabase db) {
        this.db = db;
    }

    public Long login(String email, String password) {
        Cursor cursor = db.query(
                DatabaseHelper.TABLE_USERS,
                null,
                DatabaseHelper.COL_USER_EMAIL + " = ? AND " + DatabaseHelper.COL_USER_PASSWORD + " = ?",
                new String[]{email, password},
                null,
                null,
                null
        );
        if (cursor.moveToFirst()) {
            long userId = cursor.getLong(cursor.getColumnIndex(DatabaseHelper.COL_USER_ID));
            cursor.close();
            return userId;
        } else {
            cursor.close();
            return null;
        }
    }

    public UserData getUserById(long id) {
        Cursor cursor = db.query(
                DatabaseHelper.TABLE_USERS,
                null,
                DatabaseHelper.COL_USER_ID + " = ?",
                new String[]{String.valueOf(id)},
                null,
                null,
                null
        );
        if (cursor.moveToFirst()) {
            UserData user = new UserData(
                    cursor.getLong(cursor.getColumnIndex(DatabaseHelper.COL_USER_ID)),
                    cursor.getString(cursor.getColumnIndex(DatabaseHelper.COL_USER_USERNAME)),
                    cursor.getString(cursor.getColumnIndex(DatabaseHelper.COL_USER_EMAIL)),
                    cursor.getString(cursor.getColumnIndex(DatabaseHelper.COL_USER_PASSWORD)),
                    UserRole.valueOf(cursor.getString(cursor.getColumnIndex(DatabaseHelper.COL_USER_ROLE)))
            );
            cursor.close();
            return user;
        } else {
            cursor.close();
            return null;
        }
    }

    public UserData getUserByEmail(String email) {
        Cursor cursor = db.query(
                DatabaseHelper.TABLE_USERS,
                null,
                DatabaseHelper.COL_USER_EMAIL + " = ?",
                new String[]{email},
                null,
                null,
                null
        );
        if (cursor.moveToFirst()) {
            UserData user = new UserData(
                    cursor.getLong(cursor.getColumnIndex(DatabaseHelper.COL_USER_ID)),
                    cursor.getString(cursor.getColumnIndex(DatabaseHelper.COL_USER_USERNAME)),
                    cursor.getString(cursor.getColumnIndex(DatabaseHelper.COL_USER_EMAIL)),
                    cursor.getString(cursor.getColumnIndex(DatabaseHelper.COL_USER_PASSWORD)),
                    UserRole.valueOf(cursor.getString(cursor.getColumnIndex(DatabaseHelper.COL_USER_ROLE)))
            );
            cursor.close();
            return user;
        } else {
            cursor.close();
            return null;
        }
    }

    public long register(String username, String email, String password, UserRole role) {
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COL_USER_USERNAME, username);
        values.put(DatabaseHelper.COL_USER_EMAIL, email);
        values.put(DatabaseHelper.COL_USER_PASSWORD, password);
        values.put(DatabaseHelper.COL_USER_ROLE, role.name());
        return db.insert(DatabaseHelper.TABLE_USERS, null, values);
    }

    public static class UserData {
        public long id;
        public String username;
        public String email;
        public String password;
        public UserRole role;

        public UserData(long id, String username, String email, String password, UserRole role) {
            this.id = id;
            this.username = username;
            this.email = email;
            this.password = password;
            this.role = role;
        }
    }
}
