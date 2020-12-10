import java.io.FileNotFoundException;
import java.util.HashMap;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;

public class Gui extends Application {
    // Map that holds the scenes of the application
    HashMap<String, Scene> sceneMap;

    // Buttons to continue through the application
    Button start, logIn, findShows, cont, back;

    // Comboboxes to make selections
    ComboBox<String> genresList, ratingsRange, platformsList, ageRanges;

    // Textfields to enter user name and password
    TextField userName, password;

    // String to update the texts
    String holdLogInStmt, chosenGenre, chosenAge, chosenPlatform, chosenRating;

    // Texts to display the results
    Text title, description, genre, IMBD, rottenScore, age, released, seasons, stream;
    Text logInErrorStmt;

    // VBox to hold shows for a specific account
    VBox listOfShows;

    // Menu bar
    MenuBar menuBar;
    Menu menu;
    MenuItem myShows;

    // MyConnection object called connect
    MyConnection connect = new MyConnection();

    public static void main(String[] args) {
        // TODO Auto-generated method stub
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        // TODO Auto-generated method stub
        primaryStage.setTitle("Look for TV shows to watch");

        // Initialize the HashMap
        sceneMap = new HashMap<String,Scene>();

        // Show the primaryStage
        primaryStage.show();

        // Buttons: logIn, start, findShows, cont, back
        start = new Button("START LOOKING");
        start.setStyle("-fx-background-color: SNOW;");
        logIn = new Button("LOG IN");
        logIn.setStyle("-fx-background-color: SNOW;");
        findShows = new Button("FIND SHOWS");
        findShows.setStyle("-fx-background-color: SNOW;");
        cont = new Button("CONTINUE");
        cont.setStyle("-fx-background-color: SNOW;");
        back = new Button("BACK");

        // Menu
        menuBar = new MenuBar();
        menu = new Menu("MENU");
        myShows = new MenuItem("MY SHOWS");
        menu.getItems().addAll(myShows);
        menuBar.getMenus().add(menu);
        menuBar.setStyle("-fx-background-color: FLORALWHITE;");

        // Change scene to start
        start.setOnAction(e -> {

            // Set to the login scene
            primaryStage.setScene(sceneMap.get("logIn"));

            // Try to connect to SQL
            try {
                connect.connect();
            } catch (FileNotFoundException fileNotFoundException) {
                fileNotFoundException.printStackTrace();
            }
        });

        // After login is clicked
        logIn.setOnAction(e -> {
                    // Get the inputs of user and pass
                    String userInput = userName.getText();
                    String passInput = password.getText();

                    //Look for user and password
                    Boolean foundUser = connect.lookForUser(userInput);
                    Boolean correctPass = connect.checkPass(userInput, passInput);

                    // If one of the inputs are empty then print a empty statement
                    if(passInput.equals("") || userInput.equals("")){
                        holdLogInStmt = "missing an input";
                        logInErrorStmt.setText(holdLogInStmt);
                    }
                    // If there is none found then add
                    else if(!foundUser){

                        connect.addLogIn(userInput, passInput);
                        System.out.println("Input added to table");

                        primaryStage.setScene(sceneMap.get("selections"));
                    }

                    // Else if password doesn't, match print error message
                    else if(foundUser && !correctPass){
                        holdLogInStmt = "password is wrong";
                        System.out.println("Password is wrong");
                        logInErrorStmt.setText(holdLogInStmt);
                    }

                    // Else if user and pass is same go to next scene
                    else if(foundUser && correctPass){
                        System.out.println("correct user and pass");
                        primaryStage.setScene(sceneMap.get("selections"));
                    }

                }
        );

        // When the find shows button is clicked
        findShows.setOnAction(e-> {
            // Get to the recommendations scene
            primaryStage.setScene(sceneMap.get("recommendations"));

            // Update the query string by calling functions and execute it
            connect.getPlatform(chosenPlatform);
            connect.getGenre(chosenGenre);
            connect.getRange(chosenRating);
            connect.getContent(chosenAge);
            connect.runQuery();
            connect.updateAccount();
            // Update text after getting the results
            updateText(connect.getTitle(), connect.getReleased(), connect.getAge(), connect.getIMBD(), connect.getRotten(),
                    connect.getGenre(), connect.getDescription(), connect.getSeasons(), connect.getStream());

            // Clear for the next pick
            connect.clear();
        });

        // Continue button is clicked
        cont.setOnAction(e-> {

            // Clear the selections for the comboboxes
            genresList.getSelectionModel().clearSelection();
            ratingsRange.getSelectionModel().clearSelection();
            platformsList.getSelectionModel().clearSelection();
            ageRanges.getSelectionModel().clearSelection();

            // Reset the texts to empty
            title.setText("");
            description.setText("");
            genre.setText("");
            IMBD.setText("");
            rottenScore.setText("");
            age.setText("");
            released.setText("");
            seasons.setText("");
            stream.setText("");

            // Get the selections scene once more
            primaryStage.setScene(sceneMap.get("selections"));
        });

        // My Shows menu itme is clicked
        myShows.setOnAction(e -> {
            updateShowsList(connect.getTitleLogin(), primaryStage);
            // Go to the movies scene
            primaryStage.setScene(sceneMap.get("movies"));
        });

        // Back is clicked from the movies scene
        back.setOnAction(e -> {
            listOfShows.getChildren().clear();
            // Return back to the selections scene
            primaryStage.setScene(sceneMap.get("selections"));
        });

        // Put all the scenes onto the map
        sceneMap.put("start", startScene());
        sceneMap.put("logIn", logInScene());
        sceneMap.put("selections", optionsScene());
        sceneMap.put("recommendations", showsScene());
        sceneMap.put("movies", myShowsScene());

        // Set the scene to start scene
        primaryStage.setScene(sceneMap.get("start"));
    }

