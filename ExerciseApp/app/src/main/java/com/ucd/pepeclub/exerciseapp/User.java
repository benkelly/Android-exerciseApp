package com.ucd.pepeclub.exerciseapp;

/**
 * Created by 14318776 on 02/12/2017.
 */

public class User {

    private String rank, name, points, id;

    public User(String rank, String name, String points, String id) {
        this.rank = rank;
        this.name = name;
        this.points = points;
        this.id = id;
    }

    public String getRank() {
        return rank;
    }

    public void setRank(String rank) {
        this.rank = rank;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPoints() {
        return points;
    }

    public void setPoints(String points) {
        this.points = points;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
