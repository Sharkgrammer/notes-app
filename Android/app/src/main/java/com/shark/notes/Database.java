package com.shark.notes;

import android.os.AsyncTask;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

public class Database {

    private final String USER_AGENT = "Mozilla/5.0";

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

        if (response.equals("Error")){
            return noteList;
        }

        Note note; int noteid = 0;

        for (String noteStr : response.split(";")) {
            note = new Note();
            note.setList_id(noteid++);
            note.setId(Integer.valueOf(noteStr.split("---")[0]));
            note.setUser_id(Integer.valueOf(noteStr.split("---")[1]));
            note.setTitle(noteStr.split("---")[2].replace("@@@", "\n"));
            note.setContent(noteStr.split("---")[3].replace("@@@", "\n"));
            note.setDate(toVisualDate(noteStr.split("---")[4], "-"));
            note.setType(Integer.valueOf(noteStr.split("---")[5]));

            noteList.add(note);
        }

        System.out.println("Notes recieved");

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
            response = sendGet(parms);
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
        parms.add(title.replace(" ", "---").replace("\n", "@@@"));
        parms.add("content");
        parms.add(content.replace(" ", "---").replace("\n", "@@@"));
        parms.add("ntype");
        parms.add(String.valueOf(type));
        String response = "";
        try {
            //response = sendPost(parms);
            response = sendGet(parms);
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
        parms.add(note.getTitle().replace(" ", "---").replace("\n", "@@@"));
        parms.add("content");
        parms.add(note.getContent().replace(" ", "---").replace("\n", "@@@"));
        parms.add("ntype");
        parms.add(String.valueOf(note.getType()));
        try {
            //response = sendPost(parms);
            sendGet(parms);
        } catch (Exception ex) {
            System.out.println(ex.toString());
        }

        System.out.println("Note Saved");
    }

    private String sendGet(List<String> parms) throws Exception {

        String url = "http://pokergamelabs.gearhostpreview.com/notes?";

        String urlParameters = "";

        for (int x = 0; x < parms.size(); x += 2) {
            if (x != 0) {
                urlParameters += "&";
            }
            urlParameters += parms.get(x) + "=" + parms.get(x + 1);
        }
        url += urlParameters;

        return new Fetcher().execute(url).get();
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