    // Start scene
    public Scene startScene() {

        // BorderPane for layout
        BorderPane pane = new BorderPane();

        // Set and format the welcome texts
        Text t = new Text("LOOKING FOR A SHOW?");
        t.setFont(Font.font("Helvetica", FontWeight.BOLD, 30));
        t.setTextAlignment(TextAlignment.CENTER);
        t.setFill(Color.BURLYWOOD);

        Text t2 = new Text("YOU CAME TO THE RIGHT PLACE");
        t2.setFont(Font.font("Helvetica", FontWeight.BOLD, 20));
        t2.setTextAlignment(TextAlignment.CENTER);
        t2.setFill(Color.BURLYWOOD);


        // Organize with vbox
        VBox paneCenter = new VBox(10, t, t2, start);
        paneCenter.setPadding(new Insets(100));
        paneCenter.setAlignment(Pos.CENTER);
        paneCenter.setStyle("-fx-background-color: azure;");
        addToGenre();
        pane.setCenter(paneCenter);

        // Return scene
        return new Scene(pane, 700, 700);
    }

    // Set up scene to log into your account to get the list of shows already watched
    public Scene logInScene(){
        // Set up the borderpane for layout
        BorderPane pane = new BorderPane();

        // Username text before text field
        Text user = new Text("UserName: \n");
        user.setFont(Font.font("Helvetica", FontWeight.BOLD, 20));
        user.setFill(Color.SEAGREEN);

        // Password text before text field
        Text pass = new Text("Password: \n");
        pass.setFont(Font.font("Helvetica", FontWeight.BOLD, 20));
        pass.setFill(Color.SEAGREEN);

        // Initialize text fields
        userName = new TextField();
        userName.setMaxWidth(300);
        password = new TextField();

        // HBox - userName
        HBox holdUser = new HBox(10, user, userName);
        holdUser.setPadding(new Insets(10));
        holdUser.setAlignment(Pos.CENTER);

        // HBox - password
        HBox holdPass = new HBox(10, pass, password);
        holdPass.setPadding(new Insets(10));
        holdPass.setAlignment(Pos.CENTER);

        // Set up the error statement
        logInErrorStmt = new Text();
        logInErrorStmt.setFont(Font.font("Helvetica", FontWeight.SEMI_BOLD, 15));
        logInErrorStmt.setFill(Color.MINTCREAM);

        // VBox - vertical box to keep user and pass
        VBox paneCenter = new VBox(10, holdUser, holdPass, logIn, logInErrorStmt);
        paneCenter.setPadding(new Insets(10));
        paneCenter.setAlignment(Pos.CENTER);

        // Set the layout with VBox and BorderPane
        pane.setCenter(paneCenter);
        pane.setStyle("-fx-background-color: thistle;");

        // Return the scenes
        return new Scene(pane, 700, 700);
    }

    // Set up the genre ComboBox
    public void addToGenre(){
        genresList = new ComboBox<>();
        genresList.getItems().addAll("Animation", "Action & Adventure", "Anime", "Biography", "Comedy", "Crime", "Children", "Cult",
                "Drama", "Documentary", "Fantasy", "Family", "Foods", "Game Show", "History", "Horror", "LGBTQ", "Netflix", "Mystery", "Musical", "Science-Fiction",
                "Sport", "Travel", "Thriller", "Reality", "Romance", "Stand-up & Talk");
    }

    // Set up the ratings ComboBox
    public void addToRatings(){
        ratingsRange = new ComboBox<>();
        ratingsRange.getItems().addAll("0-4", "5-7", "8-10");
    }

