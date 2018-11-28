package com.sponge.baebot;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.CalendarView;
import android.widget.TextView;

import com.sponge.baebot.CalendarQueryHandler;

import com.sponge.baebot.R;
import com.sponge.baebot.RecyclerViewAdapter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class ShowCalendarActivity extends AppCompatActivity {


    private static final String TAG = "CalendarActivity";
    private CalendarView mCalendarView;
    private TextView myDate;
    private int inputYear, inputMonth, inputDay;
    private CalendarQueryHandler handler;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private ViewPagerAdapter viewPagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState){

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_calendar);

        handler = new CalendarQueryHandler(this, this.getContentResolver()) {};
        tabLayout = (TabLayout)findViewById(R.id.tablayout);
        viewPager = (ViewPager)findViewById(R.id.viewpager);
        viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());
        viewPagerAdapter.addFragment(new FragmentEvent(),"Event");
        viewPagerAdapter.addFragment(new FragmentTask(),"Task");
        viewPager.setAdapter(viewPagerAdapter);
        tabLayout.setupWithViewPager(viewPager);

        mCalendarView = (CalendarView) findViewById(R.id.calendarView);
        myDate = (TextView) findViewById(R.id.myDate);

        mCalendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(CalendarView view, int year, int month, int dayOfMonth) {
                String dateDisplay = year + "/" + (month+1) + "/" + dayOfMonth;
                myDate.setText(dateDisplay);

                Calendar startDate_offset = new GregorianCalendar(year,month,dayOfMonth-1);
                Calendar startDate = new GregorianCalendar(year,month,dayOfMonth);
                Calendar endDate = new GregorianCalendar(year,month,dayOfMonth+1);

                handler.readEvent(startDate_offset, startDate, endDate);

                /*
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

                */
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

//    private void initRecyclerView(ArrayList<String> events) {
//        RecyclerView recyclerView = findViewById(R.id.event_recyclerView);
//        RecyclerViewAdapter adapter = new RecyclerViewAdapter(events,this);
//        recyclerView.setAdapter(adapter);
//        recyclerView.setLayoutManager(new LinearLayoutManager(this));
//    }

}
