package com.company;

import java.util.Date;

/**
 * Created by amorast on 8/22/17.
 */
public class Task {
    private Integer id;
    private String title;
    private String description;
    private Date dueDate;
    private Boolean complete;

    public Task() {}

    public Task(Integer id, String title, String description, Date dueDate, Boolean complete) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.dueDate = dueDate;
        this.complete = complete;
    }

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Date getDueDate() { return dueDate; }
    public void setDueDate(Date dueDate) { this.dueDate = dueDate; }

    public Boolean getComplete() { return complete; }
    public void setComplete(Boolean complete) { this.complete = complete; }

    @Override
    public String toString() {
        return "ID: " + id.toString() + ", Title: " + title + ", Description: " + description + ", Due Date: " + dueDate.toString() +
                ", Complete?: " + complete.toString() + ".\n";
    }

    @Override
    public boolean equals(Object o) {
        return o.toString().equals(toString());
    }
}
