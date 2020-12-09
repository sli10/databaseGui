import java.util.HashMap;


import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
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
    HashMap<String, Scene> sceneMap;
    Button start;

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

        start = new Button("Start Looking");

//		start.setOnAction(e -> primaryStage.setScene(sceneMap.get());

        sceneMap.put("start", startScene());

        primaryStage.setScene(sceneMap.get("start"));

    }

    public Scene startScene() {
        BorderPane pane = new BorderPane();

        Text t = new Text(500, 100, "Are You \n Looking For TV Shows\n to Watch?");
        t.setFont(Font.font("Monospace", FontWeight.BOLD, 70));
        t.setTextAlignment(TextAlignment.CENTER);
        t.setFill(Color.web("#ad8989"));
        t.setStroke(Color.web("#401921"));
        t.setStrokeWidth(2);

        VBox paneCenter = new VBox(10, t, start);
        paneCenter.setPadding(new Insets(100));
        paneCenter.setAlignment(Pos.CENTER);

        pane.setCenter(paneCenter);
//        pane.setStyle(Color.web("#ffffff"));

        return new Scene(pane, 800, 700);
    }

//	public Scene optionsScene() {
//
//	}
}
