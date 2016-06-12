package com.theironyard;

import spark.ModelAndView;
import spark.Session;
import spark.Spark;
import spark.template.mustache.MustacheTemplateEngine;

import java.util.HashMap;

public class Main {

    static HashMap<String, Drinker> drinkers = new HashMap<>();

    public static void main(String[] args) {

        Spark.init();
        Spark.get(
                "/",
                (request, response) -> {
                    Session session = request.session();
                    String username = session.attribute("username");

                    HashMap m = new HashMap();
                    if (username == null){
                        return new ModelAndView(m, "login.html");
                    }
                    else {
                        Drinker drinker = drinkers.get(username);
                        m.put("restaurants", drinker.beers);
                        return new ModelAndView(m, "home.html");
                    }
                },
                new MustacheTemplateEngine()
        );

    }
}
