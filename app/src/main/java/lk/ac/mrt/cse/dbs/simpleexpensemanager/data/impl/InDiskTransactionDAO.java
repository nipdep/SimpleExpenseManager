package lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.UserHandle;
import android.provider.BaseColumns;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.format.DateFormat;
import android.view.Display;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import lk.ac.mrt.cse.dbs.simpleexpensemanager.control.DatabaseHelper;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.TransactionDAO;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.ExpenseType;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.Transaction;

public class InDiskTransactionDAO implements TransactionDAO {

    private final DatabaseHelper dbHelper;

    public InDiskTransactionDAO(Context context) {

        dbHelper = new DatabaseHelper(context);
    }


    @Override
    public void logTransaction(Date date, String accountNo, ExpenseType expenseType, double amount) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        //Transaction transaction = new Transaction(date, accountNo, expenseType, amount);
        ContentValues transaction = new ContentValues();
        transaction.put("account_number", accountNo);
        transaction.put("date", String.valueOf(date));
        transaction.put("expense_type", String.valueOf(expenseType));
        transaction.put("amount", amount);
        long newRowId = db.insert("Transactions", null, transaction);
    }

    @Override
    public List<Transaction> getAllTransactionLogs() {
        //Date date, String accountNo, ExpenseType expenseType, double amount
        List<Transaction> transactions = new LinkedList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String[] projection = {
                "date","account_number",  "expense_type", "amount"
        };
        Cursor cursor = db.query(
                "Transactions",   // The table to query
                projection,             // The array of columns to return (pass null to get all)
                null,              // The columns for the WHERE clause
                null,          // The values for the WHERE clause
                null,                   // don't group the rows
                null,                   // don't filter by row groups
                null               // The sort order
        );
        if (cursor.moveToFirst()) {
            do {
                SimpleDateFormat formatter = new SimpleDateFormat("m-d-yyyy", Locale.ENGLISH);
                Date date = new Date();
                try {
                    date =  formatter.parse(cursor.getString(0));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                transactions.add(new Transaction(date, cursor.getString(1),ExpenseType.valueOf(cursor.getString(2).toUpperCase()), Double.parseDouble(cursor.getString(3))));

            } while (cursor.moveToNext());
        }
        return transactions;
    }

    @Override
    public List<Transaction> getPaginatedTransactionLogs(int limit) {
        List<Transaction> transactions = this.getAllTransactionLogs();
        int size = transactions.size();
        if (size <= limit) {
            return transactions;
        }
        // return the last <code>limit</code> number of transaction logs
        return transactions.subList(size - limit, size);
    }
}
