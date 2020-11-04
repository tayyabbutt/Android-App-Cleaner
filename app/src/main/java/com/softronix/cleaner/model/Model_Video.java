package com.softronix.cleaner.model;


public class Model_Video {
    String str_path, str_thumb,duration;
    boolean boolean_selected;
    String aName;
    String aAlbum;
    String aArtist;

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getaName() {
        return aName;
    }

    public void setaName(String aName) {
        this.aName = aName;
    }

    public String getaAlbum() {
        return aAlbum;
    }

    public void setaAlbum(String aAlbum) {
        this.aAlbum = aAlbum;
    }

    public String getaArtist() {
        return aArtist;
    }

    public void setaArtist(String aArtist) {
        this.aArtist = aArtist;
    }

    public String getStr_path() {
        return str_path;
    }

    public void setStr_path(String str_path) {
        this.str_path = str_path;
    }

    public String getStr_thumb() {
        return str_thumb;
    }

    public void setStr_thumb(String str_thumb) {
        this.str_thumb = str_thumb;
    }

    public boolean isBoolean_selected() {
        return boolean_selected;
    }

    public void setBoolean_selected(boolean boolean_selected) {
        this.boolean_selected = boolean_selected;
    }
}
