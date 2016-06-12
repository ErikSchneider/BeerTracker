package com.theironyard;

/**
 * Created by Erik on 6/12/16.
 */
public class Beer {
    String name;
    String brewery;
    String type;
    int rating;
    String comments;

    public Beer(String name, String brewery, String type, int rating, String comments) {
        this.name = name;
        this.brewery = brewery;
        this.type = type;
        this.rating = rating;
        this.comments = comments;
    }
}
