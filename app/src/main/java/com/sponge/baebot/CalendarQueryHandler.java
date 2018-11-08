// https://developer.android.com/guide/topics/providers/calendar-provider
// https://developer.android.com/reference/android/content/AsyncQueryHandler
// https://billthefarmer.github.io/blog/adding-a-calendar-event/
package com.sponge.baebot;

import android.content.AsyncQueryHandler;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.CalendarContract;
import android.util.Log;

import java.util.ArrayList;
import java.util.TimeZone;

public class CalendarQueryHandler extends AsyncQueryHandler {
    private static final String TAG = "CalendarQueryHandler";
/*
    // Projection array. Creating indices for this array instead of doing
    // dynamic lookups improves performance.
    public static final String[] EVENT_PROJECTION = new String[] {
            CalendarContract.Calendars._ID,                           // 0
            CalendarContract.Calendars.ACCOUNT_NAME,                  // 1
            CalendarContract.Calendars.CALENDAR_DISPLAY_NAME,         // 2
            CalendarContract.Calendars.OWNER_ACCOUNT                  // 3
    };

    // The indices for the projection array above.
    private static final int PROJECTION_ID_INDEX = 0;
    private static final int PROJECTION_ACCOUNT_NAME_INDEX = 1;
    private static final int PROJECTION_DISPLAY_NAME_INDEX = 2;
    private static final int PROJECTION_OWNER_ACCOUNT_INDEX = 3;

    private static final int CALENDAR = 0;
    private static final int EVENT    = 1;
    private static final int REMINDER = 2;
*/

