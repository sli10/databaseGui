import java.io.FileNotFoundException;
import java.sql.SQLException;
import java.util.HashMap;


import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;

import javax.swing.*;

public class Gui extends Application {
    HashMap<String, Scene> sceneMap;
    Button start, logIn, findShows, cont;
    ComboBox<String> genresList, ratingsRange, platformsList, ageRanges;
    TextField userName, password;
    String holdLogInStmt, chosenGenre, chosenAge, chosenPlatform, chosenRating;
    Text title, description, genre, IMBD, rottenScore, age, released, seasons, stream;
    Text logInErrorStmt;
    MyConnection connect = new MyConnection();
    HashMap<String, String> userPassList = new HashMap<String, String>();

    public static void main(String[] args) {
        // TODO Auto-generated method stub
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        // TODO Auto-generated method stub
        primaryStage.setTitle("Look for TV shows to watch");

        sceneMap = new HashMap<String,Scene>();

        Scene scene = new Scene(new HBox(), 600, 600);
        primaryStage.setScene(scene);
        primaryStage.show();

        //buttons: logIn, start, findShows
        start = new Button("START LOOKING");
        start.setStyle("-fx-background-color: SNOW;");
        logIn = new Button("LOG IN");
        logIn.setStyle("-fx-background-color: SNOW;");
        findShows = new Button("FIND SHOWS");
        findShows.setStyle("-fx-background-color: SNOW;");
        cont = new Button("CONTINUE");
        cont.setStyle("-fx-background-color: SNOW;");

        //change scene at start
        start.setOnAction(e -> {
                    primaryStage.setScene(sceneMap.get("logIn"));
                    try {
                        connect.connect();
                    } catch (FileNotFoundException fileNotFoundException) {
                        fileNotFoundException.printStackTrace();
                    }
                }

        );

        //after login is clicked
        logIn.setOnAction(e -> {
                    //get the inputs of user and pass
                    String userInput = userName.getText();
                    String passInput = password.getText();

                    System.out.println("UserInputs: " + userInput);
                    System.out.println("PassInputs: " + passInput);

                    //if one of the inputs are empty then print a empty statement
                    if(passInput.equals("") || userInput.equals("")){
                        holdLogInStmt = "missing an input";
                        logInErrorStmt.setText(holdLogInStmt);
                    }
                    //checks to see if user is in the list of usernames and passwords
                    else if(!userPassList.containsKey(userInput)){
                        userPassList.put(userInput, passInput);
                        primaryStage.setScene(sceneMap.get("selections"));
                    }
                    //else if password doesn't match print error message
                    else if(!userPassList.get(userInput).equals(passInput)){
                        holdLogInStmt = "password is wrong";
                        logInErrorStmt.setText(holdLogInStmt);
                    }
                    //else if user and pass is same go to next scene
                    else if(userPassList.get(userInput).equals(passInput)){
                        primaryStage.setScene(sceneMap.get("selections"));
                        logInErrorStmt.setText(holdLogInStmt);
                    }


                }
        );

        findShows.setOnAction(e-> {
            primaryStage.setScene(sceneMap.get("recommendations"));
            System.out.println("chosen platform: " + chosenPlatform);
            System.out.println("chosen genre: " + chosenGenre);
            System.out.println("chosen rating: " + chosenRating);
            System.out.println("chosen age: " + chosenAge);
            connect.getPlatform(chosenPlatform);
            connect.getGenre(chosenGenre);
            connect.getRange(chosenRating);
            connect.getContent(chosenAge);
            connect.runQuery();
            connect.pickOne();
            updateText(connect.getTitle(), connect.getReleased(), connect.getAge(), connect.getIMBD(), connect.getRotten(),
                    connect.getGenre(), connect.getDescription(), connect.getSeasons(), connect.getStream());
            connect.clear();
        });

        cont.setOnAction(e-> {
            genresList.getSelectionModel().clearSelection();
            ratingsRange.getSelectionModel().clearSelection();
            platformsList.getSelectionModel().clearSelection();
            ageRanges.getSelectionModel().clearSelection();
            title.setText("");
            description.setText("");
            genre.setText("");
            IMBD.setText("");
            rottenScore.setText("");
            age.setText("");
            released.setText("");
            seasons.setText("");
            stream.setText("");
            primaryStage.setScene(sceneMap.get("selections"));
        });

        sceneMap.put("start", startScene());
        sceneMap.put("logIn", logInScene());
        sceneMap.put("selections", optionsScene());
        sceneMap.put("recommendations", showsScene());

        primaryStage.setScene(sceneMap.get("start"));

    }

