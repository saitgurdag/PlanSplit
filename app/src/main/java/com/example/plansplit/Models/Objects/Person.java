package com.example.plansplit.Models.Objects;

public class Person {

    private String name;
    private String mail;
    private int cardView_shareMethodPersonPicture;
    private int checkBox_shareMethodPersonCheckBox;
    private int cardView_addgroupsPicture;

    /////////// BERKAY EKLEME KISMI ////////////

    // bu ekleme yeri grup işlemleri ekranı içinyapıldı
    //Resim olarak hazır attribute cardView_shareMethodPersonPicture kullandım kişi resmi ile eşit çünki bu , groupDepth ekledim =  kişinin gruba olan borcu

    private String groupDepth;
    private int backGroundPerson ;

    public Person(int backGroundPerson,int cardView_shareMethodPersonPicture, String groupDepth) {           // Group Operations için kullandığım Contructor
        this.backGroundPerson=backGroundPerson;
        this.cardView_shareMethodPersonPicture = cardView_shareMethodPersonPicture;
        this.groupDepth = groupDepth;
    }

    public String getGroupDepth() {
        return groupDepth;
    }

    public void setGroupDepth(String groupDepth) {
        this.groupDepth = groupDepth;
    }
    public int getBackGroundPerson() {
        return backGroundPerson;
    }

    public void setBackGroundPerson(int backGroundPerson) {
        this.backGroundPerson = backGroundPerson;
    }

    ///////////// BERKAY EKLEME KISMI BİTİŞİ /////////////

    public String getName() {
        return name;
    }

    public int getCardView_shareMethodPersonPicture() {
        return cardView_shareMethodPersonPicture;
    }

    public int getCheckBox_shareMethodPersonCheckBox() {
        return checkBox_shareMethodPersonCheckBox;
    }

    public Person(String name) {
        this.name = name;
    }

    public Person(String name, int cardView_shareMethodPersonPicture, int checkBox_shareMethodPersonCheckBox){
        this.name = name;
        this.cardView_shareMethodPersonPicture = cardView_shareMethodPersonPicture;
        this.checkBox_shareMethodPersonCheckBox = checkBox_shareMethodPersonCheckBox;
    }

    public Person(String name,String mail , int cardView_addgroupsPicture){
        this.name=name;
        this.mail=mail;
        this.cardView_addgroupsPicture=cardView_addgroupsPicture;
    }

    public String getMail() {
        return mail;
    }

    public int getCardView_addgroupsPicture() {
        return cardView_addgroupsPicture;
    }



}
