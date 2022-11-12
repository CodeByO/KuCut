package com.example.kucut;

public class ListItem {
    private String name;
    private String link;

    public void setName(String name){
        this.name = name;
    }
    public String getName(){
        return name;
    }
    public String getLink(){
        return link;
    }
    public void setLink(String link){
        this.link = link;
    }
    ListItem(String name, String link){
        this.name = name;
        this.link = link;
    }
}