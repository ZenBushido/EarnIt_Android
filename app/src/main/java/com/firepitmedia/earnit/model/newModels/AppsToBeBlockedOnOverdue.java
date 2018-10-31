package com.firepitmedia.earnit.model.newModels;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.Objects;

public class AppsToBeBlockedOnOverdue {

    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("createdDate")
    @Expose
    private String createdDate;
    @SerializedName("ignoredByParent")
    @Expose
    private Boolean ignoredByParent;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(String createdDate) {
        this.createdDate = createdDate;
    }

    public Boolean getIgnoredByParent() {
        return ignoredByParent;
    }

    public void setIgnoredByParent(Boolean ignoredByParent) {
        this.ignoredByParent = ignoredByParent;
    }

    @Override
    public String toString() {
        return "AppsToBeBlockedOnOverdue{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", createdDate='" + createdDate + '\'' +
                ", ignoredByParent=" + ignoredByParent +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AppsToBeBlockedOnOverdue that = (AppsToBeBlockedOnOverdue) o;
        return Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {

        return Objects.hash(name);
    }
}