    // Set up the platforms ComboBox
    public void  addToPlatforms(){
        platformsList = new ComboBox<>();
        platformsList.getItems().addAll("Hulu", "Netflix", "Prime Video", "Disney+");
    }

    // Set up the age range ComboBox
    public void addToAge(){
        ageRanges = new ComboBox<>();
        ageRanges.getItems().addAll("18", "16", "7");
    }

    // Update the texts for displaying the results for the show
    public void updateText(String titleStr, String releasedStr, String ageStr, String IMBDStr, String rottenStr,
                           String genreStr, String descStr, String seasonStr, String streamStr) {

        // If the title string did not return null
        if (titleStr != null ) {
            // Set the title text to be the title passed in
            title.setText("Title: " + titleStr);

            // Set the release year, content rating, IMBD/Rotten scores, description, No of seasons,
            // and streaming platform to their respective texts
            released.setText("Released Year: " + releasedStr);
            age.setText("Content Rating: " + ageStr);
            IMBD.setText("IMBD Rating: " + IMBDStr);
            rottenScore.setText("Rotten Score: " + rottenStr);
            genre.setText("Genre: " + genreStr);
            description.setText("Description: " + descStr);
            String temp = seasonStr;
            String delims = "S";
            String[] tokens = temp.split(delims);
            String seasonNum = tokens[0];
            seasons.setText("No. of Seasons: " + seasonNum + " Season(s)");
            stream.setText("Streaming Platform: " + streamStr);
        }

        // TV shows did not match, make title hold the try again message
        else {
            title.setText("No TV Shows Match... Please Try Again!" );
        }
    }

    // Scene to keep record of the options
    public Scene optionsScene() {
        // BorderPane for layout
        BorderPane pane = new BorderPane();

        // Vbox to organize the items
        VBox paneCenter = new VBox(10);
        paneCenter.setAlignment(Pos.CENTER);

        // Genre - text and declare comboBox
        Text t2 = new Text(550, 20, "WHAT GENRE ARE YOU INTERESTED IN? \n");
        t2.setFont(Font.font("Helvetica", FontWeight.BOLD, 20));
        t2.setFill(Color.web("#401921"));
        addToGenre();

        // Ratings - text and declare comboBox
        Text t3 = new Text(550, 20, "WHAT RANGE SHOULD THE RATING BE? \n");
        t3.setFont(Font.font("Helvetica", FontWeight.BOLD, 20));
        t3.setFill(Color.web("#401921"));
        addToRatings();

        // Platform - text and declare comboBox
        Text t4 = new Text(550, 20, "WHAT PLATFORM DO YOU WANT TO WATCH ON? \n");
        t4.setFont(Font.font("Helvetica", FontWeight.BOLD, 20));
        t4.setFill(Color.web("#401921"));
        addToPlatforms();

        // Age - text and declare comboBox
        Text t5 = new Text(550, 20, "WHAT SHOULD BE THE CONTENT RATING? \n");
        t5.setFont(Font.font("Helvetica", FontWeight.BOLD, 20));
        t5.setFill(Color.web("#401921"));
        addToAge();

        // Set value chosen from genres list
        genresList.setOnAction(e->{
            if(genresList.getValue() != null){
                chosenGenre = genresList.getValue();
            }
        });

        // Set value chosen from ratings list
        ratingsRange.setOnAction(e->{
            if(ratingsRange.getValue() != null){
                chosenRating = ratingsRange.getValue();
            }
        });

        // Set value chosen from age list
        ageRanges.setOnAction(e->{
            if(ageRanges.getValue() != null){
                chosenAge = ageRanges.getValue();
            }
        });

        // Set value chosen from platform list
        platformsList.setOnAction(e->{
            if(platformsList.getValue() != null){
                chosenPlatform = platformsList.getValue();
            }
        });


        // All items to vbox and reset pane
        paneCenter.getChildren().addAll(t4, platformsList, t2, genresList, t3, ratingsRange, t5, ageRanges, findShows);
        pane.setStyle("-fx-background-color:#ad8989");
        pane.setTop(menuBar);
        pane.setCenter(paneCenter);

        // Return the scene
        return new Scene(pane, 700, 700);
    }

