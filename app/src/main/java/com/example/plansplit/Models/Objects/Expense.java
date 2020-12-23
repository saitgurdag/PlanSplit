package com.example.plansplit.Models.Objects;

public class Expense {

    String expense_name, expense_type, payer;
    int price;

    public Expense(String name, String type, int price) {
        this.expense_name = name;
        this.expense_type = type;
        this.price = price;
    }

    public Expense(String expense_name, String expense_type, String payer, int price) { //Grup Expense için
        this.expense_name = expense_name;
        this.expense_type = expense_type;
        this.payer = payer;
        this.price = price;
    }

    public String getPrice() {
        return price + " TL";
    }

    public String getExpense_name() {
        return expense_name;
    }

    public String getExpense_type() {
        return expense_type;
    }

    public void setExpense_name(String expense_name) {
        this.expense_name = expense_name;
    }

    public void setExpense_type(String expense_type) {
        this.expense_type = expense_type;
    }

    public String getPayer() {
        return payer;
    }

    public void setPayer(String payer) {
        this.payer = payer;
    }

    public void setPrice(int price) {
        this.price = price;
    }
}
