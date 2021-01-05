package com.example.plansplit.Models.Objects;

public class Person {
    private String key;
    private String image;
    private long last_login;
    private String name;
    private String mail;
    private String person_id;
    private int cardView_shareMethodPersonPicture;
    private int checkBox_shareMethodPersonCheckBox;
    private String person_photo;
    private String total_expense = "0";
    private int backGroundPerson ;

    public Person(String person_id, String person_photo, String name) {           // Group Operations için kullandığım Contructor
        this.person_id = person_id;
        this.person_photo = person_photo;
        this.name = name;
    }

    public Person(String key, String name, String email, String image, long date){
        this.key = key;
        this.name = name;
        this.mail = email;
        this.image = image;
        this.last_login = date;
    }

    public Person(String name, int cardView_shareMethodPersonPicture, int checkBox_shareMethodPersonCheckBox){
        this.name = name;
        this.cardView_shareMethodPersonPicture = cardView_shareMethodPersonPicture;
        this.checkBox_shareMethodPersonCheckBox = checkBox_shareMethodPersonCheckBox;
    }

    public String getPerson_id() {
        return person_id;
    }

    public String getKey() {
        return key;
    }

    public String getImage() {
        return image;
    }

    public long getLastLogin() {
        return last_login;
    }

    public void setPerson_id(String person_id) {
        this.person_id = person_id;
    }

    public String getPerson_photo() {
        return person_photo;
    }

    public void setPerson_photo(String person_photo) {
        this.person_photo = person_photo;
    }

    public String getTotal_expense() {
        return total_expense;
    }

    public void setTotal_expense(String total_expense) {
        this.total_expense = total_expense;
    }

    public int getBackGroundPerson() {
        return backGroundPerson;
    }

    public void setBackGroundPerson(int backGroundPerson) {
        this.backGroundPerson = backGroundPerson;
    }

    public String getName() {
        return name;
    }

    public int getCardView_shareMethodPersonPicture() {
        return cardView_shareMethodPersonPicture;
    }

    public int getCheckBox_shareMethodPersonCheckBox() {
        return checkBox_shareMethodPersonCheckBox;
    }

    public String getMail() {
        return mail;
    }
}
