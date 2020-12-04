package com.example.plansplit.Objects;

import com.example.plansplit.R;

public class Friend{
    private int person_image;
    private String name;
    private String amount;
    private int amount_text;
    private int layout_background;
    private int image_background;
    private int color;
    private String key;

    public Friend(int person_image, String name, int amount, String key){
        this.person_image = person_image;
        this.name = name;
        this.key = key;

        //todo: ileride birim eklenirse değişmesi lazım
        this.amount = amount + " TL";

        //fixme: firebasede borç pozitif mi negatif mi karar vermek lazım
        //eğer kullanıcı arkadaşa borçlu ise
        if(amount <= 0){
            this.layout_background = R.drawable.itemview_bg_border_red;
            this.image_background = R.drawable.circle_background_red;
            this.color = R.color.red;
            this.amount_text =  R.string.friend_amount_user_owes;//borçlusun
        }else {
            this.layout_background = R.drawable.itemview_bg_border_green;
            this.image_background = R.drawable.circle_background_green;
            this.color = R.color.brightGreen;
            this.amount_text = R.string.friend_amount_friend_owes;//borçlu
        }
    }

    public int getPerson_image(){
        return person_image;
    }

    public String getName(){
        return name;
    }

    public String getAmount(){
        return amount;
    }

    public int getAmount_text(){
        return amount_text;
    }

    public int getLayout_background(){
        return layout_background;
    }

    public int getImage_background(){
        return image_background;
    }

    public int getColor(){
        return color;
    }
}