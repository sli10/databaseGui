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
    Button start, logIn;
    ComboBox<String> genresList, ratingsRange, platformsList, ageRanges;
    TextField userName, password;
    String holdLogInStmt;
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

        //buttons: logIn, start
        start = new Button("Start Looking");
        logIn = new Button("Log In");

        //change scene at start
		start.setOnAction(e -> {
                    primaryStage.setScene(sceneMap.get("logIn"));
                    try {
                        connect.connect();
                        connect.query();
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

        sceneMap.put("start", startScene());
        sceneMap.put("logIn", logInScene());
        sceneMap.put("selections", optionsScene());

        primaryStage.setScene(sceneMap.get("start"));

    }

    //start scene
    public Scene startScene() {
        BorderPane pane = new BorderPane();

        //welcome text
        Text t = new Text(500, 100, "Are You \n Looking For TV Shows\n to Watch?");
        t.setFont(Font.font("Monospace", FontWeight.BOLD, 70));
        t.setTextAlignment(TextAlignment.CENTER);
        t.setFill(Color.web("#ad8989"));
        t.setStroke(Color.web("#401921"));
        t.setStrokeWidth(2);

        //organize with vbox
        VBox paneCenter = new VBox(10, t, start);
        paneCenter.setPadding(new Insets(100));
        paneCenter.setAlignment(Pos.CENTER);


        pane.setCenter(paneCenter);
//        pane.setStyle(Color.web("#ffffff"));

        return new Scene(pane, 1200, 800);
    }

    //log into your account to get the list of shows already watched
    public Scene logInScene(){
        BorderPane pane = new BorderPane();

        //username text before text field
        Text user = new Text(550, 20, "UserName: \n");
        user.setFont(Font.font("Monospace", FontWeight.BOLD, 20));
        user.setFill(Color.web("#401921"));

        //password text before text field
        Text pass = new Text(550, 20, "Password: \n");
        pass.setFont(Font.font("Monospace", FontWeight.BOLD, 20));
        pass.setFill(Color.web("#401921"));

        //initialize text fields
        userName = new TextField();
        userName.setMaxWidth(300);
        password = new TextField();

        //vBox to organize the items
//        VBox paneCenter = new VBox(10, user, userName, pass, password);
//        paneCenter.setPadding(new Insets(100));
//        paneCenter.setAlignment(Pos.CENTER);

        //hBox - userName
        HBox holdUser = new HBox(10, user, userName);
        holdUser.setPadding(new Insets(10));
        holdUser.setAlignment(Pos.CENTER);

        //hBox - password
        HBox holdPass = new HBox(10, pass, password);
        holdPass.setPadding(new Insets(10));
        holdPass.setAlignment(Pos.CENTER);

        logInErrorStmt = new Text();
        logInErrorStmt.setFont(Font.font("Monospace", FontWeight.SEMI_BOLD, 15));
        logInErrorStmt.setFill(Color.web("#401921"));

        //vBox - vertical box to keep user and pass
        VBox paneCenter = new VBox(10, holdUser, holdPass, logIn, logInErrorStmt);
        paneCenter.setPadding(new Insets(10));
        paneCenter.setAlignment(Pos.CENTER);

        pane.setCenter(paneCenter);

        return new Scene(pane, 1200, 800);
    }

    public void addToGenre(){
        genresList = new ComboBox<>();
        genresList.getItems().addAll();
    }

    //scene to keep record of the options
	public Scene optionsScene() {
        BorderPane pane = new BorderPane();

        //vbox to organize the items
        VBox paneCenter = new VBox(10);
        paneCenter.setAlignment(Pos.CENTER);

        //genre - text and declare comboBox
        Text t2 = new Text(550, 20, "What genre are you interested in? \n");
        t2.setFont(Font.font("Monospace", FontWeight.BOLD, 20));
        t2.setFill(Color.web("#401921"));

        genresList = new ComboBox<>();

        //ratings - text and declare comboBox
        Text t3 = new Text(550, 20, "What range of ratings? \n");
        t3.setFont(Font.font("Monospace", FontWeight.BOLD, 20));
        t3.setFill(Color.web("#401921"));

        ratingsRange = new ComboBox<>();

        //platform - text and declare comboBox
        Text t4 = new Text(550, 20, "What platform do you watch on? \n");
        t4.setFont(Font.font("Monospace", FontWeight.BOLD, 20));
        t4.setFill(Color.web("#401921"));

        platformsList = new ComboBox<>();

        //Age - text and declare comboBox
        Text t5 = new Text(550, 20, "Content Ratings? \n");
        t5.setFont(Font.font("Monospace", FontWeight.BOLD, 20));
        t5.setFill(Color.web("#401921"));

        ageRanges = new ComboBox<>();

        //all items to vbox and reset pane
        paneCenter.getChildren().addAll(t4, platformsList, t2, genresList, t3, ratingsRange, t5, ageRanges);
        pane.setTop(paneCenter);
        pane.setStyle("-fx-background-color:#ad8989");

        return new Scene(pane, 1000, 800);

	}
}
