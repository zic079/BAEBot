package com.sponge.baebot;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.CalendarView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.sponge.baebot.CalendarQueryHandler;

import com.sponge.baebot.R;
import com.sponge.baebot.RecyclerViewAdapter;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class ShowCalendarActivity extends AppCompatActivity implements PopupMenu.OnMenuItemClickListener, View.OnClickListener {


    private static final String TAG = "CalendarActivity";
    private CalendarView mCalendarView;
    private TextView myDate;
    private int inputYear, inputMonth, inputDay;
    private CalendarQueryHandler handler;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private ViewPagerAdapter viewPagerAdapter;
    private Calendar calendar = Calendar.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState){

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_calendar);

        handler = new CalendarQueryHandler(this, this.getContentResolver()) {};

        mCalendarView = (CalendarView) findViewById(R.id.calendarView);
        myDate = (TextView) findViewById(R.id.myDate);
        DateFormat format1 = new SimpleDateFormat("yyyy/MM/dd");
        myDate.setText(format1.format(calendar.getTime()));

        mCalendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(CalendarView view, int year, int month, int dayOfMonth) {
                String dateDisplay = year + "/" + (month+1) + "/" + dayOfMonth;
                myDate.setText(dateDisplay);

                Calendar startDate_offset = new GregorianCalendar(year,month,dayOfMonth-1);
                Calendar startDate = new GregorianCalendar(year,month,dayOfMonth);
                Calendar endDate = new GregorianCalendar(year,month,dayOfMonth+1);

                handler.readEvent(startDate_offset, startDate, endDate);
            }
        });

        ////TESTING
        View mBtn = findViewById(R.id.imageButton);
        mBtn.setOnClickListener(this);

    }
    public void showPopup(View v) {
        PopupMenu popupMenu = new PopupMenu(this,v);
        popupMenu.setOnMenuItemClickListener(this);
        popupMenu.inflate(R.menu.popup_menu);
        popupMenu.show();
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.edit:
                Toast.makeText(this, "Hello Edit!", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.delete:
                Toast.makeText(this, "Hello Delete!", Toast.LENGTH_SHORT).show();
                return true;
            default:
                return false;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.imageButton:
                Log.d(TAG, "onClick: CLICKED");
                handler.deleteEvent();
        }
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
