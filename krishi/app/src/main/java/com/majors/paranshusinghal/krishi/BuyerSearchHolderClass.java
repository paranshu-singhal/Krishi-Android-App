package com.majors.paranshusinghal.krishi;

public class BuyerSearchHolderClass {

    private String nameCrop, minPrice, minVol, date, address, phone, maxBid;
    private int id;

    public BuyerSearchHolderClass(int id, String nameCrop, String minPrice, String minVol, String date, String address, String phone, String maxBid) {
        this.nameCrop = nameCrop;
        this.minPrice = minPrice;
        this.minVol = minVol;
        this.date = date;
        this.address = address;
        this.phone = phone;
        this.maxBid = maxBid;
        this.id = id;
    }

    public String getMaxBid() {
        return maxBid;
    }

    public int getId() {
        return id;
    }

    public String getNameCrop() {
        return nameCrop;
    }

    public String getPhone() { return phone;}

    public String getMinPrice() {
        return minPrice;
    }

    public String getMinVol() {
        return minVol;
    }

    public String getDate() {
        return date;
    }

    public String getAddress() {
        return address;
    }
}
