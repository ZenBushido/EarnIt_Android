package com.mobiledi.earnit.model;


import com.mobiledi.earnit.libmoduleExpandable.Model.ParentListItem;
import com.mobiledi.earnit.utils.AppConstant;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Created by praks on 13/07/17.
 */

public class ChildsTaskObject implements ParentListItem, Serializable {
    String dueDate;
    ArrayList<Tasks> tasks;

    public ChildsTaskObject(){

    }

    public ChildsTaskObject(String dueDate, ArrayList<Tasks> tasks) {
        this.dueDate = dueDate;
        this.tasks = tasks;
    }

    public String getDueDate() {
        return dueDate;
    }

    public void setDueDate(String dueDate) {
        this.dueDate = dueDate;
    }

    public ArrayList<Tasks> getTasks() {
        return tasks;
    }

    public void setTasks(ArrayList<Tasks> tasks) {
        this.tasks = tasks;
    }

    @Override
    public List<Tasks> getChildItemList() {
        return tasks;
    }

    @Override
    public boolean isInitiallyExpanded() {
        return true;
    }

    @Override
    public String toString() {
        StringBuilder tasksString = new StringBuilder();
        for (Tasks task : getTasks()){
            tasksString.append(task.toString());
        }
        return "{\"ChildsTaskObject\":{" +
                "dueDate='" + dueDate + '\'' +
                ", tasks=" + tasksString +
                "}}";
    }
}
