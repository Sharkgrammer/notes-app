package notes;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Rectangle2D;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.Cursor;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.WindowEvent;
import static notes.Notes.stages;

public class mainController implements Initializable {

    Note note;
    Stage stage;
    Database database;
    private double sceneX, sceneY, alertX, alertY;
    boolean saved = true, optionOpen = false;
    private Theme theme;

    @FXML
    private ImageView exit;
    @FXML
    private Pane exitPane;
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

        theme = Notes.themes.get(note.getTheme_id() - 1);
        randomLocation();
        titleDrag();
        cssRefresh();
        stage.setAlwaysOnTop(true);
        titleText.setText(note.getTitle());
        contentText.setText(note.getContent());
        contentText.requestFocus();
        contentText.wrapTextProperty().set(true);

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
        mainPane.getStyleClass().add("border-main");
        titleBar.getStyleClass().add("border-main");
        contentText.setStyle("-fx-prompt-text-fill: " + theme.getHintColour() + ";-fx-text-fill: " + theme.getTextColour());
        titleText.setStyle("-fx-prompt-text-fill: " + theme.getHintColour() + ";-fx-text-fill: " + theme.getTextColour());
        save.setImage(new Image("assets/save" + theme.getButtonColour() + ".png"));
        exit.setImage(new Image("assets/exit" + theme.getButtonColour() + ".png"));
        option.setImage(new Image("assets/option" + theme.getButtonColour() + ".png"));
    }

    @FXML
    private void option(MouseEvent event) {
        if (!optionOpen) {
            optionOpen = true;
            Stage stage = new Stage();
            stage.setX(this.stage.getX());
            stage.setY(this.stage.getY());
            stage.initStyle(StageStyle.UNDECORATED);
            stage.initStyle(StageStyle.TRANSPARENT);
            FXMLLoader loader = new FXMLLoader(getClass().getResource("setting.fxml"));
            try {

                stage.setScene(new Scene((Parent) loader.load()));
            } catch (Exception e) {
                System.out.println(e.toString());
            }

            settingController controller = loader.<settingController>getController();
            stages.add(new stageControl(note, stage));
            controller.start(stage, note);

            stage.getIcons().add(new Image("assets/logo.png"));
            stage.show();
            
            stage.setOnHiding(new EventHandler<WindowEvent>() {

                @Override
                public void handle(WindowEvent event) {
                    stage.close();
                    optionOpen = false;
                }
            });
        }
    }

    @FXML
    private void save(MouseEvent event) {
        //System.out.println("Option clicked");
        note.setContent(contentText.getText());
        note.setTitle(titleText.getText());
        database.updateNote(note);
        saved = true;
        runUpdates();
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
            runUpdates();
            stage.close();
        } else if (result.orElse(Not) == Hide) {
            //System.out.println("Exit clicked: Close");
            if (!saved) {
                save(null);
            }
            //close the stage, remove it from the list

            List<stageControl> list = Notes.stages;

            for (stageControl x : list) {
                if (x.noteID() == note.getId()) {
                    list.remove(x);
                    break;
                }
            }
            Notes.stages = list;

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

        titleText.setOnMousePressed((MouseEvent mouseEvent) -> {
            sceneX = stage.getX() - mouseEvent.getScreenX();
            sceneY = stage.getY() - mouseEvent.getScreenY();
        });

        titleText.setOnMouseDragged((MouseEvent mouseEvent) -> {
            stage.setX(mouseEvent.getScreenX() + sceneX);
            stage.setY(mouseEvent.getScreenY() + sceneY);
        });

    }

    private void runUpdates() {
        stages.stream().forEach((x) -> {
            x.update();
        });
    }

    public void update(Note note) {
        this.note = note;
        theme = Notes.themes.get(note.getTheme_id() - 1);
        cssRefresh();
    }

}
