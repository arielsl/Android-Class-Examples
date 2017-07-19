package com.sargent.mark.todolist.data;

import android.provider.BaseColumns;

/**
 * Created by mark on 7/4/17.
 */

public class Contract {

    public static class TABLE_TODO implements BaseColumns{
        public static final String TABLE_NAME = "todoitems";

        public static final String COLUMN_NAME_DESCRIPTION = "description";
        public static final String COLUMN_NAME_DUE_DATE = "duedate";
        //Add a new column to keep track of completion status
        public static final String COLUMN_NAME_COMPLETED = "completed";
        //Add a new column to keep track of categories
        public static final String COLUMN_NAME_CATEGORY = "category";
    }
}
