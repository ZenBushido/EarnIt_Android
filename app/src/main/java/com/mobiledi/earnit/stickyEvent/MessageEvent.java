package com.mobiledi.earnit.stickyEvent;

import com.mobiledi.earnit.model.AddTaskModel;

public class MessageEvent {

    AddTaskModel.repititionSchedule response;

    public AddTaskModel.repititionSchedule getResponse() {
        return response;
    }

    public void setResponse(AddTaskModel.repititionSchedule response) {
        this.response = response;
    }

    public MessageEvent(AddTaskModel.repititionSchedule response) {
        this.response = response;
    }
}