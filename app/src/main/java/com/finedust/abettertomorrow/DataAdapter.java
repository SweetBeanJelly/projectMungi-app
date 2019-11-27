package com.finedust.abettertomorrow;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.Date;

/*
 *
 * Created by 진연경 on 2018.05
 *
*/

public class DataAdapter {
    DataHelper dataHelper;
    public DataAdapter(Context context) {
        dataHelper = new DataHelper(context);
    }

    public long insert(String description) {
        Date date = new Date();
        ContentValues values = new ContentValues();
        values.put(DataHelper.t_description, description);
        values.put(DataHelper.t_date, date.toLocaleString());
        SQLiteDatabase database = dataHelper.getWritableDatabase();
        long jyk = database.insert(DataHelper.t_table, null, values);
        return jyk;
    }

    /*
        날짜 , 내용
    */

    public void update(int position,String content) {
        SQLiteDatabase database = dataHelper.getWritableDatabase();
        database.execSQL("UPDATE "+ DataHelper.t_table+" SET "+ DataHelper.t_description+ "= "+"'"+content+"'"+" WHERE "+ DataHelper.t_ID+"="+position);
    }

    public void delete(int position) {
        SQLiteDatabase database = dataHelper.getWritableDatabase();
        database.execSQL("DELETE FROM "+ DataHelper.t_table+" WHERE "+ DataHelper.t_ID+"="+position);
    }

    public Cursor data() {
        SQLiteDatabase database = dataHelper.getWritableDatabase();
        Cursor cursor = database.rawQuery("SELECT * FROM "+ DataHelper.t_table, null);
        return cursor;
    }

    class DataHelper extends SQLiteOpenHelper {
        private static final String t_title = "MyDiary";
        private static final String t_table = "MyTable";
        private static final String t_ID = "_id";
        private static final String t_description = "description";
        private static final String t_date = "date";

        public DataHelper(Context context) {
            super(context, t_title, null, 1);
        }

        @Override
        public void onCreate(SQLiteDatabase arg) {
            // TODO METHOD
            String create = "CREATE TABLE "+t_table+"("+t_ID+" INTEGER PRIMARY KEY AUTOINCREMENT,"
                    +t_description+" TEXT,"+t_date+" TEXT);";
            arg.execSQL(create);
        }

        @Override
        public void onUpgrade(SQLiteDatabase arg0, int arg1, int arg2) {
            // TODO METHOD
            String drop = "DROP TABLE IF EXISTS "+t_table+";";
            arg0.execSQL(drop);
            onCreate(arg0);
        }
    }
}
