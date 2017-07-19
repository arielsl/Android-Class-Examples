package com.sargent.mark.todolist;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;

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

    private int completion;



    public UpdateToDoFragment(){}

    public static UpdateToDoFragment newInstance(int year, int month, int day, String descrpition, long id, int completed) {
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

        f.setArguments(args);

        return f;
    }

    //To have a way for the activity to get the data from the dialog
    public interface OnUpdateDialogCloseListener {
        //As always, we keep track of completion
        void closeUpdateDialog(int year, int month, int day, String description, long id, int completion);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_to_do_adder, container, false);
        toDo = (EditText) view.findViewById(R.id.toDo);
        dp = (DatePicker) view.findViewById(R.id.datePicker);
        add = (Button) view.findViewById(R.id.add);

        int year = getArguments().getInt("year");
        int month = getArguments().getInt("month");
        int day = getArguments().getInt("day");
        id = getArguments().getLong("id");
        String description = getArguments().getString("description");
        //An int to keep track of completion when updating
        int completed = getArguments().getInt("completed");
        dp.updateDate(year, month, day);
        completion = completed;

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
                    //If we update the status, we pass the new completion status
                    activity.closeUpdateDialog(dp.getYear(), dp.getMonth(), dp.getDayOfMonth(), toDo.getText().toString(), id, 1);
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
                    //If we update the status, we pass the new completion status
                    activity.closeUpdateDialog(dp.getYear(), dp.getMonth(), dp.getDayOfMonth(), toDo.getText().toString(), id, 0);
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
                activity.closeUpdateDialog(dp.getYear(), dp.getMonth(), dp.getDayOfMonth(), toDo.getText().toString(), id, completion);
                UpdateToDoFragment.this.dismiss();
            }
        });

        return view;
    }
}