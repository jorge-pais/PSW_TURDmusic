package com.testing;
import java.sql.SQLException;
import java.sql.Connection;
import java.sql.Statement;
import java.sql.ResultSet;

//Should add the driver for project File->ProjectStructure->Dependencies->Add(JARs or Directories)
public class postgresql{
    public static void main(String[] args) throws SQLException {
        // Connection to database
        DBFunctions db = new DBFunctions();
        Connection conn = db.ConnectToDb();


        /* Testes com sucesso para uma determinada ligacao */
        /*
        Statement stmt = db.newMusicDB(conn, 56, 65);
        // Accesses the result
        int last_inserted_person_id = -1;
        ResultSet last_inserted_person = stmt.getResultSet();
        if(last_inserted_person.next()) {
            last_inserted_person_id = last_inserted_person.getInt(1);
        }
        System.out.println(last_inserted_person_id);
        */

        /* Statement stmt = db.updateMemberDB(conn, "Raquel", "EUA", 4); */
        /* Statement stmt = db.deleteMemberDB(conn, 5); */

        /*
        // Teste de pesquisa
        Statement stmt = db.SearchDBTable(conn, "musics");
        ResultSet rs = stmt.getResultSet();
        while(rs.next()) {
            System.out.println(rs.getString("mid")+ "\t" + rs.getString("music_name"));
        }
        */


        try {
            //stmt.close();
            conn.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
