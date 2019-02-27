package notes;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Cursor;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import static notes.Notes.stages;

public class settingController implements Initializable {

    Stage stage;
    Database database;
    Note note;
    private double sceneX, sceneY;
    Theme theme;
    private final List<Theme> themes = Notes.themes;

    @FXML
    private ImageView exit;
    @FXML
    private Pane titleBar;
    @FXML
    private Label name;
    @FXML
    private Label settingLabel;
    @FXML
    private AnchorPane menuPane;
    @FXML
    private AnchorPane menuMainPane;
    @FXML
    private ScrollPane content;
    @FXML
    private Pane clickPane;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {

    }

    public void start(Stage stage, Note note) {
        this.stage = stage;
        this.note = note;
        
        database = new Database();
        theme = themes.get(note.getTheme_id() - 1);
        menuPane.setStyle("-fx-background-color: #ffe900;");
        menuMainPane.getStyleClass().add("border-main");
        titleBar.getStyleClass().add("border-main");
        content.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        content.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        titleDrag();
        stage.setAlwaysOnTop(true);

        stage.iconifiedProperty().addListener((ObservableValue<? extends Boolean> ov, Boolean t, Boolean value) -> {
            if (!value) {
                stage.setAlwaysOnTop(true);
            }
        });
        
        cssRefresh();
        reload();
    }

    @FXML
    private void exit(MouseEvent event) {
        stage.close();
    }

    public void titleDrag() {
        titleBar.setOnMousePressed((MouseEvent mouseEvent) -> {
            sceneX = stage.getX() - mouseEvent.getScreenX();
            sceneY = stage.getY() - mouseEvent.getScreenY();
            titleBar.setCursor(Cursor.MOVE);
        });

        titleBar.setOnMouseReleased((MouseEvent mouseEvent) -> {
            titleBar.setCursor(Cursor.DEFAULT);
        });

        titleBar.setOnMouseDragged((MouseEvent mouseEvent) -> {
            stage.setX(mouseEvent.getScreenX() + sceneX);
            stage.setY(mouseEvent.getScreenY() + sceneY);
        });

    }

    private void cssRefresh() {
        titleBar.setStyle("-fx-background-color: " + theme.getPrimaryColour());
        menuPane.setStyle("-fx-background-color: " + theme.getSecondaryColour());
        menuMainPane.setStyle("-fx-background-color: " + theme.getPrimaryColour());
        exit.setImage(new Image("assets/exit" + theme.getButtonColour() + ".png"));
        settingLabel.setStyle("-fx-text-fill: " + theme.getTextColour());
    }

    
    private void reload() {
        Theme themeInteral;
        menuPane.getChildren().clear();
        Pane tempPane = clickPane;
        for (int x = themes.size() - 1; x >= 0; x--) {
            themeInteral = themes.get(x);
            Label name2 = new Label();
            Pane clickPane2 = new Pane();
            menuPane.getChildren().add(clickPane2);

            clickPane2.setLayoutX(clickPane.getLayoutX());
            clickPane2.setLayoutY(clickPane.getLayoutY() + 60);
            clickPane2.setPrefHeight(clickPane.getPrefHeight());
            clickPane2.setPrefWidth(clickPane.getPrefWidth());
            clickPane2.setStyle("-fx-background-color: " + themeInteral.getPrimaryColour());
            clickPane2.getStyleClass().add("rounded");
            clickPane2.getStyleClass().add("border-main");

            clickPane2.getChildren().add(name2);

            name2.setStyle("-fx-prompt-text-fill: " + themeInteral.getHintColour() + ";-fx-text-fill: " + themeInteral.getTextColour());
            name2.setLayoutX(name.getLayoutX());
            name2.setLayoutY(name.getLayoutY());
            name2.setPrefHeight(name.getPrefHeight());
            name2.setPrefWidth(name.getPrefWidth());
            
            name = name2;
            clickPane = clickPane2;
            name.setText(themeInteral.getName());
            final int ID = themeInteral.getTheme_id();
            clickPane.addEventFilter(MouseEvent.MOUSE_PRESSED, new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent mouseEvent) {
                    //reset theme here
                    theme = Notes.themes.get(ID - 1);
                    note.setTheme_id(ID);
                    database.updateTheme(note);
                    update();
                }
            });
        }
        clickPane = tempPane;
    }
    
    private void update(){
        cssRefresh();
        reload();
        
        stages.get(0).setPassedByID(note.getList_id());
        
        stages.stream().forEach((x) -> {
             if (x.noteID() == note.getId()){
                x.setNote(note);
            }
            x.update();
        });
    }
    
}
