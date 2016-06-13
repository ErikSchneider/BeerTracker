package com.theironyard;

/**
 * Created by Erik on 6/12/16.
 */
public class Beer {
    String name;
    String brewery;
    String type;
    String comment;
    int id;

    public Beer(String name, String brewery, String type, String comment, int id) {
        this.name = name;
        this.brewery = brewery;
        this.type = type;
        this.comment = comment;
        this.id = id;
    }

    public Beer() {

    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBrewery() {
        return brewery;
    }

    public void setBrewery(String brewery) {
        this.brewery = brewery;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
