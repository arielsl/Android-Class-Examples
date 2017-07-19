package com.sargent.mark.todolist;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Spinner;


import com.sargent.mark.todolist.data.Contract;
import com.sargent.mark.todolist.data.DBHelper;

public class MainActivity extends AppCompatActivity implements AddToDoFragment.OnDialogCloseListener, UpdateToDoFragment.OnUpdateDialogCloseListener{

    private RecyclerView rv;
    private FloatingActionButton button;
    private DBHelper helper;
    private Cursor cursor;
    private SQLiteDatabase db;
    ToDoListAdapter adapter;
    //The button for the filtering options
    private Button filter;
    //The spinner to read from
    private Spinner spinner;
    private final String TAG = "mainactivity";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d(TAG, "oncreate called in main activity");
        button = (FloatingActionButton) findViewById(R.id.addToDo);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fm = getSupportFragmentManager();
                AddToDoFragment frag = new AddToDoFragment();
                frag.show(fm, "addtodofragment");
            }
        });

        rv = (RecyclerView) findViewById(R.id.recyclerView);
        rv.setLayoutManager(new LinearLayoutManager(this));
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (db != null) db.close();
        if (cursor != null) cursor.close();
    }

    @Override
    protected void onStart() {
        super.onStart();

        helper = new DBHelper(this);
        db = helper.getWritableDatabase();
        cursor = getAllItems(db);
        //Set the spinner to read the option selected
        spinner = (Spinner) findViewById(R.id.spinner);
        //Set the button for filtering by getting it from the view
        filter = (Button) findViewById(R.id.filter_button);
        filter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Get the text from the spinner to call a query and filter it
                String category = spinner.getSelectedItem().toString();
                //Use switch to know how to filter depending on the option
                //Each option calls a getby method and passes the parameter for the query
                //After, each statement recalls the adapter to show changes
                Log.d(TAG, "INSIDE BUTTON FILTER CATEGORY IS: " + category);
                switch (category){
                    case "All": cursor = getAllItems(db);
                        setAdapter();
                        break;
                    case "Completed": cursor = getByCompletion(db, 1);
                        setAdapter();
                        break;
                    case "Not Completed": cursor = getByCompletion(db, 0);
                        setAdapter();
                        break;
                    case "School": cursor = getByCategory(db, "School");
                        setAdapter();
                        break;
                    case "Work": cursor = getByCategory(db, "Work");
                        setAdapter();
                        break;
                    case "Family": cursor = getByCategory(db, "Family");
                        setAdapter();
                        break;
                    case "Leisure": cursor = getByCategory(db, "Leisure");
                        setAdapter();
                        break;
                    default: cursor = getAllItems(db);
                        setAdapter();
                        break;
                }
            }
        });

        //The adapter was here, call it when onStart executes
        setAdapter();

        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {

            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int swipeDir) {
                long id = (long) viewHolder.itemView.getTag();
                Log.d(TAG, "passing id: " + id);
                removeToDo(db, id);
                adapter.swapCursor(getAllItems(db));
            }
        }).attachToRecyclerView(rv);
    }

    //Set the adapter in its own method to call it from the switch statements
    //With the new cursors
    public void setAdapter(){
        //Add the completion status to be added to the item selected
        adapter = new ToDoListAdapter(cursor, new ToDoListAdapter.ItemClickListener() {

            @Override
            public void onItemClick(int pos, String description, String duedate, long id, int completion, String category) {
                Log.d(TAG, "item click id: " + id);
                String[] dateInfo = duedate.split("-");
                int year = Integer.parseInt(dateInfo[0].replaceAll("\\s",""));
                int month = Integer.parseInt(dateInfo[1].replaceAll("\\s",""));
                int day = Integer.parseInt(dateInfo[2].replaceAll("\\s",""));

                FragmentManager fm = getSupportFragmentManager();

                UpdateToDoFragment frag = UpdateToDoFragment.newInstance(year, month, day, description, id, completion, category);
                frag.show(fm, "updatetodofragment");
            }
        });

        rv.setAdapter(adapter);
    }


    @Override
    public void closeDialog(int year, int month, int day, String description, String category) {
        addToDo(db, description, formatDate(year, month, day), category);
        cursor = getAllItems(db);
        adapter.swapCursor(cursor);
    }

    public String formatDate(int year, int month, int day) {
        return String.format("%04d-%02d-%02d", year, month + 1, day);
    }


    //A new query with a WHERE to filter by completion, depending on the spinner
    private Cursor getByCategory(SQLiteDatabase db, String category) {
        String where = Contract.TABLE_TODO.COLUMN_NAME_CATEGORY + "=?";
        String[] args = {category};
        return db.query(
                Contract.TABLE_TODO.TABLE_NAME,
                null,
                where,
                args,
                null,
                null,
                Contract.TABLE_TODO.COLUMN_NAME_DUE_DATE
        );
    }

    //A new query with a WHERE to filter by completion, depending on the option 1 or 0
    private Cursor getByCompletion(SQLiteDatabase db, int completion) {
        String where = Contract.TABLE_TODO.COLUMN_NAME_COMPLETED + "=?";
        String[] args = {Integer.toString(completion)};
        return db.query(
                Contract.TABLE_TODO.TABLE_NAME,
                null,
                where,
                args,
                null,
                null,
                Contract.TABLE_TODO.COLUMN_NAME_DUE_DATE
        );
    }

    private Cursor getAllItems(SQLiteDatabase db) {
        return db.query(
                Contract.TABLE_TODO.TABLE_NAME,
                null,
                null,
                null,
                null,
                null,
                Contract.TABLE_TODO.COLUMN_NAME_DUE_DATE
        );
    }

    //When we add an item to the db, we pass 0 as we just created the task and thus it is not completed
    //We also add a put who passes that 0 with the column name
    //The category is the spinner text to be sent to the db
    private long addToDo(SQLiteDatabase db, String description, String duedate, String category) {
        ContentValues cv = new ContentValues();
        cv.put(Contract.TABLE_TODO.COLUMN_NAME_DESCRIPTION, description);
        cv.put(Contract.TABLE_TODO.COLUMN_NAME_DUE_DATE, duedate);
        cv.put(Contract.TABLE_TODO.COLUMN_NAME_COMPLETED, 0);
        cv.put(Contract.TABLE_TODO.COLUMN_NAME_CATEGORY, category);
        return db.insert(Contract.TABLE_TODO.TABLE_NAME, null, cv);
    }

    private boolean removeToDo(SQLiteDatabase db, long id) {
        Log.d(TAG, "deleting id: " + id);
        return db.delete(Contract.TABLE_TODO.TABLE_NAME, Contract.TABLE_TODO._ID + "=" + id, null) > 0;
    }


    //The method now takes and int to update the completion status and a string for category when clicking the button on the dialog
    private int updateToDo(SQLiteDatabase db, int year, int month, int day, String description, long id, int completion, String category){

        String duedate = formatDate(year, month - 1, day);

        ContentValues cv = new ContentValues();
        cv.put(Contract.TABLE_TODO.COLUMN_NAME_DESCRIPTION, description);
        cv.put(Contract.TABLE_TODO.COLUMN_NAME_DUE_DATE, duedate);
        //We use the column named for completion statuses and pass the new value
        cv.put(Contract.TABLE_TODO.COLUMN_NAME_COMPLETED, completion);
        //We used the column named for category and pass the new value
        cv.put(Contract.TABLE_TODO.COLUMN_NAME_CATEGORY, category);

        return db.update(Contract.TABLE_TODO.TABLE_NAME, cv, Contract.TABLE_TODO._ID + "=" + id, null);
    }

    //We now take an int to update status and a string for category we pass it to the db
    @Override
    public void closeUpdateDialog(int year, int month, int day, String description, long id, int completion, String category) {
        updateToDo(db, year, month, day, description, id, completion, category);
        adapter.swapCursor(getAllItems(db));
    }
}
