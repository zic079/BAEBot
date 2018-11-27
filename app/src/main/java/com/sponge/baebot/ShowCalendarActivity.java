package com.sponge.baebot;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.CalendarView;
import android.widget.TextView;

import com.sponge.baebot.R;
import com.sponge.baebot.RecyclerViewAdapter;

import java.util.ArrayList;

public class ShowCalendarActivity extends AppCompatActivity {


    private static final String TAG = "CalendarActivity";

    private CalendarView mCalendarView;

    private TextView myDate;

    private int inputYear, inputMonth, inputDay;

    @Override
    protected void onCreate(Bundle savedInstanceState){

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_calendar);
        mCalendarView = (CalendarView) findViewById(R.id.calendarView);
        myDate = (TextView) findViewById(R.id.myDate);

        mCalendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(CalendarView view, int year, int month, int dayOfMonth) {
                String date = year + "/" + (month+1) + "/" + dayOfMonth;
                myDate.setText(date);
                ArrayList<String> eventList = new ArrayList<>();
                initRecyclerView(eventList);
                if (year == 2018 && month+1 == 10 && dayOfMonth == 26) {
                    eventList.add("find a boyfriend");
                    eventList.add("review CSE110 quiz1");
                    eventList.add("review CSE110 quiz2");
                    eventList.add("review CSE110 quiz3");
                    eventList.add("review CSE110 quiz4");
                    eventList.add("review CSE110 quiz5");
                    eventList.add("review CSE110 final");
                    initRecyclerView(eventList);
                }

            }
        });
    }

//    private boolean checkDate (int year, int month, int dayOfMonth) {
//        String date = year + "/" + (month) + "/" + dayOfMonth;
//        System.out.println(date);
//        if (year == inputYear && month == inputMonth && dayOfMonth == inputDay)
//            return true;
//        return false;
//    }

    private void initRecyclerView(ArrayList<String> events) {
        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        RecyclerViewAdapter adapter = new RecyclerViewAdapter(events,this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

}
