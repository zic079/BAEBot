package com.sponge.baebot;

public class Task {
    private String title, description, taskId;
    private long timestamp;

    public Task(){}

    public boolean isCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }

    private boolean completed = false;

    public Task(String taskId, String title, String description, long timestamp) {
        this.taskId = taskId;
        this.title = title;
        this.description = description;
        this.timestamp = timestamp;
    }

    public Task(String taskId, String title, String description, long timestamp, boolean completed) {
        this.taskId = taskId;
        this.title = title;
        this.description = description;
        this.timestamp = timestamp;
        this.completed = completed;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }



    public String getTaskId() {
        return taskId;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public String toString(){
        return this.title + " " + this.taskId + " " + Long.toString(this.timestamp) + " " + this.description;
    }


}