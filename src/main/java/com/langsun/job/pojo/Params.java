package com.langsun.job.pojo;

import java.io.Serializable;

public class Params implements Serializable {
    private String location;
    private Integer page;
    private String rate;
    private String keyWord;

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public Integer getPage() {
        return page;
    }

    public void setPage(Integer page) {
        this.page = page;
    }

    public String getRate() {
        return rate;
    }

    public void setRate(String rate) {
        this.rate = rate;
    }

    public String getKeyWord() {
        return keyWord;
    }

    public void setKeyWord(String keyWord) {
        this.keyWord = keyWord;
    }

    @Override
    public String toString() {
        return "Params{" +
                "location='" + location + '\'' +
                ", page=" + page +
                ", rate='" + rate + '\'' +
                ", keyWord='" + keyWord + '\'' +
                '}';
    }
}
