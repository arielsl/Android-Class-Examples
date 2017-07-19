package com.sargent.mark.todolist;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;

import java.util.Calendar;

/**
 * Created by mark on 7/5/17.
 */

public class UpdateToDoFragment extends DialogFragment {

    private EditText toDo;
    private DatePicker dp;
    private Button add;
    private final String TAG = "updatetodofragment";
    private long id;
    //This spinner will hold the value for the category change
    private Spinner spinner;
    //This int will hold the value for the completion change
    private int completion;



    public UpdateToDoFragment(){}

    public static UpdateToDoFragment newInstance(int year, int month, int day, String descrpition, long id, int completed, String category) {
        UpdateToDoFragment f = new UpdateToDoFragment();

        // Supply num input as an argument.
        Bundle args = new Bundle();
        args.putInt("year", year);
        args.putInt("month", month);
        args.putInt("day", day);
        args.putLong("id", id);
        args.putString("description", descrpition);
        //Add the completion status to the argumetns to be read
        args.putInt("completed", completed);
        //Add the category to the arguments to be read
        args.putString("category", category);

        f.setArguments(args);

        return f;
    }

    //To have a way for the activity to get the data from the dialog
    public interface OnUpdateDialogCloseListener {
        //As always, we keep track of completion
        void closeUpdateDialog(int year, int month, int day, String description, long id, int completion, String category);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_to_do_adder, container, false);
        toDo = (EditText) view.findViewById(R.id.toDo);
        dp = (DatePicker) view.findViewById(R.id.datePicker);
        add = (Button) view.findViewById(R.id.add);
        spinner = (Spinner) view.findViewById(R.id.spinner_category);

        final int year = getArguments().getInt("year");
        final int month = getArguments().getInt("month");
        final int day = getArguments().getInt("day");
        id = getArguments().getLong("id");
        final String description = getArguments().getString("description");
        //get the category of the previous instance of the spinner
        final String category = getArguments().getString("category");
        //An int to keep track of completion when updating
        int completed = getArguments().getInt("completed");
        dp.updateDate(year, month, day);
        completion = completed;
        //Set the spinner to be on the previous category
        //the value you want the position for
        String myString = category;
        //cast to an ArrayAdapter
        ArrayAdapter myAdap = (ArrayAdapter) spinner.getAdapter();
        int spinnerPosition = myAdap.getPosition(myString);
        //set the default according to value
        spinner.setSelection(spinnerPosition);

        toDo.setText(description);

        /*
         On this if statement we are checking if the task is completed or not. If it is not completed:
         add the button to be marked as done. If it is completed then add a button to unmarked it.
         */
        Log.d(TAG, "COMPLETED IS: " + completed);

        if (completed == 0){
            Button bt = (Button)view.findViewById(R.id.done);
            bt.setText("Mark as done");
            LinearLayout.LayoutParams p = (LinearLayout.LayoutParams)bt.getLayoutParams();
            p.weight = 1;
            bt.setLayoutParams(p);
            bt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    UpdateToDoFragment.OnUpdateDialogCloseListener activity = (UpdateToDoFragment.OnUpdateDialogCloseListener) getActivity();
                    Log.d(TAG, "MARKING AS DONE: " + id);
                    //We change the completion status since we clicked on mark as done
                    //If we update the status, we pass the new completion status but we do not care about other changes
                    activity.closeUpdateDialog(year, month, day, description, id, 1, category);
                    UpdateToDoFragment.this.dismiss();
                }
            });
        }else if(completed == 1){
            Button bt = (Button)view.findViewById(R.id.done);
            bt.setText("Mark as not done");
            LinearLayout.LayoutParams p = (LinearLayout.LayoutParams)bt.getLayoutParams();
            p.weight = 1;
            bt.setLayoutParams(p);
            bt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    UpdateToDoFragment.OnUpdateDialogCloseListener activity = (UpdateToDoFragment.OnUpdateDialogCloseListener) getActivity();
                    Log.d(TAG, "MARKING AS NOT DONE: " + id);
                    //We change the completion status since we clicked on mark as not done
                    //If we update the status, we pass the new completion status but we do not care about other changes
                    activity.closeUpdateDialog(year, month, day, description, id, 0, category);
                    UpdateToDoFragment.this.dismiss();
                }
            });
        }


        add.setText("Update");
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UpdateToDoFragment.OnUpdateDialogCloseListener activity = (UpdateToDoFragment.OnUpdateDialogCloseListener) getActivity();
                Log.d(TAG, "UPDATING DATE: " + id);
                //If we update the date, we pass the previous completion value since we did not change anything
                activity.closeUpdateDialog(dp.getYear(), dp.getMonth(), dp.getDayOfMonth(), toDo.getText().toString(), id, completion, spinner.getSelectedItem().toString());
                UpdateToDoFragment.this.dismiss();
            }
        });

        return view;
    }
}