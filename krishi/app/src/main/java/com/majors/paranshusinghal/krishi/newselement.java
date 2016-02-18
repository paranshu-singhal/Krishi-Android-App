package com.majors.paranshusinghal.krishi;

import java.io.Serializable;

public class newselement implements Serializable{

    private transient String title;
    private transient String description;
    private transient String link;
    private transient String pubDate;

    public newselement(String title, String desc, String link, String pubDate){
        this.title = title;
        this.description = desc;
        this.link=link;
        this.pubDate=pubDate;
    }

    public String getTitle(){return title;}
    public String getDescription(){return description;}
    public String getLink(){return link;}
    public String getPubDate(){return pubDate;}
}
