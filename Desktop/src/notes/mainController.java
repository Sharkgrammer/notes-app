package notes;

import java.net.URL;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
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
import javafx.scene.layout.AnchorPane;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class mainController implements Initializable {

    Note note;
    Stage stage;
    Database database;
    private double sceneX, sceneY, alertX, alertY;
    boolean saved = true;
    private Theme theme;

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
    private ImageView save;
    @FXML
    private Pane savePane;
    @FXML
    private Pane titleBar;
    @FXML
    private TextField titleText;
    @FXML
    private TextArea contentText;
    @FXML
    private AnchorPane mainPane;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        database = new Database();
    }

    public void start(Note note, Stage stage) {
        this.note = note;
        this.stage = stage;
        theme = Notes.themes.get( note.getTheme_id() - 1);
        randomLocation();
        titleDrag();
        cssRefresh();
        stage.setAlwaysOnTop(true);
        titleText.setText(note.getTitle());
        contentText.setText(note.getContent());
        contentText.requestFocus();
        contentText.wrapTextProperty().set(true);
        
        System.out.println(theme.getName());

        ChangeListener savedSet = (ChangeListener<String>) (ObservableValue<? extends String> observableValue, String s, String s2) -> {
            saved = false;
        };

        contentText.textProperty().addListener(savedSet);
        titleText.textProperty().addListener(savedSet);

        stage.iconifiedProperty().addListener((ObservableValue<? extends Boolean> ov, Boolean t, Boolean value) -> {
            if (!value) {
                stage.setAlwaysOnTop(true);
            }
        });
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

    private void cssRefresh() {
        titleBar.setStyle("-fx-background-color: " + theme.getPrimaryColour());
        mainPane.setStyle("-fx-background-color: " + theme.getSecondaryColour());
        contentText.setStyle("-fx-prompt-text-fill: " + theme.getHintColour() + ";-fx-text-fill: " + theme.getTextColour());
        titleText.setStyle("-fx-prompt-text-fill: " + theme.getHintColour() + ";-fx-text-fill: " + theme.getTextColour());
    }

    @FXML
    private void add(MouseEvent event) {
        //System.out.println("Add clicked");
        try {
            (new Notes()).makeStage(new Stage(), new Note(), 1);
        } catch (Exception ex) {
            System.out.println(ex.toString());
        }
    }
    
    @FXML
    private void option(MouseEvent event) {
        
    }

    @FXML
    private void save(MouseEvent event) {
        //System.out.println("Option clicked");
        note.setContent(contentText.getText());
        note.setTitle(titleText.getText());
        database.updateNote(note);
        saved = true;
    }

    @FXML
    private void exit(MouseEvent event) {
        ButtonType Delete = new ButtonType("Delete");
        ButtonType Hide = new ButtonType("Close");
        ButtonType Min = new ButtonType("Minimise");
        ButtonType Not = new ButtonType("Nothing");
        Alert alert = new Alert(AlertType.NONE, "Exit Options for: '" + note.getTitle() + "'?", Delete, Hide, Min, Not);
        alert.setX(stage.getX() - 50);
        alert.setY(stage.getY() + 80);
        alert.initStyle(StageStyle.UNDECORATED);
        stage.setAlwaysOnTop(false);

        alert.getDialogPane().setOnMousePressed((MouseEvent mouseEvent) -> {
            alertX = alert.getX() - mouseEvent.getScreenX();
            alertY = alert.getY() - mouseEvent.getScreenY();
            alert.getDialogPane().setCursor(Cursor.MOVE);
        });

        alert.getDialogPane().setOnMouseReleased((MouseEvent mouseEvent) -> {
            alert.getDialogPane().setCursor(Cursor.DEFAULT);
        });

        alert.getDialogPane().setOnMouseDragged((MouseEvent mouseEvent) -> {
            alert.setX(mouseEvent.getScreenX() + alertX);
            alert.setY(mouseEvent.getScreenY() + alertY);
        });

        Optional<ButtonType> result = alert.showAndWait();
        if (result.orElse(Not) == Delete) {
            //System.out.println("Exit clicked: Delete");
            database.deleteNote(note.getId());
            stage.close();
        } else if (result.orElse(Not) == Hide) {
            //System.out.println("Exit clicked: Close");
            if (!saved) {
                save(null);
            }
            stage.close();
        } else if (result.orElse(Not) == Min) {
            if (!saved) {
                save(null);
            }
            //System.out.println("Exit clicked: Minimise");
            stage.setIconified(true);
        } else {
            //System.out.println("Exit clicked: Nothing");
            stage.setAlwaysOnTop(true);
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
