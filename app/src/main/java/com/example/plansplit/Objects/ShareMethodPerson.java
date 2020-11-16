package com.example.plansplit.Objects;

public class ShareMethodPerson {
    private String name;
    private int cardView_shareMethodPersonPicture;
    private int checkBox_shareMethodPersonCheckBox;

    public String getName() {
        return name;
    }

    public int getCardView_shareMethodPersonPicture() {
        return cardView_shareMethodPersonPicture;
    }

    public int getCheckBox_shareMethodPersonCheckBox() {
        return checkBox_shareMethodPersonCheckBox;
    }


    public ShareMethodPerson(String name, int cardView_shareMethodPersonPicture, int checkBox_shareMethodPersonCheckBox) {
        this.name = name;
        this.cardView_shareMethodPersonPicture = cardView_shareMethodPersonPicture;
        this.checkBox_shareMethodPersonCheckBox = checkBox_shareMethodPersonCheckBox;
    }



}
