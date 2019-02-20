package notes;

import java.util.List;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class Notes extends Application {

    int user_id = 1;
    String email;
    String key;

    @Override
    public void start(Stage stage) throws Exception {
        Database database = new Database();
        List<Note> noteList = database.retrieveAllNotes(user_id);

        if (noteList.isEmpty()) {
            makeStage(stage, new Note(), 1);
        } else {
            for (Note note : noteList) {
                stage = new Stage();
                makeStage(stage, note, 0);
            }
        }
    }

    void makeStage(Stage stage, Note note, int mode) throws Exception {
        stage.initStyle(StageStyle.UNDECORATED);
        stage.initStyle(StageStyle.TRANSPARENT);

        if (mode != 0) {
            Database database = new Database();
            note.setId(database.addNote(user_id, "", "", 1));
        }

        FXMLLoader loader = new FXMLLoader(getClass().getResource("main.fxml"));
        stage.setScene(new Scene((Parent) loader.load()));

        mainController controller = loader.<mainController>getController();
        controller.start(note, stage);

        stage.getIcons().add(new Image("assets/logo.png"));
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }

}
