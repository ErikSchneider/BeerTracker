package com.theironyard;

import org.h2.engine.User;
import org.h2.tools.Server;
import spark.ModelAndView;
import spark.Session;
import spark.Spark;
import spark.template.mustache.MustacheTemplateEngine;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;

public class Main {

    public static void createTables(Connection conn) throws SQLException {
        Statement stmt = conn.createStatement();
        stmt.execute("CREATE TABLE IF NOT EXISTS drinkers (id IDENTITY, username VARCHAR, password VARCHAR)");
        stmt.execute("CREATE TABLE IF NOT EXISTS beers (id IDENTITY, name VARCHAR, brewery VARCHAR, type VARCHAR, comment VARCHAR, drinker_id INT)");

    }

    public static void insertDrinker (Connection conn, String username, String password) throws SQLException {
        PreparedStatement stmt = conn.prepareStatement("INSERT INTO drinkers VALUES (NULL, ?, ?)");
        stmt.setString(1, username);
        stmt.setString(2, password);
        stmt.execute();
    }

    public static Drinker selectDrinker (Connection conn, String username) throws SQLException {
        PreparedStatement stmt = conn.prepareStatement("SELECT * FROM drinkers WHERE username = ?");
        stmt.setString(1, username);
        ResultSet results = stmt.executeQuery();
        if (results.next()) {
            int id = results.getInt("id");
            String password = results.getString("password");
            return new Drinker(id, username, password);
        }
        return null;
    }

    public static void insertBeer (Connection conn, String name, String brewery, String type, String comment, int drinkerId) throws SQLException {
        PreparedStatement stmt = conn.prepareStatement("INSERT INTO beers VALUES (NULL, ?, ?, ?, ?, ?)");
        stmt.setString(1, name);
        stmt.setString(2, brewery);
        stmt.setString(3, type);
        stmt.setString(4, comment);
        stmt.setInt(5, drinkerId);
        stmt.execute();
    }

    public static Beer selectBeer (Connection conn, int id) throws SQLException {
        PreparedStatement stmt = conn.prepareStatement("SELECT * FROM beers WHERE id = ?");
        stmt.setInt(1, id);
        ResultSet results = stmt.executeQuery();
        if (results.next()) {
            String name = results.getString("name");
            String brewery = results.getString("brewery");
            String type = results.getString("type");
            String comment = results.getString("comment");
            return new Beer(name, brewery, type, comment, id);

        }
        return null;
    }

    public static ArrayList<Beer> selectBeers(Connection conn, int drinkerId) throws SQLException {
        PreparedStatement stmt = conn.prepareStatement("SELECT * FROM beers INNER JOIN drinkers ON beers.drinker_id = drinkers.id WHERE drinkers.id = ?");
        stmt.setInt(1, drinkerId);
        ResultSet results = stmt.executeQuery();
        ArrayList<Beer> beers = new ArrayList<>();
        while (results.next()) {
            int id = results.getInt("id");
            String name = results.getString("name");
            String brewery = results.getString("brewery");
            String type = results.getString("type");
            String comment = results.getString("comment");
            Beer beer = new Beer(name, brewery, type, comment, id);
            beers.add(beer);
        }
        return beers;
    }

    public static void updateBeer(Connection conn, String name, String brewery, String type, String comment, int id) throws SQLException {
        PreparedStatement stmt = conn.prepareStatement("UPDATE beers SET name = ?, brewery = ?, type = ?, comment = ? WHERE id = ?");
        stmt.setString(1, name);
        stmt.setString(2, brewery);
        stmt.setString(3, type);
        stmt.setString(4, comment);
        stmt.setInt(5, id);
        stmt.execute();
    }

    public static void deleteBeer(Connection conn, int id) throws SQLException {
        PreparedStatement stmt = conn.prepareStatement("DELETE FROM beers WHERE id = ?");
        stmt.setInt(1, id);
        stmt.execute();
    }



//    static HashMap<String, Drinker> drinkers = new HashMap<>();

    public static void main(String[] args) throws SQLException {
        Server.createWebServer().start();
        Connection conn = DriverManager.getConnection("jdbc:h2:./main");
        createTables(conn);

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
                        Drinker drinker = selectDrinker(conn, username);
                        m.put("beers", selectBeers(conn, drinker.id));
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

                    Drinker drinker = selectDrinker(conn, username);
                    if (drinker == null) {
                        //drinker = new Drinker(username, password);
                        insertDrinker(conn, username, password);
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

                    String name = request.queryParams("name");
                    String brewery = request.queryParams("brewery");
                    String type = request.queryParams("type");
                    String comment = request.queryParams("comment");

                    Drinker drinker = selectDrinker(conn, username);

                    insertBeer(conn, name, brewery, type, comment, drinker.id);

//                    Drinker drinker = drinkers.get(username);
//                    Beer beer = new Beer();
//                    beer.setBrewery(request.queryParams("brewery"));
//                    beer.setName(request.queryParams("name"));
//                    beer.setType(request.queryParams("type"));
//                    beer.setComment(request.queryParams("comment"));
//                    beer.setId(drinker.beers.size());
//                    drinker.beers.add(beer);

                    response.redirect("/");
                    return "";

                }
        );
        Spark.get(
                "/edit-beer",
                (request, response) -> {

//                    Session session = request.session();
//                    String username = session.attribute("username");
//
//                    Drinker drinker = drinkers.get(username);

                    int id = (Integer.valueOf(request.queryParams("id")));
                    HashMap m2 = new HashMap();
                    Beer beer = selectBeer(conn, id);
                    m2.put("beer", beer);

                    return new ModelAndView(m2, "update.html");
                },
                new MustacheTemplateEngine()
        );
        Spark.post(
                "/update-beer",
                (request, response) -> {
//                    Session session = request.session();
//                    String username = session.attribute("username");
//                    Drinker drinker = drinkers.get(username);

                    int id = Integer.valueOf(request.queryParams("id"));
                    String brewery = request.queryParams("newBrewery");
                    String name = request.queryParams("newName");
                    String type = request.queryParams("newType");
                    String comment =request.queryParams("newComment");
                    updateBeer(conn, name, brewery, type, comment, id);
                    response.redirect("/");
                    return "";
                }
        );
        Spark.post(
                "/delete-beer",
                (request, response) -> {
                    Session session = request.session();
                    String username = session.attribute("username");
//                    Drinker drinker = drinkers.get(username);
                    if (username == null) {
                        throw new Exception("Not logged in");
                    }
                    int id = Integer.valueOf(request.queryParams("id"));

//                    drinker.beers.remove(id);

//                    int index = 0;
//                    for (Beer beer : drinker.beers) {
//                        beer.id = index;
//                        index++;
//                    }
                    deleteBeer(conn, id);
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
