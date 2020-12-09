import java.io.File;
import java.io.FileNotFoundException;
import java.net.URL;
import java.util.Scanner;
import java.sql.*;
import java.sql.DriverManager;
import java.util.Scanner;

public class MyConnection {
    public static void main(String[] args) throws FileNotFoundException {
        String MySQLURL = "jdbc:mysql://localhost:3306/salesSmall?useSSL=false";

        //get the current connection
        URL path = MyConnection.class.getResource("login.txt");
        File file = new File(path.getFile());
        Scanner sc = new Scanner(file);
        sc.useDelimiter(" ");

        String databaseUserName = sc.next();
        String databasePassword = sc.next();
        Connection con = null;
//        Statement stmt = con.createStatement();
        ResultSet resultSet = null;
        String query = "";

        try {

            // open connection
            con = DriverManager.getConnection(MySQLURL, databaseUserName, databasePassword);
            if (con != null) {
                Statement stmt = con.createStatement();
                query = "SELECT P_CODE FROM salesSmall.PRODUCT";
                resultSet = stmt.executeQuery(query);

                while (resultSet.next()) {
                    System.out.println(resultSet.getString("P_CODE"));

                }



            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}