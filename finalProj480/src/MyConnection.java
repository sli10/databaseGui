import java.sql.*;
import java.sql.DriverManager;

public class MyConnection {
    public static void main(String[] args) {
        String MySQLURL = "jdbc:mysql://localhost:3306/salesSmall?useSSL=false";
        String databseUserName = "root";
        String databasePassword = "Pok3mon4evr!";
        Connection con = null;
//        Statement stmt = con.createStatement();
        ResultSet resultSet = null;
        String query = "";

        try {

            // open connection
            con = DriverManager.getConnection(MySQLURL, databseUserName, databasePassword);
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