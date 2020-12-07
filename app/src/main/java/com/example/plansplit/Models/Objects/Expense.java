package com.example.plansplit.Models.Objects;

public class Expense {

    String expense_name, expense_type;
    int price;

    public Expense(String name, String type, int price){
        this.expense_name = name;
        this.expense_type = type;
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



}
