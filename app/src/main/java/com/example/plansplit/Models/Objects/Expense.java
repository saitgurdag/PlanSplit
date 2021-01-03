package com.example.plansplit.Models.Objects;

public class Expense {

    String name, type, addedBy, date, price, addedById;
    int price_int;

    public Expense(){}

    public Expense(String name, String type, String price) {
        this.name = name;
        this.type = type;
        this.price = price;
    }

    public Expense(String expense_name, String expense_type, String payer, int price) { //Grup Expense için
        this.name = expense_name;
        this.type = expense_type;
        this.addedBy = payer;
        this.price_int = price;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getAddedById() {
        return addedById;
    }

    public void setAddedById(String addedById) {
        this.addedById = addedById;
    }

    public String getPrice_int() {
        return String.valueOf(price_int);
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getAddedBy() {
        return addedBy;
    }

    public void setAddedBy(String addedBy) {
        this.addedBy = addedBy;
    }

    public void setPrice_int(int price_int) {
        this.price_int = price_int;
    }
}
