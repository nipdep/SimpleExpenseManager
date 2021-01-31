package lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import lk.ac.mrt.cse.dbs.simpleexpensemanager.control.DatabaseHelper;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.AccountDAO;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.exception.InvalidAccountException;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.Account;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.ExpenseType;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.Transaction;

public class InDiskAccountDAO implements AccountDAO {

    private final DatabaseHelper dbHelper;

    public InDiskAccountDAO(Context context) {
        dbHelper = new DatabaseHelper(context);
    }

    @Override
    public List<String> getAccountNumbersList() {
        ArrayList<String> acc_nos = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String[] projection = {"accountNo"};
        Cursor cursor = db.query(
                "Account",   // The table to query
                projection,             // The array of columns to return (pass null to get all)
                null,              // The columns for the WHERE clause
                null,          // The values for the WHERE clause
                null,                   // don't group the rows
                null,                   // don't filter by row groups
                null               // The sort order
        );
        if (cursor.moveToFirst()) {
            do {
                acc_nos.add(cursor.getString(0));
            } while (cursor.moveToNext());
        }
        return acc_nos;
    }

    @Override
    public List<Account> getAccountsList() {
        //String accountNo, String bankName, String accountHolderName, double balance
        List<Account> accounts = new LinkedList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String[] projection = {
                "accountNo", "bank_name", "acc_holder_name","init_balance"
        };
        Cursor cursor = db.query(
                "Account",   // The table to query
                projection,             // The array of columns to return (pass null to get all)
                null,              // The columns for the WHERE clause
                null,          // The values for the WHERE clause
                null,                   // don't group the rows
                null,                   // don't filter by row groups
                null               // The sort order
        );
        if (cursor.moveToFirst()) {
            do {
                accounts.add(new Account(cursor.getString(0),cursor.getString(1), cursor.getString(2), Double.parseDouble(cursor.getString(3))));

            } while (cursor.moveToNext());
        }
        return accounts;
    }

    @Override
    public Account getAccount(String accountNo) throws InvalidAccountException {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String[] projection = {
                "accountNo", "bank_name", "acc_holder_name","init_balance"
        };
        String selection = "accountNo" + " = ?";
        String[] selectionArgs = { accountNo };
        Cursor cursor = db.query(
                "Account",   // The table to query
                projection,             // The array of columns to return (pass null to get all)
                selection,              // The columns for the WHERE clause
                selectionArgs,          // The values for the WHERE clause
                null,                   // don't group the rows
                null,                   // don't filter by row groups
                null               // The sort order
        );
        if (cursor.moveToFirst()) {
            return new Account(cursor.getString(0),cursor.getString(1), cursor.getString(2), Double.parseDouble(cursor.getString(3)));
        }
        String msg = "Account " + accountNo + " is invalid.";
        throw new InvalidAccountException(msg);
    }

    @Override
    public void addAccount(Account account) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        //Transaction transaction = new Transaction(date, accountNo, expenseType, amount);
        ContentValues acc = new ContentValues();
        acc.put("accountNo", account.getAccountNo());
        acc.put("init_balance", String.valueOf(account.getBalance()));
        acc.put("bank_name", account.getBankName());
        acc.put("acc_holder_name", account.getAccountHolderName());
        long newRowId = db.insert("Account", null, acc);
    }

    @Override
    public void removeAccount(String accountNo) throws InvalidAccountException {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        String selection = "accountNo" + " = ?";

        String[] selectionArgs = { accountNo };

        int deletedRows = db.delete("Account", selection, selectionArgs);
    }

    @Override
    public void updateBalance(String accountNo, ExpenseType expenseType, double amount) throws InvalidAccountException {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        Account account = this.getAccount(accountNo);
        double balance = account.getBalance();
        if (account == null) {
            String msg = "Account " + accountNo + " is invalid.";
            throw new InvalidAccountException(msg);
        }
        // specific implementation based on the transaction type
        switch (expenseType) {
            case EXPENSE:
                balance = balance - amount;
                break;
            case INCOME:
                balance = balance + amount;
                break;
        }
        ContentValues acc = new ContentValues();
        acc.put("accountNo", account.getAccountNo());
        acc.put("init_balance", String.valueOf(balance));
        acc.put("bank_name", account.getBankName());
        acc.put("acc_holder_name", account.getAccountHolderName());

        String selection = "accountNo" + " = ?";
        String[] selectionArgs = { accountNo };

        int count = db.update(
                "Account",
                acc,
                selection,
                selectionArgs);
    }
}
