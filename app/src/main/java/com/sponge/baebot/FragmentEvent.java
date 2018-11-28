package com.sponge.baebot;

import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;


public class FragmentEvent extends Fragment {

    private View v;
    private RecyclerView recyclerView;
    private ArrayList<String> eventList;

    private static final String[] EVENT_PROJECTION = new String[] {
            CalendarContract.Events.CALENDAR_ID,                  // 0
            CalendarContract.Events.TITLE,                        // 1
            CalendarContract.Events.DESCRIPTION,                  // 2
            CalendarContract.Events.DTSTART,                      // 3
            CalendarContract.Events.DTEND,                        // 4
            CalendarContract.Events.ALL_DAY                       // 5
    };

    // The indices for the projection array above.
    private static final int PROJECTION_ID_INDEX = 0;
    private static final int PROJECTION_TITLE_INDEX = 1;
    private static final int PROJECTION_DESCRIPTION_INDEX = 2;
    private static final int PROJECTION_TIMESTART_INDEX = 3;
    private static final int PROJECTION_TIMEEND_INDEX = 4;
    private static final int PROJECTION_ALLDAY_INDEX = 5;

    public FragmentEvent() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        v=inflater.inflate(R.layout.event_fragment,container,false);
        recyclerView = (RecyclerView)v.findViewById(R.id.event_recyclerView);
        RecyclerViewAdapter recyclerViewAdapter = new RecyclerViewAdapter(eventList,getContext());
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(recyclerViewAdapter);
        return v;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        updateEvent();
    }

    private void updateEvent() {

        ContentResolver cr = getActivity().getContentResolver();
        eventList = new ArrayList<>();

        // use CalendarContract.Instances for read data on calendar (rather than owner info)
        Uri CALENDAR_URI = Uri.parse("content://com.android.calendar/events");
        Cursor cur = null;

        // show today's event
        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        int dayOfMonth = cal.get(Calendar.DAY_OF_MONTH);

        Calendar startDate_offset = new GregorianCalendar(year,month,dayOfMonth-1);
        Calendar startDate = new GregorianCalendar(year,month,dayOfMonth);
        Calendar endDate = new GregorianCalendar(year,month,dayOfMonth+1);

        // set selection and selectionArgs as null
        String selection = "((dtstart >= " + startDate_offset.getTimeInMillis() + ") AND (dtend <= " + endDate.getTimeInMillis()+"))";
        String[] selectionArgs = null;


        cur = cr.query(CALENDAR_URI, EVENT_PROJECTION, selection, selectionArgs, CalendarContract.Events.DTSTART + " ASC");
        //ArrayList<String> calendarData = new ArrayList<>();

        if(cur != null && cur.getCount() > 0) {
            Log.d("readEvent", "events found");

            cur.moveToFirst();
            while (cur.moveToNext()) {
                // information of event
                String eventTitle;
                String eventBeginMill;
                String eventBeginDate;
                String isAllDay;

                // Get the field values
                eventTitle = cur.getString(PROJECTION_TITLE_INDEX);
                // Note: event is in UTC time
                eventBeginMill = cur.getString(PROJECTION_TIMESTART_INDEX);
                // check event is all day event
                isAllDay = cur.getString(PROJECTION_ALLDAY_INDEX);

                // check event is in searching range, all day event has offset
                if(Long.parseLong(eventBeginMill) >= startDate.getTimeInMillis() || Integer.parseInt(isAllDay) == 1) {
                    // Building string of current cursor data
                    // String currentData = String.format("Calendar ID: %s\nDisplay Name: %s\nAccount Name: %s\nOwner Name: %s", calID, displayName, accountName, ownerName);
                    String currentData;

                    if(Integer.parseInt(isAllDay) == 1) {
                        currentData = String.format("%s          All Day", eventTitle);
                    }

                    else {
                        eventBeginDate = milliToDate(eventBeginMill);
                        currentData = String.format("%s          %s", eventTitle, eventBeginDate);
                    }

                    // Log.d("readEvent", currentData);
                    eventList.add(currentData);
                }
            }
        }

        //ArrayAdapter<String> stringArrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, android.R.id.text1, calendarData);
        //listView.setAdapter(stringArrayAdapter);

        //return calendarData;
    }

    // helper function - convert millisecond to readable date
    private String milliToDate(String milliSec) {
        String date;                    // date convert from millisecond
        //int offset;                     // offset when event is all day event
        SimpleDateFormat formatter;     //date formatter for millisecond conversion

        formatter = new SimpleDateFormat("HH:mm");

        Calendar calendarTemp = Calendar.getInstance();
        //calendarTemp.setTimeInMillis(Long.parseLong(milliSec) + offset);
        calendarTemp.setTimeInMillis(Long.parseLong(milliSec));
        date = formatter.format(calendarTemp.getTime());

        return date;
    }
}