    // Shows the recommended shows scene
    public Scene showsScene(){

        // BorderPane for layout
        BorderPane pane = new BorderPane();

        // Header text
        Text rec = new Text(550, 20, "HERE IS YOUR RECOMMENDATION: \n");
        rec.setFont(Font.font("Helvetica", FontWeight.BOLD, 30));
        rec.setFill(Color.FLORALWHITE);

        // Title, released, age, IMBD, rottenScore, genre, description, seasons, and stream
        // texts initialized
        title = new Text();
        title.setFont(Font.font("Helvetica", FontWeight.BOLD, 20));
        title.setFill(Color.OLDLACE);

        released = new Text();
        released.setFont(Font.font("Helvetica", FontWeight.BOLD, 14));
        released.setFill(Color.PAPAYAWHIP);

        age = new Text();
        age.setFont(Font.font("Helvetica", FontWeight.BOLD, 14));
        age.setFill(Color.PAPAYAWHIP);

        IMBD = new Text();
        IMBD.setFont(Font.font("Helvetica", FontWeight.BOLD, 14));
        IMBD.setFill(Color.PAPAYAWHIP);

        rottenScore = new Text();
        rottenScore.setFont(Font.font("Helvetica", FontWeight.BOLD, 14));
        rottenScore.setFill(Color.PAPAYAWHIP);

        genre = new Text();
        genre.setFont(Font.font("Helvetica", FontWeight.BOLD, 14));
        genre.setFill(Color.PAPAYAWHIP);

        description = new Text();
        description.setFont(Font.font("Helvetica", FontWeight.BOLD, 14));
        description.setWrappingWidth(600);
        description.setFill(Color.PAPAYAWHIP);

        seasons = new Text();
        seasons.setFont(Font.font("Helvetica", FontWeight.BOLD, 14));
        seasons.setFill(Color.PAPAYAWHIP);

        stream = new Text();
        stream.setFont(Font.font("Helvetica", FontWeight.BOLD, 14));
        stream.setFill(Color.PAPAYAWHIP);

        // Vbox to organize
        VBox paneCenter = new VBox(10, rec, title, released, age, IMBD, rottenScore, genre, description, seasons, stream, cont);
        paneCenter.setAlignment(Pos.CENTER);
        pane.setCenter(paneCenter);
        pane.setStyle("-fx-background-color: LIGHTSTEELBLUE;");

        // Return scene
        return new Scene(pane, 700, 700);
    }

    // MyShowsScene is the scene that holds all the shows that your account saved
    public Scene myShowsScene() {
        // Header to prompt list of shows
        Text showsHeader = new Text("LIST OF YOUR SHOWS");
        showsHeader.setFont(Font.font("Helvetica",FontWeight.BOLD,20));
        showsHeader.setFill(Color.BURLYWOOD);

        // listOfShows VBox that will hold the list of shows saved
        listOfShows = new VBox();
        listOfShows.setAlignment(Pos.CENTER);

        // Back button
        back.setStyle("-fx-background-color: FLORALWHITE;");

        // VBox for organization
        VBox pane = new VBox(showsHeader, listOfShows, back);
        pane.setSpacing(50.0);
        pane.setAlignment(Pos.CENTER);

        // BorderPane for organization
        BorderPane bPane = new BorderPane();
        bPane.setCenter(pane);
        bPane.setStyle("-fx-background-color: MISTYROSE;");

        // Return the scene
        return new Scene(bPane, 700, 700);
    }

    // Function that will update the text for the list of shows
    public void updateShowsList(String showList, Stage primaryStage) {
        // If the showList is not null, update the text listOfShows
        if (showList != null) {
            // Split up the result showList into separate strings for the titles
            String temp = showList;
            String delims = "\n";
            String[] tokens = temp.split(delims);

            // Loop through the tokens array
            for ( int i = 0; i < tokens.length; i++){
                String tempStr = tokens[i];

                // For each title, make a text to display
                Text showTitle = new Text(tempStr);
                showTitle.setFont(Font.font("Helvetica",FontWeight.BOLD,14));
                showTitle.setFill(Color.BURLYWOOD);
                showTitle.setTextAlignment(TextAlignment.LEFT);

                // Make a button for information for that show
                Button b = new Button("INFO");
                b.setStyle("-fx-background-color: SNOW;");

                // If that button is clicked, go to the recommendations screen that holds the show's info
                b.setOnAction(e-> {
                    // Call search
                    connect.search(tempStr);

                    // Update the texts
                    updateText(connect.getTitle(), connect.getReleased(), connect.getAge(), connect.getIMBD(), connect.getRotten(),
                            connect.getGenre(), connect.getDescription(), connect.getSeasons(), connect.getStream());
                    listOfShows.getChildren().clear();
                    primaryStage.setScene(sceneMap.get("recommendations"));
                });

                // HBox that holds a show's title and info button
                HBox titleButton = new HBox(showTitle, b);
                titleButton.setAlignment(Pos.CENTER);
                titleButton.setSpacing(30.0);

                // Add the HBox to the listOfShows VBox
                listOfShows.getChildren().addAll(titleButton);
            }
        }
    }

}