    //start scene
    public Scene startScene() {
        BorderPane pane = new BorderPane();

        //welcome text
        Text t = new Text("LOOKING FOR A SHOW?");
        t.setFont(Font.font("Helvetica", FontWeight.BOLD, 30));
        t.setTextAlignment(TextAlignment.CENTER);
        t.setFill(Color.BURLYWOOD);

        Text t2 = new Text("YOU CAME TO THE RIGHT PLACE");
        t2.setFont(Font.font("Helvetica", FontWeight.BOLD, 20));
        t2.setTextAlignment(TextAlignment.CENTER);
        t2.setFill(Color.BURLYWOOD);


        //organize with vbox
        VBox paneCenter = new VBox(10, t, t2, start);
        paneCenter.setPadding(new Insets(100));
        paneCenter.setAlignment(Pos.CENTER);
        paneCenter.setStyle("-fx-background-color: azure;");
        addToGenre();
        pane.setCenter(paneCenter);

        return new Scene(pane, 700, 700);
    }

    //log into your account to get the list of shows already watched
    public Scene logInScene(){
        BorderPane pane = new BorderPane();

        //username text before text field
        Text user = new Text("UserName: \n");
        user.setFont(Font.font("Helvetica", FontWeight.BOLD, 20));
        user.setFill(Color.SEAGREEN);

        //password text before text field
        Text pass = new Text("Password: \n");
        pass.setFont(Font.font("Helvetica", FontWeight.BOLD, 20));
        pass.setFill(Color.SEAGREEN);

        //initialize text fields
        userName = new TextField();
        userName.setMaxWidth(300);
        password = new TextField();

        //hBox - userName
        HBox holdUser = new HBox(10, user, userName);
        holdUser.setPadding(new Insets(10));
        holdUser.setAlignment(Pos.CENTER);

        //hBox - password
        HBox holdPass = new HBox(10, pass, password);
        holdPass.setPadding(new Insets(10));
        holdPass.setAlignment(Pos.CENTER);

        logInErrorStmt = new Text();
        logInErrorStmt.setFont(Font.font("Helvetica", FontWeight.SEMI_BOLD, 15));
        logInErrorStmt.setFill(Color.MINTCREAM);

        //vBox - vertical box to keep user and pass
        VBox paneCenter = new VBox(10, holdUser, holdPass, logIn, logInErrorStmt);
        paneCenter.setPadding(new Insets(10));
        paneCenter.setAlignment(Pos.CENTER);

        pane.setCenter(paneCenter);
        pane.setStyle("-fx-background-color: thistle;");

        return new Scene(pane, 700, 700);
    }

    public void addToGenre(){
        genresList = new ComboBox<>();
        genresList.getItems().addAll("Animation", "Action & Adventure", "Anime", "Biography", "Comedy", "Crime", "Children", "Cult",
                "Drama", "Documentary", "Fantasy", "Family", "Foods", "Game Show", "History", "Horror", "LGBTQ", "Netflix", "Mystery", "Musical", "Science-Fiction",
                "Sport", "Travel", "Thriller", "Reality", "Romance", "Stand-up & Talk");
    }

    public void addToRatings(){
        ratingsRange = new ComboBox<>();
        ratingsRange.getItems().addAll("0-4", "5-7", "8-10");
    }

