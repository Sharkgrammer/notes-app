package com.shark.notes;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.Serializable;

public class NoteActivity extends AppCompatActivity implements Serializable {

    Note note;
    int user_id;
    Database database;
    EditText title, content;
    ImageView option, save, del;
    boolean saved = true;
    Theme theme;
    ConstraintLayout noteLayContent, noteLayTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note);
        database = new Database(this);
        option = findViewById(R.id.option);
        save = findViewById(R.id.Save);
        del = findViewById(R.id.Exit);
        title = findViewById(R.id.titleText);
        content = findViewById(R.id.contentText);
        noteLayContent = findViewById(R.id.noteLayContent);
        noteLayTitle = findViewById(R.id.noteLayTitle);

        Intent i = getIntent();
        Note note = new Note();

        note.setTitle((String) i.getSerializableExtra("noteTitle"));
        note.setContent((String) i.getSerializableExtra("noteContent"));
        note.setId(Integer.parseInt((String) i.getSerializableExtra("noteID")));
        note.setUser_id(Integer.parseInt((String) i.getSerializableExtra("noteUser")));
        note.setTheme_id(Integer.parseInt((String) i.getSerializableExtra("noteTheme")));
        theme = MainActivity.themes.get(note.getTheme_id() - 1);
        user_id = note.getUser_id();

        this.note = note;

        title.setText(note.getTitle());
        content.setText(note.getContent());

        cssRefresh();

        TextWatcher savedSet = new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                saved = false;
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        };

        title.addTextChangedListener(savedSet);
        content.addTextChangedListener(savedSet);
    }

    private void cssRefresh() {
        title.setTextColor(Color.parseColor(theme.getTextColour()));
        title.setHintTextColor(Color.parseColor(theme.getHintColour()));
        content.setTextColor(Color.parseColor(theme.getTextColour()));
        content.setHintTextColor(Color.parseColor(theme.getHintColour()));
        noteLayTitle.setBackgroundColor(Color.parseColor(theme.getPrimaryColour()));
        Window window = this.getWindow();
        window.setStatusBarColor(Color.parseColor(theme.getPrimaryColour()));
        noteLayContent.setBackgroundColor(Color.parseColor(theme.getSecondaryColour()));
    }

    public void add(View v) {
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
        Toast.makeText(getApplicationContext(),"Saved note " + note.getTitle(), Toast.LENGTH_SHORT).show();
        note.setTitle(title.getText().toString());
        note.setContent(content.getText().toString());
        database.updateNote(note);
        saved = true;
    }

    public void exit(View v) {
        Toast.makeText(getApplicationContext(),"Deleted note " + note.getTitle(), Toast.LENGTH_SHORT).show();
        database.deleteNote(note.getId());
        startActivity(new Intent(NoteActivity.this, MainActivity.class));
    }

    @Override
    public void onBackPressed() {
        if (!saved){
            option(null);
        }
        startActivity(new Intent(NoteActivity.this, MainActivity.class));
        this.finish();
    }

}
