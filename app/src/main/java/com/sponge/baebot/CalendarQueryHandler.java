// https://developer.android.com/guide/topics/providers/calendar-provider
// https://developer.android.com/reference/android/content/AsyncQueryHandler
// https://billthefarmer.github.io/blog/adding-a-calendar-event/
package com.sponge.baebot;

import android.content.AsyncQueryHandler;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.CalendarContract;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import java.lang.ref.WeakReference;

public class CalendarQueryHandler extends AsyncQueryHandler{
    private static final String TAG = "CalendarQueryHandler";

    // Projection array. Creating indices for this array instead of doing
    // dynamic lookups improves performance.
    // https://developer.android.com/reference/android/provider/CalendarContract.Events
    private static final String[] EVENT_PROJECTION = new String[] {
            CalendarContract.Events._ID,                          // 0
            CalendarContract.Events.TITLE,                        // 1
            CalendarContract.Events.DESCRIPTION,                  // 2
            CalendarContract.Events.DTSTART,                      // 3
            CalendarContract.Events.DTEND,                        // 4
            CalendarContract.Events.ALL_DAY                       // 5
    };

    private static final int CALENDAR = 0;
    private static final int EVENT    = 1;
    private static final int REMINDER = 2;

    // The indices for the projection array above.
    private static final int PROJECTION_ID_INDEX = 0;
    private static final int PROJECTION_TITLE_INDEX = 1;
    private static final int PROJECTION_DESCRIPTION_INDEX = 2;
    private static final int PROJECTION_TIMESTART_INDEX = 3;
    private static final int PROJECTION_TIMEEND_INDEX = 4;
    private static final int PROJECTION_ALLDAY_INDEX = 5;

    private WeakReference<AppCompatActivity> activityRef;

    //private int queryDays = 0;
    private Calendar queryStartDate_offset;
    private Calendar queryStartDate;
    private Calendar queryEndDate;

    // Public constructors: AsyncQueryHandler(ContentResolver cr)
    // https://developer.android.com/reference/android/content/AsyncQueryHandler
    // Constructor of the class
    public CalendarQueryHandler(AppCompatActivity activity, ContentResolver cr) {
        super(cr);
        activityRef = new WeakReference<AppCompatActivity>(activity);
    }

    @Override
    public void onQueryComplete(int token, Object object, Cursor cursor) {

        // Get the field values
        Log.d(TAG, "onQueryComplete: complete query");

        ArrayList<String> calendarData = new ArrayList<>();

        while (cursor.moveToNext()) {

            // information of event
            String eventTitle;
            String eventBeginMill;
            String eventBeginDate;
            String isAllDay;

            Log.d("readEvent", "Event title: " + cursor.getString(PROJECTION_TITLE_INDEX));
            Log.d("readEvent", "Event ID: " + cursor.getString(PROJECTION_ID_INDEX));

            // Get the field values
            eventTitle = cursor.getString(PROJECTION_TITLE_INDEX);

            // Note: event is in UTC time
            eventBeginMill = cursor.getString(PROJECTION_TIMESTART_INDEX);

            // check event is all day event
            isAllDay = cursor.getString(PROJECTION_ALLDAY_INDEX);
            Log.d("allDay", "is all day " + isAllDay);

            // check event is in searching range, all day event has offset
            if(Long.parseLong(eventBeginMill) >= queryStartDate.getTimeInMillis() || Integer.parseInt(isAllDay) == 1) {
                Log.d(TAG, "onQueryComplete: eventBeginMill - " + eventBeginMill);

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
                calendarData.add(currentData);
            }
        }

        updateEventList(calendarData);
    }

    @Override
    public void onInsertComplete(int token, Object object, Uri uri) {
        if (uri != null)
        {

            Log.d(TAG, "Insert complete " + uri.getLastPathSegment());
            readEvent(queryStartDate_offset, queryStartDate, queryEndDate);
        }
    }

    @Override
    protected void onUpdateComplete(int token, Object cookie, int result) {
        // update() completed
    }

