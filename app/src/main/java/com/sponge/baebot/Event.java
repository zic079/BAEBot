package com.sponge.baebot;

public class Event {
    private String title, description, eventId;
    private int year, month, dayOfMonth, hour_start, minute_start, hour_end, minute_end;

    public Event(){ }
    public Event(String eventId, String title, String description, int year, int month, int dayOfMonth,
                 int hour_start, int minute_start, int hour_end, int minute_end){
        this.eventId = eventId;
        this.title = title;
        this.description = description;
        this.year = year;
        this.month = month;
        this.dayOfMonth = dayOfMonth;
        this.hour_start = hour_start;
        this.minute_start = minute_start;
        this.hour_end = hour_end;
        this.minute_end = minute_end;
    }
    public  String getEventId(){
        return eventId;
    }

    public String getTitle(){
        return title;
    }

    public String getDescription(){
        return description;
    }

    public int getYear(){
        return year;
    }

    public int getMonth(){
        return month;
    }

    public int getDayOfMonth(){
        return dayOfMonth;
    }

    public int getHourStart(){
        return hour_start;
    }

    public int getMinuteStart(){
        return minute_start;
    }

    public int getHourEnd(){
        return hour_end;
    }

    public int getMinuteEnd(){
        return minute_end;
    }
}