    // Projection array. Creating indices for this array instead of doing
    // dynamic lookups improves performance.
    // https://developer.android.com/reference/android/provider/CalendarContract.Events
    public static final String[] EVENT_PROJECTION = new String[] {
            CalendarContract.Events.CALENDAR_ID,                  // 0
            CalendarContract.Events.TITLE,                        // 1
            CalendarContract.Events.DESCRIPTION,                  // 2
            CalendarContract.Events.DTSTART,                      // 3
            CalendarContract.Events.DTEND,                        // 4
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

    private static CalendarQueryHandler calendarQueryHandler;
    private static ArrayList<String> calendarData = new ArrayList<>();

    // Public constructors: AsyncQueryHandler(ContentResolver cr)
    // https://developer.android.com/reference/android/content/AsyncQueryHandler
    // Constructor of the class
    public CalendarQueryHandler(ContentResolver cr) {
        super(cr);
    }

    @Override
    public void onQueryComplete(int token, Object object, Cursor cursor) {
        // Use the cursor to move through the returned records
        //cursor.moveToFirst();

        // Get the field values
        ///long calendarID = cursor.getLong(PROJECTION_ID_INDEX);

        while (cursor.moveToNext()) {
            /*
            long calID = 0;
            String displayName = null;
            String accountName = null;
            String ownerName = null;

            // Get the field values
            calID = cursor.getLong(PROJECTION_ID_INDEX);
            displayName = cursor.getString(PROJECTION_DISPLAY_NAME_INDEX);
            accountName = cursor.getString(PROJECTION_ACCOUNT_NAME_INDEX);
            ownerName = cursor.getString(PROJECTION_OWNER_ACCOUNT_INDEX);

            // building string of current cursor data
            String currentData = String.format("Calendar ID: %s\nDisplay Name: %s\nAccount Name: %s\nOwner Name: %s", calID, displayName, accountName, ownerName);
            calendarData.add(currentData);

            Log.d(TAG, currentData);
            */

        }

        ///Log.d(TAG, "Calendar query complete " + calendarID);

        /*
        ContentValues values = (ContentValues) object;
        values.put(CalendarContract.Events.CALENDAR_ID, calendarID);
        values.put(CalendarContract.Events.EVENT_TIMEZONE,
                TimeZone.getDefault().getDisplayName());

        startInsert(EVENT, null, CalendarContract.Events.CONTENT_URI, values);
        */
    }

    @Override
    public void onInsertComplete(int token, Object object, Uri uri) {
        if (uri != null)
        {

            Log.d(TAG, "Insert complete " + uri.getLastPathSegment());

            switch (token)
            {
                case EVENT:
                    long eventID = Long.parseLong(uri.getLastPathSegment());
                    ContentValues values = new ContentValues();
                    values.put(CalendarContract.Reminders.MINUTES, 10);
                    values.put(CalendarContract.Reminders.EVENT_ID, eventID);
                    values.put(CalendarContract.Reminders.METHOD, CalendarContract.Reminders.METHOD_ALERT);
                    startInsert(REMINDER, null, CalendarContract.Reminders.CONTENT_URI, values);
                    break;
            }
        }
    }

    @Override
    protected void onUpdateComplete(int token, Object cookie, int result) {
        // update() completed
    }

    @Override
    protected void onDeleteComplete(int token, Object cookie, int result) {
        // delete() completed
    }

    public static ArrayList<String> readEvent(Context context) {

        ContentResolver cr = context.getContentResolver();

        // initialize the query handler if it didn't been initialized yet
        if (calendarQueryHandler == null) {
            calendarQueryHandler = new CalendarQueryHandler(cr);
        }

        // ContentResolver cr = context. getContentResolver();

        // ---- current design return all calendar event (no filtering)
        // could add the feature in the future - pass in from the parameter
        /*
        String selection = "((" + Calendars.ACCOUNT_NAME + " = ?) AND ("
                + Calendars.ACCOUNT_TYPE + " = ?) AND ("
                + Calendars.OWNER_ACCOUNT + " = ?))";
        String[] selectionArgs = new String[] {"hera@example.com", "com.example",
                "hera@example.com"};
        */

        // set selection and selectionArgs as null
        String selection = null;
        String[] selectionArgs = null;

        //ArrayList<String> calendarData = new ArrayList<>();

        //Cursor cur = cr.query(CalendarContract.Calendars.CONTENT_URI, EVENT_PROJECTION, selection, selectionArgs, null);
        calendarQueryHandler.startQuery(CALENDAR, null, CalendarContract.Calendars.CONTENT_URI,
                EVENT_PROJECTION, selection, selectionArgs, null);


        /*
        while (cur.moveToNext()) {
            long calID = 0;
            String displayName = null;
            String accountName = null;
            String ownerName = null;

            // Get the field values
            calID = cur.getLong(PROJECTION_ID_INDEX);
            displayName = cur.getString(PROJECTION_DISPLAY_NAME_INDEX);
            accountName = cur.getString(PROJECTION_ACCOUNT_NAME_INDEX);
            ownerName = cur.getString(PROJECTION_OWNER_ACCOUNT_INDEX);

            // building string of current cursor data
            String currentData = String.format("Calendar ID: %s\nDisplay Name: %s\nAccount Name: %s\nOwner Name: %s", calID, displayName, accountName, ownerName);
            calendarData.add(currentData);
        }
        */

        //ArrayAdapter<String> stringArrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, android.R.id.text1, calendarData);
        //listView.setAdapter(stringArrayAdapter);

        return calendarData;
    }


    // insertEvent method
    public static void insertEvent(Context context, long startTime,
                                   long endTime, String title, String description) {
        ContentResolver cr = context.getContentResolver();

        // initialize the query handler if it didn't been initialized yet
        if (calendarQueryHandler == null) {
            calendarQueryHandler = new CalendarQueryHandler(cr);
        }

        ContentValues values = new ContentValues();
        values.put(CalendarContract.Events.DTSTART, startTime);
        values.put(CalendarContract.Events.DTEND, endTime);
        values.put(CalendarContract.Events.TITLE, title);
        values.put(CalendarContract.Events.DESCRIPTION, description);

        // Uri uri = cr.insert(CalendarContract.Events.CONTENT_URI, values);

        if (BuildConfig.DEBUG) {
            Log.d(TAG, "Calendar query start");
        }

        // start the query
        calendarQueryHandler.startQuery(CALENDAR, values, CalendarContract.Calendars.CONTENT_URI,
                EVENT_PROJECTION, null, null, null);
    }
}
