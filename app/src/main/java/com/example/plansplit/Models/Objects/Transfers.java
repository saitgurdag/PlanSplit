package com.example.plansplit.Models.Objects;

import com.example.plansplit.R;

public class Transfers {   //bu clasın amacı kişiler arasındaki para alışverişi gruba item ekleyince paragirdisiyle borç borçlu durumu belirlenip hesaplanması
    //item_groupevents_object icin

    private int type;
    private String groupEvents_object_objectName;
    private String groupEvents_object_payerName;
    private String groupEvents_object_payAmounth;
    private String groupEvents_object_depthAmount;
    private int groupEvents_object_depthStatus;
    private int groupEvents_object_image;
    private int object_payOrPayed;
    private int color;

    //item_groupevents_payments için

    private String groupEvents_payments_payerName;
    private String groupEvents_payments_payedName;
    private String groupEvents_payments_payAmount;
    private int groupEvents_payment_money_image;
    private int groupEvents_payment_payOrpayed;

    public Transfers(int type, int groupEvents_object_image, String groupEvents_object_objectName, String groupEvents_object_payerName, String groupEvents_object_payAmounth, String groupEvents_object_depthAmount) {
        this.type = type;
        this.groupEvents_object_image = groupEvents_object_image;
        this.groupEvents_object_objectName = groupEvents_object_objectName;
        this.groupEvents_object_payerName = groupEvents_object_payerName;
        this.groupEvents_object_payAmounth = groupEvents_object_payAmounth;
        this.groupEvents_object_depthAmount = groupEvents_object_depthAmount;

        //eğer kullanıcı değilde beaşka birisi item eklediyse kullanıcı borçlu olur mantığı burda equals sen olayı yaptım ama ilerde user id == ise şeklinde yapılabilir
        if (!groupEvents_object_payerName.equals("Sen")) {       //equals kullanmak burda yanlış olabilir
            this.color = R.color.red;
            this.groupEvents_object_depthStatus = R.string.group_events_object_depth_status_owes;  //Borçlusun
            this.object_payOrPayed = R.string.group_events_object_payOrpayed_payed;  //ödeyen Sen yani ben değilsem paranın sağında ödedi yazmalı ama solunda TL YAZIYO
        } else {
            this.color = R.color.green;
            this.groupEvents_object_depthStatus = R.string.group_events_object_depth_status_claimant; //Alacaklısın
            this.object_payOrPayed = R.string.group_events_object_payOrpayed_pay;//ödeyen Sen yani bensem değilsem paranın sağında ödedin yazmalı ama solunda TL YAZIYO

        }
    }
    public Transfers(int type, int groupEvents_payment_money_image, String groupEvents_payments_payerName, String groupEvents_payments_payedName, String groupEvents_payments_payAmount) {
        this.type = type;
        this.groupEvents_payment_money_image = groupEvents_payment_money_image;
        this.groupEvents_payments_payerName = groupEvents_payments_payerName;
        this.groupEvents_payments_payedName = groupEvents_payments_payedName;
        this.groupEvents_payments_payAmount = groupEvents_payments_payAmount;

        if (!groupEvents_payments_payerName.equals("Sen")) {
            this.groupEvents_payment_payOrpayed = R.string.group_events_object_payOrpayed_payed;
        } else {
            this.groupEvents_payment_payOrpayed = R.string.group_events_object_payOrpayed_pay;
        }
    }

    public int getGroupEvents_payment_payOrpayed() {
        return groupEvents_payment_payOrpayed;
    }

    public int getObject_payOrPayed() {
        return object_payOrPayed;
    }

    public int getGroupEvents_object_image() {
        return groupEvents_object_image;
    }

    public int getColor() {
        return color;
    }

    public int getGroupEvents_payment_money_image() {
        return groupEvents_payment_money_image;
    }

    public int getType() {
        return type;
    }

    public String getGroupEvents_object_objectName() {
        return groupEvents_object_objectName;
    }

    public String getGroupEvents_object_payerName() {
        return groupEvents_object_payerName;
    }

    public String getGroupEvents_object_payAmounth() {
        return groupEvents_object_payAmounth;
    }

    public String getGroupEvents_object_depthAmount() {
        return groupEvents_object_depthAmount;
    }

    public int getGroupEvents_object_depthStatus() {
        return groupEvents_object_depthStatus;
    }

    public String getGroupEvents_payments_payerName() {
        return groupEvents_payments_payerName;
    }

    public String getGroupEvents_payments_payedName() {
        return groupEvents_payments_payedName;
    }

    public String getGroupEvents_payments_payAmount() {
        return groupEvents_payments_payAmount;
    }
}
