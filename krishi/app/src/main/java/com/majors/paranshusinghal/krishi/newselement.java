package com.majors.paranshusinghal.krishi;
public class newselement {

    private String title;
    private String description;
    private String link;
    private String pubDate;

    public newselement(String title, String desc, String link, String pubDate) {
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
