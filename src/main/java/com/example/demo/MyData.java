package com.example.demo;

public class MyData {

    private String firstName;

    private String secondName;

    private Integer score;


    public MyData(String firstName, String secondName, Integer score) {
        this.firstName = firstName;
        this.secondName = secondName;
        this.score = score;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getSecondName() {
        return secondName;
    }

    public Integer getScore() {
        return score;
    }

    public Object[] toRow(){
        return new Object[]{
                firstName,
                secondName,
                score
        };
    }
}
