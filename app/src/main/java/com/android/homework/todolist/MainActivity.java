package com.android.homework.todolist;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.android.homework.todolist.beans.Note;
import com.android.homework.todolist.beans.State;
import com.android.homework.todolist.db.TodoDbHelper;
import com.android.homework.todolist.operation.activity.DatabaseActivity;
import com.android.homework.todolist.operation.activity.DebugActivity;
import com.android.homework.todolist.operation.activity.SettingActivity;
import com.android.homework.todolist.ui.NoteListAdapter;
import com.android.homework.todolist.db.TodoContract.TodoEntry;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_CODE_ADD = 1002;
    private static final String TAG = "MainActivity";

    private TodoDbHelper dbHelper;
    private RecyclerView recyclerView;
    private NoteListAdapter notesAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dbHelper=new TodoDbHelper(this);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivityForResult(
                        new Intent(MainActivity.this, NoteActivity.class),
                        REQUEST_CODE_ADD);
            }
        });

        recyclerView = findViewById(R.id.list_todo);
        recyclerView.setLayoutManager(new LinearLayoutManager(this,
                LinearLayoutManager.VERTICAL, false));
        recyclerView.addItemDecoration(
                new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        notesAdapter = new NoteListAdapter(new NoteOperator() {
            @Override
            public void deleteNote(Note note) {
                MainActivity.this.deleteNote(note);
            }

            @Override
            public void updateNote(Note note) {
                MainActivity.this.updateNode(note);
            }
        });
        recyclerView.setAdapter(notesAdapter);


        notesAdapter.refresh(loadNotesFromDatabase());

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.action_settings:
                startActivity(new Intent(this, SettingActivity.class));
                return true;
            case R.id.action_debug:
                startActivity(new Intent(this, DebugActivity.class));
                return true;
            case R.id.action_database:
                startActivity(new Intent(this, DatabaseActivity.class));
                return true;
            default:
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_ADD
                && resultCode == Activity.RESULT_OK) {
            notesAdapter.refresh(loadNotesFromDatabase());
        }
    }

    private List<Note> loadNotesFromDatabase() {
        // TODO 从数据库中查询数据，并转换成 JavaBeans
        List<Note> allTodos = new ArrayList<Note>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(dbHelper.TABLE_NAME,
                new String[] { TodoEntry._ID,
                        TodoEntry.COLUMN_NAME_DATE,
                        TodoEntry.COLUMN_NAME_STATE,
                        TodoEntry.COLUMN_NAME_CONTENT,
                        TodoEntry.COLUMN_NAME_LEVEL},
                null,
                null,
                null,
                null,
                null);

        while (cursor.moveToNext()){
            Note bean = new Note(cursor.getLong(cursor.getColumnIndexOrThrow(TodoEntry._ID)));

            bean.setDate(new Date(Long.valueOf(cursor.getString(cursor.getColumnIndex(TodoEntry.COLUMN_NAME_DATE)))));

            String s=cursor.getString(cursor.getColumnIndexOrThrow(TodoEntry.COLUMN_NAME_STATE)).trim();

            Log.d("mmmmmmm","m"+Integer.valueOf(s)+"m");

            if(Integer.valueOf(s)==State.TODO.intValue)
                bean.setState(State.TODO);
            else
                bean.setState(State.DONE);

            bean.setContent(cursor.getString(cursor.getColumnIndex(TodoEntry.COLUMN_NAME_CONTENT)));
            bean.setLevel(cursor.getString(cursor.getColumnIndex(TodoEntry.COLUMN_NAME_LEVEL)).length());

            allTodos.add(bean);
        }
        cursor.close();
        db.close();

        //排序
        Collections.sort(allTodos, new Comparator<Note>() {
            @Override
            public int compare(Note o1, Note o2) {
                return Double.valueOf(o2.getLevel()).compareTo(Double.valueOf(o1.getLevel()));
            }
        });

        return allTodos;
    }

    private void deleteNote(Note note) {
        // TODO 删除数据
        // Gets the data repository in write mode
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        String selection = TodoEntry._ID + " LIKE ?";
        int result = db.delete(TodoEntry.TABLE_NAME, selection, new String[] {String.valueOf(note.id)} );
        db.close();

        if(result>0)
            Toast.makeText(MainActivity.this,
                    "No Error", Toast.LENGTH_SHORT).show();
        else
            Toast.makeText(MainActivity.this,
                    "Error", Toast.LENGTH_SHORT).show();

        //刷新数据
        notesAdapter.refresh(loadNotesFromDatabase());
    }

    private void updateNode(Note note) {
        // 更新数据
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        // New value for one column
        ContentValues values = new ContentValues();
        values.put(TodoEntry.COLUMN_NAME_STATE, note.getState().intValue);

        String selection = TodoEntry._ID + " LIKE ?";
        String[] selectionArgs = { String.valueOf(note.id) };

        int count = db.update(
                TodoEntry.TABLE_NAME,
                values,
                selection,
                selectionArgs);
        Log.i(TAG , "perform update data, result:" + count);

    }

}