    public void  addToPlatforms(){
        platformsList = new ComboBox<>();
        platformsList.getItems().addAll("Hulu", "Netflix", "Prime Video", "Disney+");
    }

    public void addToAge(){
        ageRanges = new ComboBox<>();
        ageRanges.getItems().addAll("18", "16", "7");
    }

    public void updateText(String titleStr, String releasedStr, String ageStr, String IMBDStr, String rottenStr,
                           String genreStr, String descStr, String seasonStr, String streamStr) {
        System.out.println("titleStr: " + titleStr);

        if (titleStr != null ) {
            title.setText("Title: " + titleStr);
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
        else {
            title.setText("No TV Shows Match... Please Try Again!" );
        }
    }
    
    //scene to keep record of the options
    public Scene optionsScene() {
        BorderPane pane = new BorderPane();

        //vbox to organize the items
        VBox paneCenter = new VBox(10);
        paneCenter.setAlignment(Pos.CENTER);

        //genre - text and declare comboBox
        Text t2 = new Text(550, 20, "WHAT GENRE ARE YOU INTERESTED IN? \n");
        t2.setFont(Font.font("Helvetica", FontWeight.BOLD, 20));
        t2.setFill(Color.web("#401921"));
        addToGenre();

        //ratings - text and declare comboBox
        Text t3 = new Text(550, 20, "WHAT RANGE SHOULD THE RATING BE? \n");
        t3.setFont(Font.font("Helvetica", FontWeight.BOLD, 20));
        t3.setFill(Color.web("#401921"));
        addToRatings();

        //platform - text and declare comboBox
        Text t4 = new Text(550, 20, "WHAT PLATFORM DO YOU WANT TO WATCH ON? \n");
        t4.setFont(Font.font("Helvetica", FontWeight.BOLD, 20));
        t4.setFill(Color.web("#401921"));
        addToPlatforms();

        //Age - text and declare comboBox
        Text t5 = new Text(550, 20, "WHAT SHOULD BE THE CONTENT RATING? \n");
        t5.setFont(Font.font("Helvetica", FontWeight.BOLD, 20));
        t5.setFill(Color.web("#401921"));
        addToAge();

        //set value chosen from genres list
        genresList.setOnAction(e->{
            if(genresList.getValue() != null){
                chosenGenre = genresList.getValue();
            }
        });

        //set value chosen from ratings list
        ratingsRange.setOnAction(e->{
            if(ratingsRange.getValue() != null){
                chosenRating = ratingsRange.getValue();
            }
        });

        //set value chosen from age list
        ageRanges.setOnAction(e->{
            if(ageRanges.getValue() != null){
                chosenAge = ageRanges.getValue();
            }
        });

        //set value chosen from platform list
        platformsList.setOnAction(e->{
            if(platformsList.getValue() != null){
                chosenPlatform = platformsList.getValue();
            }
        });


        //all items to vbox and reset pane
        paneCenter.getChildren().addAll(t4, platformsList, t2, genresList, t3, ratingsRange, t5, ageRanges, findShows);
        pane.setStyle("-fx-background-color:#ad8989");

        pane.setCenter(paneCenter);

        return new Scene(pane, 700, 700);

    }

    //shows the recommended shows scene
    public Scene showsScene(){
        BorderPane pane = new BorderPane();

        //header text
        Text rec = new Text(550, 20, "HERE IS YOUR RECOMMENDATION: \n");
        rec.setFont(Font.font("Helvetica", FontWeight.BOLD, 30));
        rec.setFill(Color.FLORALWHITE);

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

        //vbox to organize
        VBox paneCenter = new VBox(10, rec, title, released, age, rottenScore, genre, description, seasons, stream, cont);
        paneCenter.setAlignment(Pos.CENTER);
        pane.setCenter(paneCenter);
        pane.setStyle("-fx-background-color: LIGHTSTEELBLUE;");
        return new Scene(pane, 700, 700);
    }

}