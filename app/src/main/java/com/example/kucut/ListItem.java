package com.example.kucut;

public class ListItem {
    private String name;
    private String link;
    private String img;
    public void setName(String name){
        this.name = name;
    }
    public String getName(){
        return name;
    }
    public String getLink(){
        return link;
    }
    public String getImg() {return img;}
    public void setLink(String link){
        this.link = link;
    }
    ListItem(String name, String link, String img){
        this.name = name;
        this.link = link;
        this.img = img;
    }
}