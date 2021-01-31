package lk.ac.mrt.cse.dbs.simpleexpensemanager.control;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;


public class DatabaseHelper extends SQLiteOpenHelper {
    public  static final String DB_NAME = "180127U";
    public  static final String TABLE_1 = "Account";
    public  static final String TABLE_2 = "Transactions";



    public DatabaseHelper(Context context) {
        super(context, DB_NAME, null, 11);
        SQLiteDatabase db = this.getWritableDatabase();
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String ddl_q_1 = "create table "+TABLE_1+" (accountNo TEXT(50) PRIMARY KEY,bank_name TEXT(50),acc_holder_name TEXT(50),init_balance NUMERIC) ";
        String ddl_q_2 = " create table "+TABLE_2+" (account_number TEXT(50) ,date date, expense_type TEXT(20),amount REAL,FOREIGN KEY (account_number) REFERENCES "+TABLE_1+"(accountNo))";
        sqLiteDatabase.execSQL(ddl_q_1);
        sqLiteDatabase.execSQL(ddl_q_2);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        String delete_q = "DROP TABLE IF EXISTS "+TABLE_1;
        String delete_q_2 ="DROP TABLE IF EXISTS "+TABLE_2;
        sqLiteDatabase.execSQL(delete_q);
        sqLiteDatabase.execSQL(delete_q_2);
        onCreate(sqLiteDatabase);
    }




}
