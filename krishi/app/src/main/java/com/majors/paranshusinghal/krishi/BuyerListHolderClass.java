package com.majors.paranshusinghal.krishi;


public class BuyerListHolderClass {

    private int id;
    private String name, date, maxprice, maxvol,maxbid, mybid;

    public BuyerListHolderClass(int id, String date, String name, String maxprice, String maxvol, String maxbid, String mybid) {
        this.id = id;
        this.date = date;
        this.name = name;
        this.maxprice = maxprice;
        this.maxvol = maxvol;
        this.maxbid = maxbid;
        this.mybid = mybid;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDate() {
        return date;
    }

    public String getMaxprice() {
        return maxprice;
    }

    public String getMaxvol() {
        return maxvol;
    }

    public String getMaxbid() {
        return maxbid;
    }

    public String getMybid() {
        return mybid;
    }
}
