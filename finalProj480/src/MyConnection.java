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
    String query2 = "SELECT * FROM tvShows.Shows ORDER BY RAND() LIMIT 1"
    String title, description, genre, IMBD, rottenScore, age, released, seasons, stream;
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
                        numOfSeasonsMap.put(resultSet.getString("Title"),resultSet.getString("NumOfseasons"));
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
        Random rand = new Random();
        rand.setSeed(seed);

        if (releasedMap.size() > 0) {
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
        }


    }

    public String getTitle() {
        System.out.println("title: " + title);
        return title;
    }

    public String getReleased() {
        System.out.println("released: " + released);
        return released;
    }

    public String getAge() {
        System.out.println("age: " + age);
        return age;
    }

    public String getIMBD() {
        System.out.println("imbd: " + IMBD);
        return IMBD;
    }

    public String getRotten() {
        System.out.println("rotten: " + rottenScore);
        return rottenScore;
    }

    public String getGenre() {
        System.out.println("genre: " + genre);
        return genre;
    }

    public String getDescription() {
        System.out.println("description: " + description);
        return description;
    }

    public String getSeasons() {
        System.out.println("seasons: " + seasons);
        return seasons;
    }

    public String getStream() {
        System.out.println("stream: " + stream);
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
}