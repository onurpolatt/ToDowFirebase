package com.example.onurp.myapplication.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.onurp.myapplication.SectionHeaders;
import com.example.onurp.myapplication.Tasks;

import java.util.ArrayList;

/**
 * Created by onurp on 8.09.2017.
 */

public class dbManager{
    public dbHandler dbHelper;
    public Context mContext;
    private SQLiteDatabase database;

    public dbManager(Context c) {
        mContext = c;
    }
    public dbManager open() throws SQLException {
        dbHelper = new dbHandler(mContext);
        database = dbHelper.getWritableDatabase();
        return this;
    }

    public void close() {
        dbHelper.close();
    }

    public void insertSections(String header) {
        try{
            ContentValues contentValue = new ContentValues();
            contentValue.put(dbHandler.COLUMN_SECTION_NAME, header);
            database.insert(dbHandler.TABLE_SHEADERS, null, contentValue);
        }catch (SQLException e){
            e.printStackTrace();
        }
    }

    public void insertTasks(String header,String content,String implvl,String date,Integer sectionID) {
        try{
            ContentValues contentValue = new ContentValues();
            contentValue.put(dbHandler.COLUMN_HEADER, header);
            contentValue.put(dbHandler.COLUMN_CONTENT, content);
            contentValue.put(dbHandler.COLUMN_IMPLVL, implvl);
            contentValue.put(dbHandler.COLUMN_DATE, date);
            contentValue.put(dbHandler.COLUMN_SGROUPID, sectionID);
            database.insert(dbHandler.TABLE_TASKS, null, contentValue);
        }catch (SQLException e){
            e.printStackTrace();
        }
    }

    public ArrayList<Tasks> getAllTasks() {

        ArrayList<Tasks> taskList = new ArrayList<Tasks>();
        String query;

        query = "SELECT * FROM "  + dbHandler.TABLE_TASKS;


        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor res = db.rawQuery(query, null);
        res.moveToFirst();

        while (res.isAfterLast() == false) {

          taskList.add(new Tasks(res.getInt(0),res.getString(1),res.getString(2),
                    res.getString(3),res.getInt(4)));

            res.moveToNext();
        }
        return taskList;
    }

    public ArrayList<Tasks> getAllHeaders() {

        ArrayList<Tasks> sHeaders = new ArrayList<Tasks>();
        String query;

        query = "SELECT * FROM "  + dbHandler.TABLE_SHEADERS;

        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor res = db.rawQuery(query, null);
        res.moveToFirst();

        while (res.isAfterLast() == false) {

            sHeaders.add(new Tasks(res.getInt(0),res.getString(1)));

            res.moveToNext();
        }
        return sHeaders;
    }

    public int updateTasks(Tasks tasks) {

            SQLiteDatabase db = dbHelper.getWritableDatabase();

            ContentValues values = new ContentValues();
            values.put(dbHandler.COLUMN_CONTENT, tasks.getContent());
            values.put(dbHandler.COLUMN_IMPLVL, tasks.getImportanceLevel());
            values.put(dbHandler.COLUMN_DATE, tasks.getEndDate());


            return db.update(dbHandler.TABLE_TASKS, values, dbHandler.COLUMN_ID + " = ?",
                    new String[]{String.valueOf(tasks.getIdRow())});

    }

    public int updateSections(Tasks tasks) {

        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(dbHandler.COLUMN_SECTION_NAME, tasks.getSectionName());

        return db.update(dbHandler.TABLE_SHEADERS, values, dbHandler.COLUMN_HEADER_ID + " = ?",
                new String[]{String.valueOf(tasks.getIdSection())});

    }

    public void deleteTask(Tasks task) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.delete(dbHandler.TABLE_TASKS, dbHandler.COLUMN_ID + " = ?",
                new String[] { String.valueOf(task.getIdRow()) });
        db.close();
    }

    public int getTaskCount() {
        int taskCount=0;
        String query = "SELECT  * FROM " + dbHandler.TABLE_TASKS;

        try{
            SQLiteDatabase db = dbHelper.getReadableDatabase();
            Cursor cursor = db.rawQuery(query, null);
            taskCount = cursor.getCount();
            cursor.close();
        }catch (SQLException e){
            e.printStackTrace();
        }


        return taskCount;
    }
}
