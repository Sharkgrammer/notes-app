package com.shark.notes;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
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
import java.util.ArrayList;
import java.util.List;

public class NoteActivity extends AppCompatActivity implements Serializable {

    Note note;
    int user_id;
    Database database;
    EditText title, content;
    ImageView option, save, del;
    boolean saved = true;
    Theme theme;
    List<Theme> themes = MainActivity.themes;
    ConstraintLayout noteLayContent, noteLayTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note);
        database = new Database(this);
        option = findViewById(R.id.option);
        save = findViewById(R.id.save);
        del = findViewById(R.id.exit);
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
        theme = themes.get(note.getTheme_id() - 1);
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
        option.setImageResource(getResourceId("option" + theme.getButtonColour(), "drawable"));
        del.setImageResource(getResourceId("exit" + theme.getButtonColour(), "drawable"));
        save.setImageResource(getResourceId("save" + theme.getButtonColour(), "drawable"));
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

    public void save(View v) {
        Toast.makeText(getApplicationContext(),"Saved note " + note.getTitle(), Toast.LENGTH_SHORT).show();
        note.setTitle(title.getText().toString());
        note.setContent(content.getText().toString());
        database.updateNote(note);
        saved = true;
    }

    public void option(View v){
        final int loc = themes.size() - 1;

        CharSequence[] arr = new CharSequence[themes.size()];
        for (int x = 0; x < themes.size(); x++){
            arr[loc - x] = themes.get(x).getName();
        }


        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Theme picker").setSingleChoiceItems(arr, arr.length,
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        note.setTheme_id((loc - arg1) + 1);
                        database.updateTheme(note);
                        theme = themes.get(loc - arg1);
                        cssRefresh();
                    }
                }).setCancelable(false).setNegativeButton("Okay", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
        //Creating dialog box
        AlertDialog dialog  = builder.create();
        dialog.show();

    }

    public void exit(View v) {

        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which){
                    case DialogInterface.BUTTON_POSITIVE:
                        Toast.makeText(getApplicationContext(),"Deleted note " + note.getTitle(), Toast.LENGTH_SHORT).show();
                        database.deleteNote(note.getId());
                        startActivity(new Intent(NoteActivity.this, MainActivity.class));
                        break;
                }
            }
        };

        new AlertDialog.Builder(this).setMessage("Delete note " + note.getTitle() +"?").setPositiveButton("Yes", dialogClickListener).setNegativeButton("No", dialogClickListener).show();

    }

    @Override
    public void onBackPressed() {
        if (!saved){
            save(null);
        }
        startActivity(new Intent(NoteActivity.this, MainActivity.class));
        this.finish();
    }

    private int getResourceId(String name, String res)
    {
        try {
            return getResources().getIdentifier(name, res, getPackageName());
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }

}
