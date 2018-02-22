package com.loopbots.reversevideo.change_language.models;

/**
 * Created by shashi on 4/25/2016.
 */
public class All_Language {
    String name, alpha2, is_selected;
    Integer image;

    public All_Language(String unit_name, Integer unit_image, String alpha2, String is_selected) {
        this.name = unit_name;
        this.image = unit_image;
        this.alpha2 = alpha2;
        this.is_selected = is_selected;
    }

    public String getName() {
        return this.name;
    }

    public Integer getImage() {
        return this.image;
    }

    public String getAlpha2() {
        return this.alpha2;
    }

    public String getIs_selected() {
        return is_selected;
    }

    public void setIs_selected(String is_selected) {
        this.is_selected = is_selected;
    }
}