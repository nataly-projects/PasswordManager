package com.example.passmanager.model;

import com.example.passmanager.MainActivity;

public class Header implements MainActivity.ListItem {

    private String title;
    private boolean expand;

    public Header() {
        this.expand = true;
    }

    public String getTitle(){
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public boolean isExpand() {
        return expand;
    }

    public void setExpand(boolean expand) {
        this.expand = expand;
    }

    @Override
    public boolean isHeader() {
        return true;
    }

    @Override
    public String getName() {
        return title;
    }
}
