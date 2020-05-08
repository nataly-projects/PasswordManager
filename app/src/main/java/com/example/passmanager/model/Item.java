package com.example.passmanager.model;

import com.example.passmanager.MainActivity;

public class Item implements MainActivity.ListItem {

    private String itemId;
    private String title;
    private String userName;
    private String password;
    private String category;
    private String email;
    private String note;

    public Item() {}

    public Item(String itemId, String title, String userName, String password, String category, String email, String note) {
        this.itemId = itemId;
        this.title = title;
        this.userName = userName;
        this.password = password;
        this.category = category;
        this.email = email;
        this.note = note;
    }

    public String getItemId() {
        return itemId;
    }

    public void setItemId(String itemId) {
        this.itemId = itemId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    @Override
    public boolean isHeader() {
        return false;
    }

    @Override
    public String getName() {
        return category;
    }


}
