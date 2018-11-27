package com.sponge.baebot;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.core.Tag;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;

import static com.sponge.baebot.R.id.btnTask;

public class TaskActivity extends AppCompatActivity
        implements View.OnClickListener {
    private Button selectDate, selectTime, createTask;
    private EditText date, time, title, description;
    private DatePickerDialog datePickerDialog;
    private TimePickerDialog timePickerDialog;
    private int year, month, dayOfMonth, hour, minute;
    private Calendar calendar = Calendar.getInstance();
    private static FirebaseDatabase database = FirebaseDatabase.getInstance(); // Firebase databse
    private static DatabaseReference mDatabase = database.getReference();
    private String userId;
    private User myUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task);

        createTask = findViewById(R.id.btnTask);
        createTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.w("button", "create task button clicked!");
                String strTitle = title.getText().toString();
                String strDescription = description.getText().toString();
                String strDate = "";
                String strTime = "";
                if (selectDate != null && selectTime != null) {
                    strDate = selectDate.getText().toString();
                    strTime = selectTime.getText().toString();
                }

                if (strDate.length() != 0 && strTime.length() != 0 && strTitle.length() != 0) {


                    // xxxx-xx-xx or xxxx-x-xx or xxxx-xx-x or xxxx-x-x
                        year = Integer.parseInt(strDate.substring(0, 4));
                        String strMonth = "";
                        int i = 5;
                        for (i = 5; i < strDate.length(); ++i) {
                            if (strDate.charAt(i) != '-') {
                                strMonth += strDate.charAt(i);
                            } else {
                                break;
                            }
                        }
                        month = Integer.parseInt(strMonth);
                        String strDay = "";
                        for (int j = i + 1; j < strDate.length(); ++j) {
                            if (strDate.charAt(j) != '-') {
                                strDay += strDate.charAt(j);
                            } else {
                                break;
                            }
                        }
                        dayOfMonth = Integer.parseInt(strDay);

                    // xx:xx or x:xx or x:x or xx:x
                        int idx;
                        String strHour = "";
                        for (idx = 0; idx < strTime.length(); ++idx) {
                            if (strTime.charAt(idx) != ':') {
                                strHour += strTime.charAt(idx);
                            } else {
                                break;
                            }
                        }
                        hour = Integer.parseInt(strHour);
                        minute = Integer.parseInt(strTime.substring(idx+1));


                    Task task = new Task(strTitle, strDescription, year, month, dayOfMonth, hour, minute);

                    if (userId != null) {
                        //myUser.updateIdCount();
                        long currentTime = calendar.getTimeInMillis();
                        String strCurrentTime = Long.toString(currentTime);
                        mDatabase.child("task").child(userId).child(strCurrentTime).setValue(task);
                        Log.w("add to db", "success");
                    } else {
                        Log.w("dataBase error", "No such User");
                    }
                }
            }
        });

        title = findViewById(R.id.title_input);
        description = findViewById(R.id.description);

        selectDate = findViewById(R.id.btnDate);
        selectDate.setOnClickListener(this);

        selectTime = findViewById(R.id.btnTime);
        selectTime.setOnClickListener(this);
        String temp1 = selectTime.getText().toString();
        selectTime.setText(temp1);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            userId = extras.getString("userId");
            //The key argument here must match that used in the other activity
        }
        Intent intent = getIntent();
        myUser = intent.getParcelableExtra("user");
    }


    @Override
    public void onClick(View v){
        switch (v.getId()) {
            case R.id.btnDate:
                Log.w("button", "select date button clicked!");
                year = calendar.get(Calendar.YEAR);
                month = calendar.get(Calendar.MONTH);
                dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
                datePickerDialog = new DatePickerDialog(TaskActivity.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                                selectDate.setText(year + "-" + (month + 1) + "-" + day);
                            }
                        }, year, month, dayOfMonth);
                datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis());
                datePickerDialog.show();
                break;
            case R.id.btnTime:
                Log.w("button", "select time button clicked!");
                hour = calendar.get(Calendar.HOUR);
                minute = calendar.get(Calendar.MINUTE);

                timePickerDialog = new TimePickerDialog(TaskActivity.this,
                        new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                selectTime.setText(hourOfDay + ":" + minute);
                            }
                        }, hour, minute, true);
                timePickerDialog.show();
                break;
        }
    }
}
