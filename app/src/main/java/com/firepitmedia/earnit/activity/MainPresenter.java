package com.firepitmedia.earnit.activity;

import android.support.annotation.Nullable;
public  class MainPresenter {

    @Nullable
    private MainView view;

    public MainPresenter(@Nullable MainView view) {
        this.view = view;
    }
    public void addCalendarView() {
        if (null != getView()) {
            getView().prepareCalendarView();
        }
    }
    public void animate() {
        if (null != getView()) {
            getView().animateViews();
        }
    }
    @Nullable
    public MainView getView() {
        return view;
    }
}

