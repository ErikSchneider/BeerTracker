package com.theironyard;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by Erik on 6/14/16.
 */
public class MainTest {
    public Connection startConnection() throws SQLException {
        Connection conn = DriverManager.getConnection("jdbc:h2:mem:test");
        Main.createTables(conn);
        return conn;
    }

    @Test
    public void testDrinker() throws SQLException {
        Connection conn = startConnection();
        Main.insertDrinker(conn, "Erik", "");
        Drinker drinker = Main.selectDrinker(conn, "Erik");
        conn.close();
        assertTrue(drinker != null);
    }

    @Test
    public void testBeer() throws SQLException {
        Connection conn = startConnection();
        Main.insertDrinker(conn, "Erik", "");
        Drinker drinker = Main.selectDrinker(conn, "Erik");
        Main.insertBeer(conn,"Highlife", "Miller", "Lager", "Cheap", 1);
        Beer beer = Main.selectBeer(conn, 1);
        conn.close();
        assertTrue(drinker != null);
        assertTrue(beer.name.equals("Highlife"));

    }

    @Test
    public void testUpdate() throws SQLException {
        Connection conn = startConnection();

    }

}