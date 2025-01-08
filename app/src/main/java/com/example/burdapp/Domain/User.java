package com.example.burdapp.Domain;

public class User {

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }


    public User(){
        this.name =  name;
    }
    public  String getName(){
        return name;
    }
    public void setName(String name){
        this.name = name;
    }


    public String getMale() {
        return male;
    }

    public void setMale(String male) {
        this.male = male;
    }

    private String male;

    public String getFemale() {
        return female;
    }

    public void setFemale(String female) {
        this.female = female;
    }

    private String female;
    private String name;
    private String gender;
}
