package com.theironyard;

import java.util.ArrayList;

/**
 * Created by Erik on 6/12/16.
 */
public class Drinker {
    String username;
    String password;

    ArrayList<Beer> beers = new ArrayList<>();

    public Drinker(String username, String password) {
        this.username = username;
        this.password = password;
    }
}
