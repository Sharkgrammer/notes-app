package notes;

import javafx.stage.Stage;

public class stageControl {

    private Note note;
    private Stage stage;
    private mainController mainCon = null;
    private menuController menuCon = null;
    public static int passedByID = 0;

    public stageControl(Note note, Stage stage, mainController controller) {
        setNote(note);
        setStage(stage);
        setMainCon(controller);
    }

    public stageControl(Note note, Stage stage, menuController controller) {
        setNote(note);
        setStage(stage);
        setMenuCon(controller);
    }
    
    public stageControl(Note note, Stage stage) {
        setNote(note);
        setStage(stage);
    }

    public Note getNote() {
        return note;
    }

    public void setNote(Note note) {
        this.note = note;
    }

    public Stage getStage() {
        return stage;
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    public boolean closeStage(int ID) {
        if (ID == noteID()) {
            stage.close();
            return true;
        } else {
            return false;
        }
    }

    public int noteID() {
        if (note == null) {
            return 0;
        } else {
            return note.getId();
        }
    }

    public void setFocus() {
        stage.show();
    }

    public void update() {
        if (mainCon != null) {
            mainCon.update(note);
        } else if(menuCon != null) {
            menuCon.update(passedByID);
        }
    }
    
    public void close() {
        stage.close();
    }

    public mainController getMainCon() {
        return mainCon;
    }

    public void setMainCon(mainController mainCon) {
        this.mainCon = mainCon;
    }

    public menuController getMenuCon() {
        return menuCon;
    }

    public void setMenuCon(menuController menuCon) {
        this.menuCon = menuCon;
    }
    
    public void setPassedByID(int aPassedByID) {
        passedByID = aPassedByID;
    }

}
