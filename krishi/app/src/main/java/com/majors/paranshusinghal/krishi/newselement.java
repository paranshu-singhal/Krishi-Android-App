package com.majors.paranshusinghal.krishi;
public class newselement {

    private String title;
    private String description;

    public newselement(String title, String desc) {
        this.title = title;
        this.description = desc;
    }

    public String gettitle(){
        return title;
    }
    public String getdescription(){
        return description;
    }
}
