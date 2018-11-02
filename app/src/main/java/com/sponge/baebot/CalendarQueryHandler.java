package com.sponge.baebot;

import android.content.AsyncQueryHandler;
import android.content.ContentResolver;
import android.provider.CalendarContract;

public class CalendarQueryHandler extends AsyncQueryHandler {
    private static final String TAG = "QueryHandler";

    // From "Calendar provider overview"
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

    private static CalendarQueryHandler calendarQueryHandler;

    // Public constructors: AsyncQueryHandler(ContentResolver cr)
    // https://developer.android.com/reference/android/content/AsyncQueryHandler
    // Constructor of the class
    public CalendarQueryHandler(ContentResolver cr)
    {
        super(cr);
    }


}
