package com.android.homework.todolist.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import static com.android.homework.todolist.db.TodoContract.SQL_CREATE_TODOS;

public class TodoDbHelper extends SQLiteOpenHelper {

    public String TABLE_NAME = "todo";
    // TODO 定义数据库名、版本；创建数据库

    public TodoDbHelper(Context context) {
        super(context, "todo.db", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_TODOS);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
