package com.gb.globalfamily.Model;

//abstract class. Generalizes Refugee and AidWorker.

public abstract class User {
    String name;
    String nationality;

    //getters and setters
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getNationality() {
        return nationality;
    }
    public void setNationality(String nationality) {
        this.nationality = nationality;
    }

    //class members returned as a human readable string
    @Override
    public String toString() {
        return "User{" +
                "name='" + name + '\'' +
                ", nationality='" + nationality + '\'' +
                '}';
    }


    /**
     * constructor. Sets name and nationality of user
     * @param name name of User
     * @param nationality Nationality of user
     */
    User(String name, String nationality) {

        this.name = name;
        this.nationality = nationality;
    }



}



