package com.firepitmedia.earnit.model;

import java.io.Serializable;
import java.util.Objects;

public class BlockingApp implements Serializable {

    private long id;
    private String name;
    private String createdDate;
    private boolean ignoredByParent;

    public BlockingApp(long id, String name, String createdDate, boolean ignoredByParent) {
        this.id = id;
        this.name = name;
        this.createdDate = createdDate;
        this.ignoredByParent = ignoredByParent;
    }

    public BlockingApp() {
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
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

    public boolean isIgnoredByParent() {
        return ignoredByParent;
    }

    public void setIgnoredByParent(boolean ignoredByParent) {
        this.ignoredByParent = ignoredByParent;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BlockingApp that = (BlockingApp) o;
        return Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {

        return Objects.hash(name);
    }

    @Override
    public String toString() {
        return "BlockingApp{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }
}
