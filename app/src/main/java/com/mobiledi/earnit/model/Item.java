package com.mobiledi.earnit.model;

/**
 * Created by mobile-di on 28/10/17.
 */

public class Item {

    private String mTitle;
    private int id;

    public Item(int id, String mTitle) {

        this.id = id;
        this.mTitle = mTitle;
    }

    public String getmTitle() {
        return mTitle;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setmTitle(String mTitle) {
        this.mTitle = mTitle;
    }

    public String getTitle() {
        return mTitle;
    }

    @Override
    public String toString() {
        return "Item{" +
                "mTitle='" + mTitle + '\'' +
                ", id=" + id +
                '}';
    }
}
