package com.shark.notes;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import java.io.Serializable;

public class NoteActivity extends AppCompatActivity implements Serializable {

    Note note;
    int user_id = 1;
    Database database;
    EditText title, content;
    ImageView add, save, del;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note);
        database = new Database();
        add = findViewById(R.id.Add);
        save = findViewById(R.id.Save);
        del = findViewById(R.id.Exit);
        title = findViewById(R.id.titleText);
        content = findViewById(R.id.contentText);

        Intent i = getIntent();
        Note note = new Note();

        note.setTitle((String) i.getSerializableExtra("noteTitle"));
        note.setContent((String) i.getSerializableExtra("noteContent"));
        note.setId(Integer.parseInt((String) i.getSerializableExtra("noteID")));
        note.setUser_id(Integer.parseInt((String) i.getSerializableExtra("noteUser")));

        this.note = note;

        title.setText(note.getTitle());
        content.setText(note.getContent());
    }

    public void add(View v) {
        //System.out.println("Add clicked");
        try {
            Intent i = new Intent(NoteActivity.this, NoteActivity.class);

            i.putExtra("noteTitle", "");
            i.putExtra("noteContent", "");
            i.putExtra("noteID", String.valueOf(database.addNote(user_id, "", "", 1)));
            i.putExtra("noteUser", String.valueOf(1));

            startActivity(i);
        } catch (Exception ex) {
            System.out.println(ex.toString());
        }
    }

    public void option(View v) {
        //System.out.println("Option clicked");
        note.setTitle(title.getText().toString());
        note.setContent(content.getText().toString());
        database.updateNote(note);
    }

    public void exit(View v) {
        database.deleteNote(note.getId());
        startActivity(new Intent(NoteActivity.this, MainActivity.class));
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(NoteActivity.this, MainActivity.class));
    }

}
