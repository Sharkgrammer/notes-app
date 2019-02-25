package notes;

import javafx.stage.Stage;

public class stageControl {
    private Note note;
    private Stage stage;
    
    public stageControl(Note note, Stage stage){
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
    
    public boolean closeStage(int ID){
        if (ID == noteID()){
            stage.close();
            return true;
        }else{
            return false;
        }
    }
    
    public int noteID(){
        return note.getId();
    }
    
    public void setFocus(){
        stage.show();
    }
    
    
}
