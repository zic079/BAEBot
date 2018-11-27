package com.sponge.baebot;

public class Task {
    private String title, description, taskId;

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public void setDayOfMonth(int dayOfMonth) {
        this.dayOfMonth = dayOfMonth;
    }

    public void setHour(int hour) {
        this.hour = hour;
    }

    public void setMinute(int minute) {
        this.minute = minute;
    }

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
