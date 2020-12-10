import java.io.File;
import java.io.FileNotFoundException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Scanner;
import java.sql.*;
import java.sql.DriverManager;
import java.util.Scanner;

public class MyConnection {
    Connection con;
    public void connect() throws FileNotFoundException {
        String MySQLURL = "jdbc:mysql://localhost:3306/tvShows?useSSL=false";

        //get the current connection
        URL path = MyConnection.class.getResource("login.txt");
        File file = new File(path.getFile());
        Scanner sc = new Scanner(file);
        sc.useDelimiter(" ");

        String databaseUserName = sc.next();
        String databasePassword = sc.next();
        con = null;

        try {

            // open connection
            con = DriverManager.getConnection(MySQLURL, databaseUserName, databasePassword);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void query() {
        ResultSet resultSet = null;
        String query = "";
        ArrayList<String> listTitle = new ArrayList<String>();
        try {
            if (con != null) {
                Statement stmt = con.createStatement();
                query = "SELECT * FROM tvShows.Shows";
                resultSet = stmt.executeQuery(query);

                while (resultSet.next()) {
                    // System.out.println(resultSet.getString("Title"));
                    listTitle.add(resultSet.getString("Title"));
                }

                for ( int i = 0; i < listTitle.size(); i++) {
                    System.out.println(listTitle.get(i));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}