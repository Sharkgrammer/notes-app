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

    int user_id = 0;
    String email;
    String key;
    
    public static List<Theme> themes;
    
    public Notes(){
        Database database = new Database();
        String temp = database.getKey();
        themes = database.loadThemes();
        
        if (!temp.equals("")) {
            user_id = Integer.parseInt(temp.split(";")[0]);
            key = temp.split(";")[1];
        }
    }

    @Override
    public void start(Stage stage) throws Exception {
        Database database = new Database();
        String temp = database.getKey();
        
        if (temp.equals("")) {
            startUp(stage, null);
        } else {
            user_id = Integer.parseInt(temp.split(";")[0]);
            key = temp.split(";")[1];
            
            List<Note> noteList = database.retrieveAllNotes(user_id);
            startUp(stage, noteList);
        }
        /*
        if (noteList.isEmpty()) {
            makeStage(stage, new Note(), 1);
        } else {
            for (Note note : noteList) {
                stage = new Stage();
                makeStage(stage, note, 0);
            }
        }//*/
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

    void startUp(Stage stage, List<Note> notes) throws Exception {
        stage.initStyle(StageStyle.UNDECORATED);
        stage.initStyle(StageStyle.TRANSPARENT);

        FXMLLoader loader = new FXMLLoader(getClass().getResource("menu.fxml"));
        stage.setScene(new Scene((Parent) loader.load()));

        menuController controller = loader.<menuController>getController();
        controller.start(notes, stage);

        stage.getIcons().add(new Image("assets/logo.png"));
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }

}
