package com.example.plansplit;

public class Notification {

    private String mainText, secondText, date, clock;
    private int image;

    public Notification(String mainText, String secondText, String date, String clock, int image){
        this.date = date;
        this.mainText=mainText;
        this.secondText=secondText;
        this.clock=clock;
        this.image = image;
    }

    public int getImage() {
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
