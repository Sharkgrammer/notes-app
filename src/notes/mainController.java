package notes;

import java.net.URL;
import java.util.Optional;
import java.util.Random;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Rectangle2D;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.Cursor;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DialogPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class mainController implements Initializable {

    Note note;
    Stage stage;
    Database database;
    private double sceneX, sceneY;

    @FXML
    private ImageView exit;
    @FXML
    private Pane exitPane;
    @FXML
    private ImageView add;
    @FXML
    private Pane addPane;
    @FXML
    private ImageView option;
    @FXML
    private Pane optionPane;
    @FXML
    private Pane titleBar;
    @FXML
    private TextField titleText;
    @FXML
    private TextArea contentText;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        database = new Database();
    }

    public void start(Note note, Stage stage) {
        this.note = note;
        this.stage = stage;
        randomLocation();
        titleDrag();
        titleText.setText(note.getTitle());
        contentText.setText(note.getContent());
        contentText.requestFocus();
    }

    public void randomLocation() {
        Rectangle2D bounds = Screen.getPrimary().getBounds();
        int StageX = (int) bounds.getWidth();
        int StageY = (int) bounds.getHeight();
        Random rand = new Random();
        int movementX = rand.nextInt(StageX - (int) Math.round(StageX * 0.15));
        int movementY = rand.nextInt(StageY - (int) Math.round(StageY * 0.30));
        stage.setX(movementX);
        stage.setY(movementY);
    }

    @FXML
    private void add(MouseEvent event) {
        System.out.println("Add clicked");
        try {
            (new Notes()).makeStage(new Stage(), new Note(), 1);
        } catch (Exception ex) {
            System.out.println(ex.toString());
        }
    }

    @FXML
    private void option(MouseEvent event) {
        System.out.println("Option clicked");
        note.setContent(contentText.getText());
        note.setTitle(titleText.getText());
        database.updateNote(note);
    }

    @FXML
    private void exit(MouseEvent event) {
        ButtonType Yes = new ButtonType("Yes");
        ButtonType No = new ButtonType("No");
        ButtonType Hide = new ButtonType("Hide");
        Alert alert = new Alert(AlertType.NONE, "Do you want to delete your note: '" + note.getTitle() + "'?", Yes, No, Hide);
        alert.setX(stage.getX() - 50);
        alert.setY(stage.getY() + 80);
        alert.initStyle(StageStyle.UNDECORATED);
        
        Optional<ButtonType> result = alert.showAndWait();
        if (result.orElse(No) == Yes) {
            System.out.println("Exit clicked: Delete");
            database.deleteNote(note.getId());
            stage.close();
        }else if (result.orElse(No) == Hide) {
            System.out.println("Exit clicked: Hide");
            stage.close();
        }else{
            System.out.println("Exit clicked: No");
        }
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

}
