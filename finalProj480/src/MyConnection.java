import java.io.File;
import java.io.FileNotFoundException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;
import java.util.Scanner;
import java.sql.*;
import java.sql.DriverManager;

public class MyConnection {
    Connection con;
    int seed = 1;
    String query = "SELECT * FROM tvShows.Shows WHERE ";
    String title, description, genre, IMBD, rottenScore, age, released, seasons, stream;
    String movieTitleLogin = "";
    String userName, password;
    ArrayList<String> titleList = new ArrayList<String>();
    HashMap<String,String> descriptionMap = new HashMap<String,String>();
    HashMap<String,String> genreMap = new HashMap<String,String>();
    HashMap<String,String> IMBDMap = new HashMap<String,String>();
    HashMap<String,String> rottenMap = new HashMap<String,String>();
    HashMap<String,String> ageMap = new HashMap<String,String>();
    HashMap<String,String> releasedMap = new HashMap<String,String>();
    HashMap<String,String> numOfSeasonsMap = new HashMap<String,String>();
    HashMap<String,String> streamMap = new HashMap<String,String>();


    public void connect() throws FileNotFoundException {
        String MySQLURL = "jdbc:mysql://localhost:3306/tvShows?useSSL=false";

        //get the current connection
        URL path = MyConnection.class.getResource("connectInfo.txt");
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

    public void getPlatform(String platform) {
        query += "StreamingPlatform = '";
        query += platform;
        query += "'";
    }

    public void getGenre(String genre) {
        query += " AND Genre LIKE '";
        query += genre;
        query += "%'";
        query += " OR Genre LIKE ";
        query += "'%";
        query += genre;
        query += "'";
    }

    public void getRange(String currRange) {
        String temp = currRange;
        String delims = "-";
        String[] tokens = temp.split(delims);

        String firstNum = tokens[0];
        String secondNum = tokens[1];

        query += " AND ";
        query += firstNum;
        query += " <= IMBDRating AND ";
        query += secondNum;
        query += " >= IMBDRating";
    }

    public void getContent(String content) {
        query += " AND ContentRating = ";
        query += content;
    }

    public int runQuery() {
        ResultSet resultSet = null;
        System.out.println("query: " + query);
        try {
            if (con != null) {
                Statement stmt = con.createStatement();

                resultSet = stmt.executeQuery(query);

                if (resultSet == null) {
                    System.out.println("no show");
                }
                else {
                    while (resultSet.next()) {
                        titleList.add(resultSet.getString("Title"));
                        releasedMap.put(resultSet.getString("Title"),resultSet.getString("Released"));
                        ageMap.put(resultSet.getString("Title"),resultSet.getString("ContentRating"));
                        IMBDMap.put(resultSet.getString("Title"),resultSet.getString("IMBDRating"));
                        rottenMap.put(resultSet.getString("Title"),resultSet.getString("RottenRating"));
                        genreMap.put(resultSet.getString("Title"),resultSet.getString("Genre"));
                        descriptionMap.put(resultSet.getString("Title"),resultSet.getString("Description"));
                        numOfSeasonsMap.put(resultSet.getString("Title"),resultSet.getString("NumOfSeasons"));
                        streamMap.put(resultSet.getString("Title"),resultSet.getString("StreamingPlatform"));
                    }
                }


            }
        } catch (Exception e) {
            return 0;
        }

        return 1;
    }

    public void pickOne() {

        if (releasedMap.size() > 0) {
            Random rand = new Random();
            rand.setSeed(seed);
            int randIndex = rand.nextInt(releasedMap.size() - 1 - 1) + 1;
            title = titleList.get(randIndex);
            released = releasedMap.get(title);
            age = ageMap.get(title);
            IMBD = IMBDMap.get(title);
            rottenScore = rottenMap.get(title);
            genre = genreMap.get(title);
            description = descriptionMap.get(title);
            seasons = numOfSeasonsMap.get(title);
            stream = streamMap.get(title);
            try{
                //check for connection
                if (con != null) {
                    //update table with query
                    Statement stmt = con.createStatement();
                    if (title != null) {
                        movieTitleLogin += title;
                        movieTitleLogin += "\n";
                        System.out.println("username: " + userName);
                        System.out.println("password: " + password);
                        if (lookForUser(userName) == true) {

                            String addMovieQ = "UPDATE tvShows.LogIn SET MovieTitle='" + movieTitleLogin + "' WHERE UserName='" + userName + "' AND Password='" + password +"'";

                            System.out.println("add: " + addMovieQ);
                            stmt.executeUpdate(addMovieQ);
                        }

                    }
                }

            }
            catch(Exception e){
                e.printStackTrace();
            }
        }

    }

    public String getMovieTitleLogin() {
        return movieTitleLogin;
    }

    public String getTitle() {
        return title;
    }

    public String getReleased() {
        return released;
    }

    public String getAge() {
        return age;
    }

    public String getIMBD() {
        return IMBD;
    }

    public String getRotten() {
        return rottenScore;
    }

    public String getGenre() {
        return genre;
    }

    public String getDescription() {
        return description;
    }

    public String getSeasons() {
        return seasons;
    }

    public String getStream() {
        return stream;
    }

    public void clear() {
        query = "SELECT * FROM tvShows.Shows WHERE ";
        titleList.clear();
        releasedMap.clear();
        ageMap.clear();
        IMBDMap.clear();
        rottenMap.clear();
        genreMap.clear();
        descriptionMap.clear();
        numOfSeasonsMap.clear();
        streamMap.clear();
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

    //add log in to table
    public void addLogIn(String user, String pass){
        String currentQ = "";
        try{
            //check for connection
            if (con != null) {
                //update table with query
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

    //looks for user
    public Boolean lookForUser(String user){
        String currentQ = "";
        String currentUser = "";
        try{

            //check to see if there is connection
            if (con != null) {
                Statement stmt = con.createStatement();
                currentQ += "SELECT Username FROM tvShows.LogIn WHERE Username= '" + user + "'";
                ResultSet result = stmt.executeQuery(currentQ);

                //if there is a user then return true if user is found
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

    //checks to see if password and username match
    public Boolean checkPass(String user, String pass){
        String currentQ = "";
        String currentUser = "";
        try{
            //makes sure there's a connection
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
}