package com.example.omanimedicien;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class DbSQLlite extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "OmaniMedicine.db";
    private static final int DATABASE_VERSION = 3;

    public static final String TABLE_USERS = "users";
    public static final String U_ID = "id";
    public static final String U_EMAIL = "email";
    public static final String U_PASSWORD = "password";

    public static final String TABLE_ADMINS = "admins";
    public static final String A_ID = "id";
    public static final String A_USERNAME = "username";
    public static final String A_PASSWORD = "password";

    public static final String TABLE_MEDICINES = "medicines";
    public static final String M_ID = "id";
    public static final String M_NAME = "name";
    public static final String M_IMAGE = "image";
    public static final String M_COMPOUND = "compound_name";
    public static final String M_DESCRIPTION = "description";
    public static final String M_INSTRUCTIONS = "usage_instructions";
    public static final String M_PRICE = "price";
    public static final String M_TEMP_RANGE = "temp_range";
    public static final String M_AGE_LIMIT = "age_limit";
    public static final String M_WARNINGS = "warnings";

    public static final String TABLE_ORDERS = "orders";
    public static final String O_ID = "id";
    public static final String O_USER_EMAIL = "user_email";
    public static final String O_MED_NAME = "medicine_name";
    public static final String O_QUANTITY = "quantity";
    public static final String O_ADDRESS = "address";
    public static final String O_PHONE = "phone";
    public static final String O_PAYMENT_MODE = "payment_mode";
    public static final String O_PAYMENT_STATUS = "payment_status";
    public static final String O_STATUS = "status";
    public static final String O_DATE = "order_date";

    public DbSQLlite(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_USERS_TABLE = "CREATE TABLE " + TABLE_USERS + " (" + U_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + U_EMAIL + " TEXT UNIQUE NOT NULL, " + U_PASSWORD + " TEXT NOT NULL);";
        String CREATE_ADMINS_TABLE = "CREATE TABLE " + TABLE_ADMINS + " (" + A_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + A_USERNAME + " TEXT UNIQUE NOT NULL, " + A_PASSWORD + " TEXT NOT NULL);";
        String CREATE_MEDICINES_TABLE = "CREATE TABLE " + TABLE_MEDICINES + " (" + M_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + M_NAME + " TEXT NOT NULL, " + M_IMAGE + " TEXT, " + M_COMPOUND + " TEXT, " + M_DESCRIPTION + " TEXT, " + M_INSTRUCTIONS + " TEXT, " + M_PRICE + " REAL, " + M_TEMP_RANGE + " TEXT, " + M_AGE_LIMIT + " TEXT, " + M_WARNINGS + " TEXT);";
        String CREATE_ORDERS_TABLE = "CREATE TABLE " + TABLE_ORDERS + " (" + O_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + O_USER_EMAIL + " TEXT, " + O_MED_NAME + " TEXT, " + O_QUANTITY + " INTEGER, " + O_ADDRESS + " TEXT, " + O_PHONE + " TEXT, " + O_PAYMENT_MODE + " TEXT, " + O_PAYMENT_STATUS + " TEXT, " + O_STATUS + " TEXT, " + O_DATE + " TEXT);";

        db.execSQL(CREATE_USERS_TABLE);
        db.execSQL(CREATE_ADMINS_TABLE);
        db.execSQL(CREATE_MEDICINES_TABLE);
        db.execSQL(CREATE_ORDERS_TABLE);

        ContentValues cv = new ContentValues();
        cv.put(A_USERNAME, "admin");
        cv.put(A_PASSWORD, "admin123");
        db.insert(TABLE_ADMINS, null, cv);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ADMINS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_MEDICINES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ORDERS);
        onCreate(db);
    }

    // USER METHODS
    public User getUserByEmail(String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_USERS + " WHERE " + U_EMAIL + "=?", new String[]{email});
        if (cursor != null && cursor.moveToFirst()) {
            User user = new User(cursor.getInt(0), cursor.getString(1), cursor.getString(2));
            cursor.close();
            return user;
        }
        return null;
    }

    public boolean updateUserProfile(String oldEmail, String newEmail, String newPassword) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(U_EMAIL, newEmail);
        cv.put(U_PASSWORD, newPassword);
        return db.update(TABLE_USERS, cv, U_EMAIL + "=?", new String[]{oldEmail}) > 0;
    }

    public int getUsersCount() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_USERS, null);
        int count = cursor.getCount();
        cursor.close();
        return count;
    }

    public List<User> getAllUsers() {
        List<User> userList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_USERS, null);
        if (cursor.moveToFirst()) {
            do {
                userList.add(new User(cursor.getInt(0), cursor.getString(1), cursor.getString(2)));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return userList;
    }

    public User getUserById(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_USERS + " WHERE " + U_ID + "=?", new String[]{String.valueOf(id)});
        if (cursor != null && cursor.moveToFirst()) {
            User user = new User(cursor.getInt(0), cursor.getString(1), cursor.getString(2));
            cursor.close();
            return user;
        }
        return null;
    }

    public boolean updateUser(int id, String email) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(U_EMAIL, email);
        return db.update(TABLE_USERS, cv, U_ID + "=?", new String[]{String.valueOf(id)}) > 0;
    }

    public boolean updatePassword(int id, String newPassword) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(U_PASSWORD, newPassword);
        return db.update(TABLE_USERS, cv, U_ID + "=?", new String[]{String.valueOf(id)}) > 0;
    }

    public void deleteUser(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_USERS, U_ID + "=?", new String[]{String.valueOf(id)});
    }

    public boolean addUser(String email, String password) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(U_EMAIL, email);
        cv.put(U_PASSWORD, password);
        long result = db.insert(TABLE_USERS, null, cv);
        return result != -1;
    }

    public boolean checkUserLogin(String email, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_USERS + " WHERE " + U_EMAIL + "=? AND " + U_PASSWORD + "=?", new String[]{email, password});
        boolean exists = cursor.getCount() > 0;
        cursor.close();
        return exists;
    }

    public boolean checkAdminLogin(String username, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_ADMINS + " WHERE " + A_USERNAME + "=? AND " + A_PASSWORD + "=?", new String[]{username, password});
        boolean exists = cursor.getCount() > 0;
        cursor.close();
        return exists;
    }

    public boolean checkEmailExists(String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_USERS + " WHERE " + U_EMAIL + "=?", new String[]{email});
        boolean exists = cursor.getCount() > 0;
        cursor.close();
        return exists;
    }

    // MEDICINE METHODS
    public int getMedicinesCount() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_MEDICINES, null);
        int count = cursor.getCount();
        cursor.close();
        return count;
    }

    public boolean addMedicine(String name, String image, String compound, String desc, String instructions, double price, String temp, String age, String warnings) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(M_NAME, name);
        cv.put(M_IMAGE, image);
        cv.put(M_COMPOUND, compound);
        cv.put(M_DESCRIPTION, desc);
        cv.put(M_INSTRUCTIONS, instructions);
        cv.put(M_PRICE, price);
        cv.put(M_TEMP_RANGE, temp);
        cv.put(M_AGE_LIMIT, age);
        cv.put(M_WARNINGS, warnings);
        long result = db.insert(TABLE_MEDICINES, null, cv);
        return result != -1;
    }

    public List<Medicine> getAllMedicines() {
        List<Medicine> medList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_MEDICINES, null);
        if (cursor.moveToFirst()) {
            do {
                medList.add(new Medicine(cursor.getInt(0), cursor.getString(1), cursor.getString(2), cursor.getString(3), cursor.getString(4), cursor.getString(5), cursor.getDouble(6), cursor.getString(7), cursor.getString(8), cursor.getString(9)));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return medList;
    }

    public List<Medicine> searchMedicines(String query) {
        List<Medicine> medList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_MEDICINES + " WHERE " + M_NAME + " LIKE ?", new String[]{"%" + query + "%"});
        if (cursor.moveToFirst()) {
            do {
                medList.add(new Medicine(cursor.getInt(0), cursor.getString(1), cursor.getString(2), cursor.getString(3), cursor.getString(4), cursor.getString(5), cursor.getDouble(6), cursor.getString(7), cursor.getString(8), cursor.getString(9)));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return medList;
    }

    public Medicine getMedicineById(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_MEDICINES + " WHERE " + M_ID + "=?", new String[]{String.valueOf(id)});
        if (cursor != null && cursor.moveToFirst()) {
            Medicine med = new Medicine(cursor.getInt(0), cursor.getString(1), cursor.getString(2), cursor.getString(3), cursor.getString(4), cursor.getString(5), cursor.getDouble(6), cursor.getString(7), cursor.getString(8), cursor.getString(9));
            cursor.close();
            return med;
        }
        return null;
    }

    public boolean updateMedicine(int id, String name, String image, String compound, String desc, String instructions, double price, String temp, String age, String warnings) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(M_NAME, name);
        cv.put(M_IMAGE, image);
        cv.put(M_COMPOUND, compound);
        cv.put(M_DESCRIPTION, desc);
        cv.put(M_INSTRUCTIONS, instructions);
        cv.put(M_PRICE, price);
        cv.put(M_TEMP_RANGE, temp);
        cv.put(M_AGE_LIMIT, age);
        cv.put(M_WARNINGS, warnings);
        return db.update(TABLE_MEDICINES, cv, M_ID + "=?", new String[]{String.valueOf(id)}) > 0;
    }

    public void deleteMedicine(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_MEDICINES, M_ID + "=?", new String[]{String.valueOf(id)});
    }

    // ORDER METHODS
    public int getOrdersCountByStatus(String status) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_ORDERS + " WHERE " + O_STATUS + "=?", new String[]{status});
        int count = cursor.getCount();
        cursor.close();
        return count;
    }

    public double getCompletedOrdersTotalAmount() {
        double total = 0;
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT SUM(o." + O_QUANTITY + " * m." + M_PRICE + ") FROM " + TABLE_ORDERS + " o " +
                       "JOIN " + TABLE_MEDICINES + " m ON o." + O_MED_NAME + " = m." + M_NAME + " " +
                       "WHERE o." + O_STATUS + " = 'Completed'";
        Cursor cursor = db.rawQuery(query, null);
        if (cursor.moveToFirst()) {
            total = cursor.getDouble(0);
        }
        cursor.close();
        return total;
    }

    public boolean addOrder(String userEmail, String medName, int quantity, String address, String phone, String payMode, String payStatus, String status, String date) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(O_USER_EMAIL, userEmail);
        cv.put(O_MED_NAME, medName);
        cv.put(O_QUANTITY, quantity);
        cv.put(O_ADDRESS, address);
        cv.put(O_PHONE, phone);
        cv.put(O_PAYMENT_MODE, payMode);
        cv.put(O_PAYMENT_STATUS, payStatus);
        cv.put(O_STATUS, status);
        cv.put(O_DATE, date);
        long result = db.insert(TABLE_ORDERS, null, cv);
        return result != -1;
    }

    public List<Order> getOrdersByUser(String email) {
        List<Order> orderList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_ORDERS + " WHERE " + O_USER_EMAIL + "=?", new String[]{email});
        if (cursor.moveToFirst()) {
            do {
                orderList.add(new Order(cursor.getInt(0), cursor.getString(1), cursor.getString(2), cursor.getInt(3), cursor.getString(4), cursor.getString(5), cursor.getString(6), cursor.getString(7), cursor.getString(8), cursor.getString(9)));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return orderList;
    }

    public List<Order> getAllOrders() {
        List<Order> orderList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_ORDERS, null);
        if (cursor.moveToFirst()) {
            do {
                orderList.add(new Order(cursor.getInt(0), cursor.getString(1), cursor.getString(2), cursor.getInt(3), cursor.getString(4), cursor.getString(5), cursor.getString(6), cursor.getString(7), cursor.getString(8), cursor.getString(9)));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return orderList;
    }

    public boolean updateOrderStatus(int orderId, String status) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(O_STATUS, status);
        return db.update(TABLE_ORDERS, cv, O_ID + "=?", new String[]{String.valueOf(orderId)}) > 0;
    }
}
