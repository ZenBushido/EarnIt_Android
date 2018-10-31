package com.firepitmedia.earnit.stickyEvent;

import com.firepitmedia.earnit.model.AddTaskModel;

public class MessageEvent {

    AddTaskModel.repititionSchedule response;

    public AddTaskModel.repititionSchedule getResponse() {
        return response;
    }

    public void setResponse(AddTaskModel.repititionSchedule response) {
        this.response = response;
    }

    public MessageEvent(AddTaskModel.repititionSchedule response/*, List<BlockingApp> blockingApps*/) {
        this.response = response;
        /*this.blockingApps = blockingApps;*/
    }

    @Override
    public String toString() {
        return "MessageEvent{" +
                "response=" + response +
                '}';
    }
}