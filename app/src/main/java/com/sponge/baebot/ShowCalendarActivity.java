package com.sponge.baebot;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.CalendarView;
import android.widget.TextView;

import java.util.ArrayList;

public class ShowCalendarActivity extends AppCompatActivity
        implements View.OnClickListener {


    private static final String TAG = "CalendarActivity";

    private CalendarView mCalendarView;

    private TextView myDate;

    @Override
    protected void onCreate(Bundle savedInstanceState){

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_calendar);

        mCalendarView = (CalendarView) findViewById(R.id.calendarView);
        myDate = (TextView) findViewById(R.id.myDate);
        mCalendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(CalendarView view, int year, int month, int dayOfMonth) {
                String date = (year + 1) + "/" + month + "/" + dayOfMonth;
                myDate.setText(date);
            }
        });

    }

    @Override
    public void onClick(View v) {
    }
}
