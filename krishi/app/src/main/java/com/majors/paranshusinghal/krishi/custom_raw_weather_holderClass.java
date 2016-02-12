package com.majors.paranshusinghal.krishi;

import java.util.Date;

public class custom_raw_weather_holderClass {

    private Double maxTemp, minTemp, humidity, clouds, speed;
    private String weather;
    private Date date;

    public custom_raw_weather_holderClass(Double maxTemp,Double minTemp,Double humidity,Double clouds,Double speed, String weather, Date date){
        this.clouds=clouds;
        this.maxTemp=maxTemp;
        this.minTemp=minTemp;
        this.humidity=humidity;
        this.speed=speed;
        this.weather=weather;
        this.date=date;
    }

    public Double getClouds(){return clouds;}
    public Double getMaxTemp(){
        return (maxTemp-273);
    }
    public Double getMinTemp(){return (minTemp-273);}
    public Double getHumidity(){
        return humidity;
    }
    public Double getSpeed(){
        return speed;
    }
    public String getWeather(){
        return weather;
    }
    public Date getDate(){return date;}

}
