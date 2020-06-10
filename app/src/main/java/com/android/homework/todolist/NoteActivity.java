package com.android.homework.todolist;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.homework.todolist.beans.State;
import com.android.homework.todolist.db.TodoContract.TodoEntry;

import com.android.homework.todolist.db.TodoDbHelper;

import static java.lang.System.currentTimeMillis;

public class NoteActivity extends AppCompatActivity {

    private static final String TAG = "NoteActivity";
    private EditText editText;
    private Spinner levelSp;
    private Button addBtn;
    private TodoDbHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note);
        setTitle(R.string.take_a_note);

        editText = findViewById(R.id.edit_text);
        editText.setFocusable(true);
        editText.requestFocus();
        InputMethodManager inputManager = (InputMethodManager)
                getSystemService(Context.INPUT_METHOD_SERVICE);
        if (inputManager != null) {
            inputManager.showSoftInput(editText, 0);
        }

        levelSp=findViewById(R.id.sp_level);
        addBtn = findViewById(R.id.btn_add);

        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CharSequence content = editText.getText();
                CharSequence level=levelSp.getSelectedItem().toString();

                if (TextUtils.isEmpty(content)) {
                    Toast.makeText(NoteActivity.this,
                            "No content to add", Toast.LENGTH_SHORT).show();
                    return;
                }
                boolean succeed = saveNote2Database(content.toString().trim(),level.toString().trim());
                if (succeed) {
                    Toast.makeText(NoteActivity.this,
                            "Note added", Toast.LENGTH_SHORT).show();
                    setResult(Activity.RESULT_OK);
                } else {
                    Toast.makeText(NoteActivity.this,
                            "Error", Toast.LENGTH_SHORT).show();
                }
                finish();
            }
        });

        dbHelper=new TodoDbHelper(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private boolean saveNote2Database(String content,String level) {

        SQLiteDatabase db = dbHelper.getWritableDatabase();

        // Create a new map of values, where column names are the keys
        ContentValues values = new ContentValues();


        values.put(TodoEntry.COLUMN_NAME_DATE, currentTimeMillis());


        values.put(TodoEntry.COLUMN_NAME_STATE, State.TODO.intValue);
        values.put(TodoEntry.COLUMN_NAME_CONTENT, content);
        values.put(TodoEntry.COLUMN_NAME_LEVEL, level);

        // Insert the new row, returning the primary key value of the new row
        long newRowId = db.insert(TodoEntry.TABLE_NAME, null, values);
        Log.i(TAG, "perform add data, result:" + newRowId);

        if(newRowId!=-1)
            return true;
        else
            return false;
    }
}
