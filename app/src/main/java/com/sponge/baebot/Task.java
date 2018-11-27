package com.sponge.baebot;

public class Task {
    private String title, description, taskId;
    private int year, month, dayOfMonth, hour, minute;

    public Task(){ }
    public Task(String taskId, String title, String description,int year, int month, int dayOfMonth,
                int hour, int minute){
        this.taskId = taskId;
        this.title = title;
        this.description = description;
        this.year = year;
        this.month = month;
        this.dayOfMonth = dayOfMonth;
        this.hour = hour;
        this.minute = minute;
    }
    public  String getTaskId(){
        return taskId;
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

    public int getHour(){
        return hour;
    }

    public int getMinute(){
        return minute;
    }
}
