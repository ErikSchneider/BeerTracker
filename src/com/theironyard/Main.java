package com.theironyard;

import spark.ModelAndView;
import spark.Session;
import spark.Spark;
import spark.template.mustache.MustacheTemplateEngine;

import java.util.ArrayList;
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
                        ArrayList<Beer> beerList = drinkers.get(username).beers;
                        m.put("username", username);
                        m.put("beers", beerList);
                        return new ModelAndView(m, "home.html");
                    }
                },
                new MustacheTemplateEngine()
        );
        Spark.post(
                "/login",
                (request, response) -> {
                    String username = request.queryParams("username");
                    String password = request.queryParams("password");
                    if (username == null || password == null) {
                        throw new Exception("Username or password not found");
                    }

                    Drinker drinker = drinkers.get(username);
                    if (drinker == null) {
                        drinker = new Drinker(username, password);
                        drinkers.put(username, drinker);
                    }
                    else if (!password.equals(drinker.password)){
                        Spark.halt("Incorrect password ");
                    }

                    Session session = request.session();
                    session.attribute("username", username);

                    response.redirect("/");
                    return "";
                }
        );
        Spark.post(
                "/create-beer",
                (request, response) -> {
                    Session session = request.session();
                    String username = session.attribute("username");
                    Drinker drinker = drinkers.get(username);

                    Beer beer = new Beer();
                    beer.setBrewery(request.queryParams("brewery"));
                    beer.setName(request.queryParams("name"));
                    beer.setType(request.queryParams("type"));
                    beer.setComment(request.queryParams("comment"));
                    beer.setId(drinker.beers.size());
                    drinker.beers.add(beer);

                    response.redirect("/");
                    return "";

                }
        );
        Spark.get(
                "/edit-beer",
                (request, response) -> {

                    Session session = request.session();
                    String username = session.attribute("username");

                    Drinker drinker = drinkers.get(username);

                    int id = (Integer.valueOf(request.queryParams("id")));
                    HashMap m2 = new HashMap();
                    Beer beer = drinker.beers.get(id);
                    m2.put("beer", beer);

                    return new ModelAndView(m2, "update.html");
                },
                new MustacheTemplateEngine()
        );
        Spark.post(
                "/update-beer",
                (request, response) -> {
                    Session session = request.session();
                    String username = session.attribute("username");
                    Drinker drinker = drinkers.get(username);

                    int id = Integer.valueOf(request.queryParams("id"));
                    Beer beer = drinker.beers.get(id);
                    beer.setBrewery(request.queryParams("newBrewery"));
                    beer.setName(request.queryParams("newName"));
                    beer.setType(request.queryParams("newType"));
                    beer.setComment(request.queryParams("newComment"));
                    response.redirect("/");
                    return "";
                }
        );
        Spark.post(
                "/delete-beer",
                (request, response) -> {
                    Session session = request.session();
                    String username = session.attribute("username");
                    Drinker drinker = drinkers.get(username);
                    if (username == null) {
                        throw new Exception("Not logged in");
                    }
                    int id = Integer.valueOf(request.queryParams("id"));

                    drinker.beers.remove(id);

                    int index = 0;
                    for (Beer beer : drinker.beers) {
                        beer.id = index;
                        index++;
                    }
                    response.redirect("/");
                    return "";

                }

        );

        Spark.post(
                "/logout",
                (request, response) -> {
                    Session session = request.session();
                    session.invalidate();
                    response.redirect("/");
                    return "";
                }
        );

    }
}
