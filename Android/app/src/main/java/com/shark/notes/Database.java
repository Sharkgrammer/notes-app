package com.shark.notes;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

public class Database {
    private String key = "0";

    Database(Context context){
        key = context.getSharedPreferences("com.shark.notes", Context.MODE_PRIVATE).getString("key", "0");
    }

    public String login(String email, String password){
        List<String> parms = new ArrayList<>();
        parms.add("type");
        parms.add("1");
        parms.add("username");
        parms.add(email);
        parms.add("password");
        parms.add(password);
        String response = "";
        try {
            response = sendPost(parms);
            //response = sendGet(parms);
        } catch (Exception ex) {
            System.out.println(ex.toString());
        }

        System.out.println("Login");
        return response;
    }

    public boolean register(String email, String password){
        List<String> parms = new ArrayList<>();
        parms.add("type");
        parms.add("2");
        parms.add("username");
        parms.add(email);
        parms.add("password");
        parms.add(password);
        String response = "";
        try {
            response = sendPost(parms);
            //response = sendGet(parms);
        } catch (Exception ex) {
            System.out.println(ex.toString());
        }

        System.out.println("Registered");
        return response.equals("Table Updated");
    }

    public List<Note> retrieveAllNotes(int ID) {
        List<Note> noteList = new ArrayList<>();
        List<String> parms = new ArrayList<>();
        parms.add("type");
        parms.add("4");
        parms.add("ID");
        parms.add(String.valueOf(ID));
        String response = "";
        try {
            //response = sendPost(parms);
            response = sendGet(parms);
        } catch (Exception ex) {
            System.out.println(ex.toString());
        }

        if (response.equals("Error")) {
            return noteList;
        }

        Note note; int noteid = 0;
        //System.out.println(response);
        for (String noteStr : response.split("/split2/")) {
            note = new Note();
            String[] noteArr = noteStr.split("/split1/");
            note.setList_id(noteid++);
            note.setId(Integer.valueOf(noteArr[0]));
            note.setUser_id(Integer.valueOf(noteArr[1]));
            note.setTitle(noteArr[2]);
            note.setContent(noteArr[3].replace("/para/", "\n"));
            note.setDate(toVisualDate(noteArr[4], "-"));
            note.setType(Integer.valueOf(noteArr[5]));

            noteList.add(note);
        }

        System.out.println("Notes Recieved");

        return noteList;
    }

    public boolean deleteNote(int ID) {
        List<String> parms = new ArrayList<>();
        parms.add("type");
        parms.add("5");
        parms.add("ID");
        parms.add(String.valueOf(ID));
        String response = "";
        try {
            //response = sendPost(parms);
            sendPost(parms);
        } catch (Exception ex) {
            System.out.println(ex.toString());
        }

        System.out.println("Note Deleted");
        return response.equals("deleted");
    }

    public int addNote(int user_id, String title, String content, int type) {
        List<String> parms = new ArrayList<>();
        parms.add("type");
        parms.add("3");
        parms.add("ID");
        parms.add(String.valueOf(user_id));
        parms.add("title");
        parms.add(title);
        parms.add("content");
        parms.add(content);
        parms.add("ntype");
        parms.add(String.valueOf(type));
        String response = "";
        try {
            response = sendPost(parms);
            //response = sendGet(parms);
        } catch (Exception ex) {
            System.out.println(ex.toString());
        }

        System.out.println("Note Added");
        return Integer.valueOf(response);
    }

    public void updateNote(Note note) {
        List<String> parms = new ArrayList<>();
        parms.add("type");
        parms.add("6");
        parms.add("ID");
        parms.add(String.valueOf(note.getId()));
        parms.add("title");
        parms.add(note.getTitle());
        parms.add("content");
        parms.add(note.getContent());
        parms.add("ntype");
        parms.add(String.valueOf(note.getType()));
        try {
            //response = sendPost(parms);
            sendPost(parms);
        } catch (Exception ex) {
            System.out.println(ex.toString());
        }

        System.out.println("Note Saved");
    }

    private String sendGet(List<String> parms) throws Exception {
        parms.add("key");
        parms.add(key);
        String url = "http://notesapp.gearhostpreview.com?";

        String urlParameters = "";

        for (int x = 0; x < parms.size(); x += 2) {
            if (x != 0) {
                urlParameters += "&";
            }
            urlParameters += parms.get(x) + "=" + parms.get(x + 1);
        }
        url += urlParameters;

        return new Fetcher().execute(url, "0").get();
    }

    private String sendPost(List<String> parms) throws Exception {
        parms.add("key");
        parms.add(key);


        String url = "http://notesapp.gearhostpreview.com";
        String data = "";

        for (int x = 0; x < parms.size(); x += 2) {
            if (x != 0) {
                data += "&";
            }
            data += URLEncoder.encode(parms.get(x), "UTF-8") + "=" + URLEncoder.encode(parms.get(x + 1), "UTF-8");
        }

        return new Fetcher().execute(url, data).get();
    }

    private String toVisualDate(String date, String s) {
        String returnDate = "";
        String[] dateArr = date.split(s);
        for (int x = dateArr.length - 1; x >= 0; x--) {
            returnDate += dateArr[x];
            if (x != 0) {
                returnDate += "/";
            }
        }
        return returnDate;
    }

}

class Fetcher extends AsyncTask<String, String, String> {

    @Override
    protected String doInBackground(String... args) {
        String link = args[0];
        String postData = args[1];

        if (!postData.equals("0")){
            try{
                URL url = new URL(link);
                System.out.println(postData);
                URLConnection conn = url.openConnection();
                conn.setDoOutput(true);
                OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
                wr.write(postData);
                wr.flush();

                BufferedReader reader = null;
                StringBuilder response = new StringBuilder();
                reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                String line = null;

                // Read Server Response
                while ((line = reader.readLine()) != null) {
                    // Append server response in string
                    response.append(line);
                }
                return response.toString().replace("\"", "");
            }catch (Exception ex){
                System.out.println(ex.toString());
                return ex.toString();
            }
        }else{
            try{
                HttpClient client = new DefaultHttpClient();
                HttpGet request = new HttpGet();
                request.setURI(new URI(link));
                HttpResponse response = client.execute(request);
                BufferedReader in = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
                String Boop = in.readLine();//.substring(1);
                in.close();
                if (Boop.equals("Error")){
                    return "Error";
                }
                return Boop;
            }catch (Exception ex){
                return ex.toString();
            }
        }
    }

}