    @Override
    protected void onDeleteComplete(int token, Object cookie, int result) {
        // delete() completed
        readEvent(queryStartDate_offset, queryStartDate, queryEndDate);
    }

    /**
     * send update query request to calendar provider
     *
     */
    public void readEvent(Calendar startDate_offset, Calendar startDate, Calendar endDate) {

        //queryDays = numsDay;

        // Reason for start date offset: all day event use PST time, causing offset
        queryStartDate_offset = startDate_offset;
        queryStartDate = startDate;
        queryEndDate = endDate;

        Log.d(TAG, "readEvent: startDate - " + startDate);

        Uri CALENDAR_URI = Uri.parse("content://com.android.calendar/events");

        // up to event end 00:59 on next day
        long endAdjust = endDate.getTimeInMillis() + 3599999;

        // set selection and selectionArgs as null
        String selection = "((dtstart >= " + startDate_offset.getTimeInMillis() + ") AND (dtend <= " + (endAdjust)+"))";
        String[] selectionArgs = null;

        Log.d(TAG, "readEvent: startmill - " + startDate.getTimeInMillis());
        Log.d(TAG, "readEvent: endDate - " + endDate.getTimeInMillis());

        //ArrayList<String> calendarData = new ArrayList<>();

        //Cursor cur = cr.query(CalendarContract.Calendars.CONTENT_URI, EVENT_PROJECTION, selection, selectionArgs, null);
        startQuery(EVENT, null, CALENDAR_URI,
                   EVENT_PROJECTION, selection, selectionArgs, CalendarContract.Events.DTSTART + " ASC");

    }


    // insertEvent method
    public void insertEvent(Context context, long startTime,
                                   long endTime, String title, String description) {

        //ContentResolver cr = context.getContentResolver();

        ContentValues values = new ContentValues();
        // hardcode calendar ID
        values.put(CalendarContract.Events.CALENDAR_ID, 3);
        values.put(CalendarContract.Events.DTSTART, startTime);
        values.put(CalendarContract.Events.DTEND, endTime);
        values.put(CalendarContract.Events.EVENT_TIMEZONE, "PST");
        values.put(CalendarContract.Events.TITLE, title);
        values.put(CalendarContract.Events.DESCRIPTION, description);

        // Uri uri = cr.insert(CalendarContract.Events.CONTENT_URI, values);

        if (BuildConfig.DEBUG) {
            Log.d(TAG, "Calendar query start");
        }

        startInsert(CALENDAR, null, CalendarContract.Events.CONTENT_URI, values);
    }

    public void deleteEvent() {
        Log.d(TAG, "deleteEvent: !!!");

        // hard code test
        long eventID = 55;

        //ContentResolver cr = context.getContentResolver();
        Uri deleteUri = null;
        deleteUri = ContentUris.withAppendedId(CalendarContract.Events.CONTENT_URI, eventID);
        //int rows = cr.delete(deleteUri, null, null);
        startDelete(EVENT, null, deleteUri, null,null);

    }

    // helper function - convert millisecond to readable date
    private String milliToDate(String milliSec) {
        String date;                      // date convert from millisecond
        //int offset;                     // offset when event is all day event
        SimpleDateFormat formatter;       //date formatter for millisecond conversion

        formatter = new SimpleDateFormat("HH:mm");

        Calendar calendarTemp = Calendar.getInstance();
        //calendarTemp.setTimeInMillis(Long.parseLong(milliSec) + offset);
        calendarTemp.setTimeInMillis(Long.parseLong(milliSec));
        date = formatter.format(calendarTemp.getTime());

        return date;
    }


    private void updateEventList(ArrayList<String> calendarData) {
        AppCompatActivity mActivity = activityRef.get();

        RecyclerView recyclerView = mActivity.findViewById(R.id.event_recyclerView);
        RecyclerViewAdapter adapter = new RecyclerViewAdapter(calendarData, mActivity);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(mActivity));
    }

}
