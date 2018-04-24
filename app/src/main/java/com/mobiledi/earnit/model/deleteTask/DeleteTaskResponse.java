package com.mobiledi.earnit.model.deleteTask;

import java.util.Arrays;
import java.util.List;

public class DeleteTaskResponse {

    private List<String> message;

    public DeleteTaskResponse(List<String> message) {
        this.message = message;
    }

    public List<String> getMessage() {
        return message;
    }

    public void setMessage(List<String> message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return "DeleteTaskResponse{" +
                "message=" + message +
                '}';
    }
}
