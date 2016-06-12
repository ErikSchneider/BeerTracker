package com.theironyard;

import java.util.ArrayList;

/**
 * Created by Erik on 6/12/16.
 */
public class Drinker {
    String name;
    String password;

    ArrayList<Beer> beers = new ArrayList<>();

    public Drinker(String name, String password) {
        this.name = name;
        this.password = password;
    }
}
