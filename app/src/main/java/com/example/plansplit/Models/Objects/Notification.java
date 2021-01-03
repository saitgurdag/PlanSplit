package com.example.plansplit.Models.Objects;

import android.content.Context;

import com.example.plansplit.R;

import org.jetbrains.annotations.NotNull;

import java.util.Calendar;

public class Notification {

    private String mainText, secondText, date, clock, image;

    public Notification(Context context, @NotNull String type, long date, String image, String info){
        switch (type){
            case "over_budget":
                this.mainText = context.getResources().getString(R.string.notification_over_budget);
                this.secondText = info;
                break;
            case "budget_change":
                this.mainText = context.getResources().getString(R.string.notification_budget_change);
                this.secondText = info;
                break;
            case "friend_add":
                this.mainText = context.getResources().getString(R.string.notification_friend_add, info);
                this.secondText = "";
                break;
            case "friend_remove":
                this.mainText = context.getResources().getString(R.string.notification_friend_remove, info);
                this.secondText = "";
                break;
            case "group_add":
                this.mainText = context.getResources().getString(R.string.notification_group_add, info);
                this.secondText = "";
                break;
            case "group_remove":
                this.mainText = context.getResources().getString(R.string.notification_group_remove, info);
                this.secondText = "";
                break;
            case "monthly_expense":
                this.mainText = context.getResources().getString(R.string.notification_monthly_expense);
                this.secondText = info;
                break;
            //todo more
            default:
                this.mainText = "";
                this.secondText = "";
        }
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(date);
        String minute;
        if (c.get(Calendar.MINUTE) < 10){
            minute = "0" + c.get(Calendar.MINUTE);
        }else{
            minute = String.valueOf(c.get(Calendar.MINUTE));
        }
        this.date = c.get(Calendar.DAY_OF_MONTH) + "." + (c.get(Calendar.MONTH) + 1) + "." + c.get(Calendar.YEAR);
        this.clock = c.get(Calendar.HOUR_OF_DAY) + ":" + minute;
        this.image = image;
    }

    public String getImage() {
        return image;
    }

    public String getClock() {
        return clock;
    }

    public String getMainText() {
        return mainText;
    }

    public String getSecondText() {
        return secondText;
    }

    public String getDate() {
        return date;
    }

}
