package com.softronix.cleaner.model;

import java.io.Serializable;
import java.util.ArrayList;

public class Model_images implements Serializable {
    String str_folder;
    ArrayList<String> al_imagepath;
    boolean isSelected;

    public String getStr_folder() {
        return str_folder;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public void setStr_folder(String str_folder) {
        this.str_folder = str_folder;
    }

    public ArrayList<String> getAl_imagepath() {
        return al_imagepath;
    }

    public void setAl_imagepath(ArrayList<String> al_imagepath) {
        this.al_imagepath = al_imagepath;
    }
}
