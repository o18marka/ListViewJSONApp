package com.example.brom.listviewjsonapp;

public class Mountain {

    private String name;
    private String location;
    private int height;

    public Mountain(){
        name="NoName";
        location="NoLocation";
        height=-1;
    }

    public Mountain(String n, String l, int h){
        name=n;
        location=l;
        height=h;
    }

    public String info(){
        String tmp=new String();
        tmp+=name+" ligger i "+location+" och har en höjd på "+height+"m.";
        return tmp;
    }

    public void setName(String n){
        name=n;
    }

    public String getName(){
        return name;
    }

}
