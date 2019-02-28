package com.shark.notes;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import java.io.Serializable;
import java.util.List;

public class MainActivity extends AppCompatActivity implements Serializable {

    List<Note> notes;
    int user_id = 0;
    String user_key = "";
    LinearLayout layNotes;
    int pi = 0;
    Note note;
    View Child;
    Database database;
    public static List<Theme> themes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        String Key = this.getSharedPreferences("com.shark.notes", Context.MODE_PRIVATE).getString("key", "0");

        Log.wtf("huh", Key);

        if (Key.equals("0")) {
            startActivity(new Intent(MainActivity.this, LoginActivity.class));
            finish();
        }else{
            user_id = Integer.parseInt(Key.split(";")[0]);

            database = new Database(this);
            notes = database.retrieveAllNotes(user_id);
            themes = database.loadThemes();
            layNotes = findViewById(R.id.layNote);

            LoadNotes();
        }
    }

    void NoteClicked(String ID) {
        Note note = notes.get(Integer.valueOf(ID));
        System.out.println(ID);
        Intent i = new Intent(MainActivity.this, NoteActivity.class);
        i.putExtra("noteTitle", note.getTitle());
        i.putExtra("noteContent", note.getContent());
        i.putExtra("noteID", String.valueOf(note.getId()));
        i.putExtra("noteUser", String.valueOf(note.getUser_id()));
        i.putExtra("noteTheme", String.valueOf(note.getTheme_id()));
        startActivity(i);
    }

    void LoadNotes() {
        for (int i = 0; i < notes.size(); i++) {
            note = notes.get(i);
            Child = LayoutInflater.from(this).inflate(R.layout.noteitem, null);
            Theme locTheme = themes.get(note.getTheme_id() - 1);
            LinearLayout layInt = Child.findViewById(R.id.layInt);
            Drawable Drawable = layInt.getBackground();
            //layInt.setBackgroundColor(Color.parseColor(locTheme.getPrimaryColour()));
            Drawable.setColorFilter(Color.parseColor(locTheme.getPrimaryColour()), PorterDuff.Mode.MULTIPLY);

            TextView title = (TextView) Child.findViewById(R.id.noteTitle);
            TextView date = (TextView) Child.findViewById(R.id.noteDate);
            title.setText(note.getTitle());
            date.setText(note.getDate());
            title.setTextColor(Color.parseColor(locTheme.getTextColour()));
            date.setTextColor(Color.parseColor(locTheme.getTextColour()));

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

        MainActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                LinearLayout lay = new LinearLayout(MainActivity.this);
                lay.setMinimumHeight(50);
                layNotes.addView(lay);
            }
        });
    }

    public void add(View v) {
        try {
            Intent i = new Intent(MainActivity.this, NoteActivity.class);

            i.putExtra("noteTitle", "");
            i.putExtra("noteContent", "");
            i.putExtra("noteID", String.valueOf(database.addNote(user_id, "", "", 1)));
            i.putExtra("noteUser", String.valueOf(1));
            i.putExtra("noteTheme", String.valueOf(1));

            startActivity(i);
        } catch (Exception ex) {
            System.out.println(ex.toString());
        }
    }

    public void option(View v){
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which){
                    case DialogInterface.BUTTON_POSITIVE:
                        SharedPreferences prefs = MainActivity.this.getSharedPreferences("com.shark.notes", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = prefs.edit();
                        editor.putString("key", "0");
                        editor.apply();

                        startActivity(new Intent(MainActivity.this, LoginActivity.class));
                        break;
                }
            }
        };

        new AlertDialog.Builder(this).setMessage("Logout?").setPositiveButton("Yes", dialogClickListener).setNegativeButton("No", dialogClickListener).show();

    }

    @Override
    public void onBackPressed() {
        System.exit(0);
    }
}
