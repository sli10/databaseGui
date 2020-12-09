import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import java.sql.*;
import java.sql.DriverManager;
import java.util.Scanner;

public class MyConnection {
    public static void main(String[] args) throws FileNotFoundException {
        String MySQLURL = "jdbc:mysql://localhost:3306/salesSmall?useSSL=false";
        File file = new File("./login.txt");
        Scanner sc = new Scanner(file);
        sc.useDelimiter(sc.next());

        System.out.println();

        String databaseUserName = "root";
        String databasePassword = "Pok3mon4evr!";
        Connection con = null;
//        Statement stmt = con.createStatement();
        ResultSet resultSet = null;
        String query = "";

        try {

            // open connection
            con = DriverManager.getConnection(MySQLURL, databaseUserName, databasePassword);
            if (con != null) {
            	 Statement stmt = con.createStatement();
//                System.out.println("Database connection is successful !!!!");
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