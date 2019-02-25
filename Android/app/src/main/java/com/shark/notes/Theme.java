package com.shark.notes;

public class Theme {

    private int theme_id;
    private String name;
    private String primaryColour;
    private String secondaryColour;
    private String textColour;
    private String hintColour;
    private String accentColour;
    private String buttonColour;

    public Theme() {
        theme_id = 0;
        primaryColour = "";
        secondaryColour = "";
        textColour = "";
        hintColour = "";
        accentColour = "";
        buttonColour = "";
    }

    public int getTheme_id() {
        return theme_id;
    }

    public void setTheme_id(int theme_id) {
        this.theme_id = theme_id;
    }

    public String getPrimaryColour() {
        return primaryColour;
    }

    public void setPrimaryColour(String primaryColour) {
        this.primaryColour = primaryColour;
    }

    public String getSecondaryColour() {
        return secondaryColour;
    }

    public void setSecondaryColour(String secondaryColour) {
        this.secondaryColour = secondaryColour;
    }

    public String getTextColour() {
        return textColour;
    }

    public void setTextColour(String textColour) {
        this.textColour = textColour;
    }

    public String getHintColour() {
        return hintColour;
    }

    public void setHintColour(String hintColour) {
        this.hintColour = hintColour;
    }

    public String getAccentColour() {
        return accentColour;
    }

    public void setAccentColour(String accentColour) {
        this.accentColour = accentColour;
    }

    public String getButtonColour() {
        return buttonColour;
    }

    public void setButtonColour(String buttonColour) {
        this.buttonColour = buttonColour;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}

