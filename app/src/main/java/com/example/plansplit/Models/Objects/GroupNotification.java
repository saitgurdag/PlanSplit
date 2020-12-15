package com.example.plansplit.Models.Objects;

import java.util.Date;

public class GroupNotification {
    private String notif_creator_id, notif_text, notif_type;  //notif_type : Üye eklennmesi, ödeme yapılması, harcama eklenmesi
    private Date creation_date;

    public GroupNotification(String notif_creator_id, String notif_text, String notif_type, Date creation_date) {
        this.notif_creator_id = notif_creator_id;
        this.notif_text = notif_text;
        this.notif_type = notif_type;
        this.creation_date = creation_date;
    }

    public String getNotif_creator_id() {
        return notif_creator_id;
    }

    public void setNotif_creator_id(String notif_creator_id) {
        this.notif_creator_id = notif_creator_id;
    }

    public String getNotif_text() {
        return notif_text;
    }

    public void setNotif_text(String notif_text) {
        this.notif_text = notif_text;
    }

    public String getNotif_type() {
        return notif_type;
    }

    public void setNotif_type(String notif_type) {
        this.notif_type = notif_type;
    }

    public Date getCreation_date() {
        return creation_date;
    }

    public void setCreation_date(Date creation_date) {
        this.creation_date = creation_date;
    }
}
