package com.testing;
import java.sql.SQLException;
import java.sql.Connection;
import java.sql.Statement;
import java.sql.DriverManager;

/* Ver diferencas entre execute, executeUpdate e executeQuery*/
/* Prepared statement */

// Contains all instructions for DB data manipulation
public class DBFunctions {
    /* Estabelece ligacao 'a base de dados (esquema especifico)  */
    public Connection ConnectToDb(){
        String URL = "jdbc:postgresql://db.fe.up.pt:5432/";
        // String DBNAME = "LaBatata";
        String USER = "pswt0201";
        String PASS = "TURD2022";


        Connection conn = null;
        try {

            Class.forName("org.postgresql.Driver");     // Entender isto
            conn = DriverManager.getConnection(URL , USER, PASS);

            // conn.setAutoCommit(); Se True as transacoes/statements sao feitas individualmente

            // Especificar a Schema (esquema) ao qual ligar (senao assume o esquema publico)
            try {
                Statement stmt = conn.createStatement();
                String sql = "SET SCHEMA 'LaBatata';";
                stmt.execute(sql);
                stmt.close();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }

            if(conn != null){
                System.out.println("Connection established");
            }else{
                System.out.println("Connection failed");
            }

        }catch (Exception e){
            e.printStackTrace();
        }
        return conn;
    }

    /* ---------------------- The following functions represent administrative privileges ----------------------- */
    /*                             (adding members, deleting members, updating members)                           */


    // Adicionar as ligacoes com outros objetos
    /* Sera passado o objeto musics. Devemos pesquisar antes de poder adicionar o objeto                          */
    public Statement newObjectDB(Connection c, String table, String id, String name){
        String column1, column2;
        switch (table) {
            case "musics": column1 = "mid"; column2 = "music_name"; break;
            case "artists": column1 = "artist_id"; column2 = "artist_name"; break;
            case "albuns": column1 = "album_id"; column2 = "album_name"; break;
            default: return null;
        }

        Statement stmt;
        try {

            stmt = c.createStatement();
            String sql = String.format("INSERT INTO %s (%s, %s) VALUES ('%s','%s') RETURNING %s;", table, column1, column2, id, name, column1);

            stmt.execute(sql);

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return stmt;
    }

    // Login

    /* Permite verificacao da existencia de determinados objetos na base de dados                                 */
    public Statement SearchDBTable(Connection c, String TableName){

        Statement stmt;
        try {

            stmt = c.createStatement();
            String sql = String.format("SELECT * FROM %s;", TableName);
            stmt.executeQuery(sql);

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return stmt;
    }

    public Statement deleteMemberDB(Connection c, int identifier){

        Statement stmt;
        try {

            stmt = c.createStatement();
            String sql = String.format("DELETE FROM person WHERE id='%d';", identifier);
            stmt.execute(sql);

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return stmt;
    }


    /* Updates already existing member to the table 'person'. return - object statement.                          */
    public Statement updateMemberDB(Connection c, String nome, String pais, int identifier){

        Statement stmt;
        try {

            stmt = c.createStatement();
            String sql = String.format("UPDATE person SET name='%s', country='%s' WHERE id = %d;", nome, pais, identifier);
            stmt.executeUpdate(sql);

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return stmt;
    }
}

