package com.firepitmedia.earnit.model.adjustBalance;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;


public class Goal {


    public Goal(Integer id) {
        this.id = id;
    }

    @SerializedName("id")
    @Expose
    private Integer id;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }
}
