package com.shark.notes;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import java.io.Serializable;
import java.util.List;

public class MainActivity extends AppCompatActivity implements Serializable {

    List<Note> notes;
    int user_id = 1;
    LinearLayout layNotes;
    int pi = 0;
    Note note;
    View Child;
    Database database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        database = new Database();
        notes = database.retrieveAllNotes(user_id);
        layNotes = findViewById(R.id.layNote);

        LoadNotes();
    }

    void NoteClicked(String ID) {
        Note note = notes.get(Integer.valueOf(ID));
        Intent i = new Intent(MainActivity.this, NoteActivity.class);
        i.putExtra("noteTitle", note.getTitle());
        i.putExtra("noteContent", note.getContent());
        i.putExtra("noteID", String.valueOf(note.getId()));
        i.putExtra("noteUser", String.valueOf(note.getUser_id()));
        startActivity(i);
    }

    void LoadNotes() {
        for (int i = 0; i < notes.size(); i++) {
            note = notes.get(i);
            Child = LayoutInflater.from(this).inflate(R.layout.noteitem, null);
            ((TextView) Child.findViewById(R.id.noteTitle)).setText(note.getTitle());
            ((TextView) Child.findViewById(R.id.noteDate)).setText(note.getDate());

            final TextView ID = (TextView) Child.findViewById(R.id.noteid);
            Child.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    NoteClicked(ID.getText().toString());
                }
            });
            ID.setText(String.valueOf(note.getList_id()));

            MainActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    layNotes.addView(Child, pi);
                }
            });
        }
    }

    public void add(View v) {
        //System.out.println("Add clicked");
        try {
            Intent i = new Intent(MainActivity.this, NoteActivity.class);

            i.putExtra("noteTitle", "");
            i.putExtra("noteContent", "");
            i.putExtra("noteID", String.valueOf(database.addNote(user_id, "", "", 1)));
            i.putExtra("noteUser", String.valueOf(1));

            startActivity(i);
        } catch (Exception ex) {
            System.out.println(ex.toString());
        }
    }

    @Override
    public void onBackPressed() {
        System.exit(0);
    }
}
