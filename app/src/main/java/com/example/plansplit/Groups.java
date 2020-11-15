package com.example.plansplit;

public class Groups {
    private int group_id;
    private String group_name;
    private String group_type;
    private int group_photo;
    private int group_notification_symbol;
    private int group_balance_sheet;

    public Groups(int group_id, String group_name, String group_type, int group_photo, int group_notification_symbol, int group_balance_sheet) {
        this.group_id = group_id;
        this.group_name = group_name;
        this.group_type = group_type;
        this.group_photo = group_photo;
        this.group_notification_symbol = group_notification_symbol;
        this.group_balance_sheet = group_balance_sheet;
    }

    public int getGroup_notification_symbol() {
        return group_notification_symbol;
    }

    public void setGroup_notification_symbol(int group_notification_symbol) {
        this.group_notification_symbol = group_notification_symbol;
    }

    public String getGroup_balance_sheet() {
        return String.valueOf(group_balance_sheet) + "  TL";
    }

    public void setGroup_balance_sheet(int group_balance_sheet) {
        this.group_balance_sheet = group_balance_sheet;
    }

    public int getGroup_id() {
        return group_id;
    }

    public void setGroup_id(int group_id) {
        this.group_id = group_id;
    }

    public String getGroup_name() {
        return group_name;
    }

    public void setGroup_name(String group_name) {
        this.group_name = group_name;
    }

    public String getGroup_type() {
        return group_type;
    }

    public void setGroup_type(String group_type) {
        this.group_type = group_type;
    }

    public int getGroup_photo() {
        return group_photo;
    }

    public void setGroup_photo(int group_photo) {
        this.group_photo = group_photo;
    }
}
