import java.io.File;
import java.io.FileNotFoundException;
import java.net.URL;
import java.util.Scanner;
import java.sql.*;
import java.sql.DriverManager;

public class MyConnection {
    // Connection object
    Connection con;

    // Query that will be used to find shows with speicifc genre, IMBD score, age range, and stream platform
    String query = "SELECT * FROM tvShows.Shows WHERE ";

    // Strings to hold search results for picking one show
    String title, description, genre, IMBD, rottenScore, age, released, seasons, stream;

    // Strings for log in info
    String titleLogin = "";
    String userName, password;


    // connect function connects to SQL
    public void connect() throws FileNotFoundException {
        // String to hold the localhost info and the schema
        String MySQLURL = "jdbc:mysql://localhost:3306/tvShows?useSSL=false";

        // Get the current connection
        URL path = MyConnection.class.getResource("login.txt");

        // File and Scanner objects
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

    // getPlatform concats query so that it searches where StreamingPlatform matches the platform passed in
    public void getPlatform(String platform) {
        query += "(StreamingPlatform LIKE '";
        query += platform;
        query += "%'";
        query += " OR StreamingPlatform LIKE ";
        query += "'%";
        query += platform;
        query += "')";
    }

    // getGenre concats query so that it searches where Genre holds the genre passed in somewhere in the result
    public void getGenre(String genre) {
        query += " AND (Genre LIKE '";
        query += genre;
        query += "%'";
        query += " OR Genre LIKE ";
        query += "'%";
        query += genre;
        query += "')";
    }

    // getRange concats query so that it searches where the IMBDRating is inbetween the currRange
    public void getRange(String currRange) {
        String temp = currRange;
        String delims = "-";
        String[] tokens = temp.split(delims);

        String firstNum = tokens[0];
        String secondNum = tokens[1];

        query += " \nAND (";
        query += firstNum;
        query += " <= IMBDRating) AND (";
        query += secondNum;
        query += " >= IMBDRating)";
    }

    // getContent concats query so that it searches where ContentRating holds the content passed in
    public void getContent(String content) {
        query += " AND (ContentRating = ";
        query += content;
        query += ")";
    }

    // runQuery runs the query
    public int runQuery() {
        ResultSet resultSet = null;
        // Randomize the result and limit by 1
        query += " ORDER BY RAND() LIMIT 1";

        try {
            // If the connnection is connected
            if (con != null) {
                // Execute the query
                Statement stmt = con.createStatement();
                resultSet = stmt.executeQuery(query);

                // If the result is null
                if (resultSet == null) {
                    System.out.println("no show");
                }

                // If the result returns something
                else {
                    // Loop through storing the resultSet into its respective string
                    while (resultSet.next()) {
                        title = (resultSet.getString("Title"));
                        released = (resultSet.getString("Released"));
                        age = (resultSet.getString("ContentRating"));
                        IMBD = (resultSet.getString("IMBDRating"));
                        rottenScore = (resultSet.getString("RottenRating"));
                        genre = (resultSet.getString("Genre"));
                        description = (resultSet.getString("Description"));
                        seasons = (resultSet.getString("NumOfSeasons"));
                        stream = (resultSet.getString("StreamingPlatform"));
                    }
                }
            }
        } catch (Exception e) {
            return 0; // Failed, return 0
        }
        return 1; // Success, return 1
    }

    // updateAccount updates the shows held under the account
    public void updateAccount() {

        // Update the LogIn table in SQL to hold the show titles
        try{
            // Check for connection
            if (con != null) {
                // Update table with query
                Statement stmt = con.createStatement();

                // If the title is not null, add the show name to the showTitle attribute of the
                // row holding the correct username and password
                if (title != null) {
                    // If the title is not already in the account's show
                    if (strstr(titleLogin, title) == -1) {
                        titleLogin += title;
                        titleLogin += "\n";

                        // userName was found--account exists
                        if (lookForUser(userName) == true) {
                            String addShowQ = "UPDATE tvShows.LogIn SET ShowTitle=\"" + titleLogin + "\" WHERE UserName='" + userName + "' AND Password='" + password +"'";

                            stmt.executeUpdate(addShowQ);
                        }
                    }

                }
            }
        }
        // Failed
        catch(Exception e){
            e.printStackTrace();
        }
    }

    // Gets the title of the show from the LogIn table
    public String getTitleLogin() {
        // Update the LogIn table in SQL to hold the show titles
        try{
            // Check for connection
            if (con != null) {
                // Gets the show name based on a given account
                Statement stmt = con.createStatement();
                String getShowsLoginQ = "SELECT ShowTitle FROM tvShows.LogIn WHERE UserName='" + userName + "' AND Password='" + password +"'";
                ResultSet resultSet = stmt.executeQuery(getShowsLoginQ);
                if (resultSet != null) {
                    // Loop through storing the resultSet
                    while (resultSet.next()) {
                        // Update titleLogin to hold the ShowTitle value returned
                        titleLogin = resultSet.getString("ShowTitle");
                    }
                }

            }
        }
        // Failed
        catch(Exception e){
            e.printStackTrace();
        }
        return titleLogin;
    }

    // Returns the title of the show
    public String getTitle() {
        return title;
    }

    // Returns the release date
    public String getReleased() {
        return released;
    }

    // Returns the age restriction
    public String getAge() {
        return age;
    }

    // Returns the IMBD score
    public String getIMBD() {
        return IMBD;
    }

    // Returns the Rotten Tomato score
    public String getRotten() {
        return rottenScore;
    }

    // Returns the genre
    public String getGenre() {
        return genre;
    }

    // Returns the description
    public String getDescription() {
        return description;
    }

    // Returns the number of seasons
    public String getSeasons() {
        return seasons;
    }

    // Returns the streaming platform
    public String getStream() {
        return stream;
    }

    // Clear function resets the query and data structures for next search
    public void clear() {
        // Reset query
        query = "SELECT * FROM tvShows.Shows WHERE ";

        // Reset the strings
        title = null;
        released = null;
        age = null;
        IMBD = null;
        rottenScore = null;
        genre = null;
        description = null;
        seasons = null;
        stream = null;

    }

    // Add log in info to table
    public void addLogIn(String user, String pass){
        String currentQ = "";
        try{
            // Check for connection
            if (con != null) {
                // Update table with query
                Statement stmt = con.createStatement();
                userName = user;
                password = pass;
                currentQ += "INSERT INTO tvShows.LogIn (UserName, Password)" + " VALUES ('" + user + "', " + "'" + pass + "')";
                stmt.executeUpdate(currentQ);
            }
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }

    // Looks for user
    public Boolean lookForUser(String user){
        String currentQ = "";
        String currentUser = "";
        try{

            // Check to see if there is connection
            if (con != null) {
                Statement stmt = con.createStatement();
                currentQ += "SELECT Username FROM tvShows.LogIn WHERE Username= '" + user + "'";
                ResultSet result = stmt.executeQuery(currentQ);

                // If there is a user then return true if user is found
                while(result.next()){
                    currentUser = result.getString("Username");
                    if(currentUser == null){
                        return false;
                    }
                    else{
                        userName = user;
                        return true;
                    }
                }
            }
        }
        catch(Exception e){
            e.printStackTrace();
        }
        return false;
    }

    // Checks to see if password and username match
    public Boolean checkPass(String user, String pass){
        String currentQ = "";
        String currentUser = "";
        try{
            // Makes sure there's a connection
            if (con != null) {
                Statement stmt = con.createStatement();
                currentQ += "SELECT Username FROM tvShows.LogIn WHERE Username= '" + user + "' AND Password= '" + pass + "'";
                ResultSet result = stmt.executeQuery(currentQ);

                //goes through the result
                while(result.next()){
                    userName = user;
                    password = pass;
                    return true;
                }
            }
        }
        catch(Exception e){
            e.printStackTrace();
        }
        return false;
    }

    // search searches through data to find the information for a show given a title
    public void search(String inTitle) {
        // Clear everything
        clear();

        // Set the resultSet and query
        ResultSet resultSet = null;
        String q = "SELECT * FROM tvShows.shows WHERE Title='" + inTitle + "'";

        try {
            // If the connnection is connected
            if (con != null) {
                // Execute the query
                Statement stmt = con.createStatement();
                resultSet = stmt.executeQuery(q);

                // If the result is null
                if (resultSet == null) {
                    System.out.println("no show");
                }

                // If the result returns something
                else {
                    // Loop through storing the resultSet into its respective string
                    while (resultSet.next()) {
                        title = (resultSet.getString("Title"));
                        released = (resultSet.getString("Released"));
                        age = (resultSet.getString("ContentRating"));
                        IMBD = (resultSet.getString("IMBDRating"));
                        rottenScore = (resultSet.getString("RottenRating"));
                        genre = (resultSet.getString("Genre"));
                        description = (resultSet.getString("Description"));
                        seasons = (resultSet.getString("NumOfSeasons"));
                        stream = (resultSet.getString("StreamingPlatform"));
                    }
                }
            }
        } catch (Exception e) {}
    }

    // Function to implement strstr() function
    public static int strstr(String X, String Y)
    {
        // if X is null or if X's length is less than that of Y's
        if (X == null || Y.length() > X.length()) {
            return -1;
        }

        // if Y is null or is empty
        if (Y == null || Y.length() == 0) {
            return 0;
        }

        for (int i = 0; i <= X.length() - Y.length(); i++)
        {
            int j;
            for (j = 0; j < Y.length(); j++) {
                if (Y.charAt(j) != X.charAt(i + j)) {
                    break;
                }
            }

            if (j == Y.length()) {
                return i;
            }
        }

        return -1;
    }
}