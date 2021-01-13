package com.langsun.job.pojo;

public class MyTest {
    private String type;
    private String keyWord;
    private String state;

    @Override
    public String toString() {
        return "MyTest{" +
                "type='" + type + '\'' +
                ", keyWord='" + keyWord + '\'' +
                ", state='" + state + '\'' +
                '}';
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getKeyWord() {
        return keyWord;
    }

    public void setKeyWord(String keyWord) {
        this.keyWord = keyWord;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }
}
