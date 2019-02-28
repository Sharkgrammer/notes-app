package notes;

import java.net.URL;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.ResourceBundle;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
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
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import static notes.Notes.stages;

public class menuController implements Initializable {

    List<Note> notes;
    Stage stage;
    Database database;
    private double sceneX, sceneY, alertX, alertY;
    boolean saved = true;
    List<Theme> themes = Notes.themes;

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
    private ScrollPane content;
    @FXML
    private Pane clickPane;
    @FXML
    private Label name;
    @FXML
    private Label date;
    @FXML
    private AnchorPane menuPane;
    @FXML
    private AnchorPane menuMainPane;
    @FXML
    private Pane logPane;
    @FXML
    private TextField email;
    @FXML
    private PasswordField password;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        database = new Database();
    }

    public void start(List<Note> notes, Stage stage) {
        this.notes = notes;
        this.stage = stage;

        if (notes == null) {
            content.setVisible(false);
            logPane.setVisible(true);
            addPane.setVisible(false);
            optionPane.setVisible(false);
        } else {
            reload(notes);
        }

        menuPane.setStyle("-fx-background-color: #ffe900;");
        menuMainPane.getStyleClass().add("border-main");
        titleBar.getStyleClass().add("border-main");
        content.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        content.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        randomLocation();
        titleDrag();
        stage.setAlwaysOnTop(true);

        stage.iconifiedProperty().addListener((ObservableValue<? extends Boolean> ov, Boolean t, Boolean value) -> {
            if (!value) {
                stage.setAlwaysOnTop(true);
            }
        });
    }

    private void reload(List<Note> notes) {
        Note note;
        menuPane.getChildren().clear();
        Pane tempPane = clickPane;
        for (int x = notes.size() - 1; x >= 0; x--) {
            note = notes.get(x);
            Label name2 = new Label(), date2 = new Label();
            Pane clickPane2 = new Pane();
            menuPane.getChildren().add(clickPane2);
            Theme theme = themes.get(note.getTheme_id() - 1);

            clickPane2.setLayoutX(clickPane.getLayoutX());
            clickPane2.setLayoutY(clickPane.getLayoutY() + 60);
            clickPane2.setPrefHeight(clickPane.getPrefHeight());
            clickPane2.setPrefWidth(clickPane.getPrefWidth());
            clickPane2.setStyle("-fx-background-color: " + theme.getPrimaryColour());
            clickPane2.getStyleClass().add("rounded");
            clickPane2.getStyleClass().add("border-main");

            clickPane2.getChildren().add(name2);
            clickPane2.getChildren().add(date2);

            name2.setStyle("-fx-prompt-text-fill: " + theme.getHintColour() + ";-fx-text-fill: " + theme.getTextColour());
            name2.setLayoutX(name.getLayoutX());
            name2.setLayoutY(name.getLayoutY());
            name2.setPrefHeight(name.getPrefHeight());
            name2.setPrefWidth(name.getPrefWidth());

            date2.setStyle("-fx-prompt-text-fill: " + theme.getHintColour() + ";-fx-text-fill: " + theme.getTextColour());
            date2.setLayoutX(date.getLayoutX());
            date2.setLayoutY(date.getLayoutY());
            date2.setPrefHeight(date.getPrefHeight());
            date2.setPrefWidth(date.getPrefWidth());

            name = name2;
            date = date2;
            clickPane = clickPane2;

            //System.out.println(note.getTitle() + "  " + note.getList_id() + "  " + x);
            name.setText(note.getTitle());
            date.setText(note.getDate());
            final int ID = note.getList_id();
            clickPane.addEventFilter(MouseEvent.MOUSE_PRESSED, new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent mouseEvent) {
                    noteClick(ID, 1);
                }
            });
        }
        clickPane = tempPane;
    }

    private void noteClick(int ID, int mode) {
        try {
            Theme theme = themes.get(notes.get(ID).getTheme_id() - 1);
            if (mode != 0) {
                (new Notes()).makeStage(new Stage(), notes.get(ID), 0);
            }
            menuPane.setStyle("-fx-background-color: " + theme.getSecondaryColour());
            menuMainPane.setStyle("-fx-background-color: " + theme.getPrimaryColour());
            titleBar.setStyle("-fx-background-color: " + theme.getPrimaryColour());
            add.setImage(new Image("assets/add" + theme.getButtonColour() + ".png"));
            exit.setImage(new Image("assets/exit" + theme.getButtonColour() + ".png"));
            option.setImage(new Image("assets/option" + theme.getButtonColour() + ".png"));
        } catch (Exception ex) {
            System.out.println(ex.toString());
        }
    }

    private void randomLocation() {
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
        //System.out.println("Add clicked");
        try {
            (new Notes()).makeStage(new Stage(), new Note(), 1);

            runUpdates();
        } catch (Exception ex) {
            System.out.println(ex.toString());
        }
    }

    @FXML
    private void option(MouseEvent event) {
        //Exit

        ButtonType Log = new ButtonType("Yes");
        ButtonType No = new ButtonType("No");
        Alert alert = new Alert(AlertType.NONE, "Logout?", Log, No);
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
        if (result.orElse(No) == Log) {
            database.setKey("");

            stages.stream().forEach((x) -> {
                x.close();
            });
        } else {
            stage.setAlwaysOnTop(true);
        }
    }

    @FXML
    private void exit(MouseEvent event) {
        ButtonType Hide = new ButtonType("Close All");
        ButtonType Min = new ButtonType("Minimise");
        ButtonType Not = new ButtonType("Nothing");
        Alert alert = new Alert(AlertType.NONE, "Exit Options", Hide, Min, Not);
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
        if (result.orElse(Not) == Hide) {
            stages.stream().forEach((x) -> {
                x.close();
            });
        } else if (result.orElse(Not) == Min) {
            stage.setIconified(true);
        } else {
            //System.out.println("Exit clicked: Nothing");
            stage.setAlwaysOnTop(true);
        }
    }

    @FXML
    private void login(MouseEvent event) {
        String Email = email.getText();
        String Password = password.getText();

        if (Email.isEmpty() || Password.isEmpty()) {

        } else {
            String ans = database.login(Email, Password);
            if (!ans.isEmpty() && !ans.equals("Error")) {
                try {
                    database.setKey(ans);
                    (new Notes()).start(new Stage());
                    stage.close();
                } catch (Exception ex) {
                    System.out.println(ex.toString());
                }
            } else {
                System.out.println("Oof");
            }
        }

    }

    @FXML
    private void register(MouseEvent event) {
        String Email = email.getText();
        String Password = password.getText();

        if (Email.isEmpty() || Password.isEmpty()) {

        } else {
            Boolean ans = database.register(Email, Password);

            if (ans) {
                System.out.println("Yeetus");
            } else {
                System.out.println("Oof");
            }
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

    private void runUpdates() {
        stages.stream().forEach((x) -> {
            x.update();
        });
    }

    public void update(int ID) {
        if (!notes.isEmpty()) {
            notes = database.retrieveAllNotes(notes.get(0).getUser_id());
            reload(notes);
        }
        Notes.themes = database.loadThemes();
        noteClick(ID, 0);
    }
}
