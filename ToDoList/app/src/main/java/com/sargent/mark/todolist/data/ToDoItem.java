package com.sargent.mark.todolist.data;

/**
 * Created by mark on 7/4/17.
 */

public class ToDoItem {
    private String description;
    private String dueDate;
    //The boolean completed will be used to keep track if a task is done or not. Implemented by a
    //simple int:  1 for completed or 0 for not.
    private int completed;
    //A string to represent the category of the task
    private String category;

    public ToDoItem(String description, String dueDate, int completed, String category) {
        this.description = description;
        this.dueDate = dueDate;
        this.completed = completed;
        this.category = category;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDueDate() {
        return dueDate;
    }

    public void setDueDate(String dueDate) {
        this.dueDate = dueDate;
    }

    public int getCompleted(){ return completed; }

    public void setCompleted(int completed){ this.completed = completed; }

    public String getCategory(){ return this.category; }

    public void setCategory(String category) { this.category = category; }
}
