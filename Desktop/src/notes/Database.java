package notes;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

public class Database {

    private final String USER_AGENT = "Mozilla/5.0";

    private String key = "0";

    public Database(){
        key = getKey();
    }
    public String login(String email, String password) {
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

    public boolean register(String email, String password) {
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

        Note note;
        int noteid = 0;
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

    private String sendPost(List<String> parms) throws Exception {
        parms.add("key");
        parms.add(key);
        
        String obj = "http://notesapp.gearhostpreview.com";
        URL url = new URL(obj);
        String data = "";
        StringBuilder response = new StringBuilder();

        for (int x = 0; x < parms.size(); x += 2) {
            if (x != 0) {
                data += "&";
            }
            data += URLEncoder.encode(parms.get(x), "UTF-8") + "=" + URLEncoder.encode(parms.get(x + 1), "UTF-8");
        }

        try {
            //System.out.println(data);
            URLConnection conn = url.openConnection();
            conn.setDoOutput(true);
            OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
            wr.write(data);
            wr.flush();

            BufferedReader reader = null;
            reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String line = null;

            // Read Server Response
            while ((line = reader.readLine()) != null) {
                // Append server response in string
                response.append(line);
            }
            //System.out.println(response.toString());
        } catch (Exception e) {

        }

        //System.out.println(response);
        return response.toString().replace("\"", "");
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

        //System.out.println(url);
        URL obj = new URL(url);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();

        // optional default is GET
        con.setRequestMethod("GET");

        //add request header
        con.setRequestProperty("User-Agent", USER_AGENT);

        int responseCode = con.getResponseCode();

        StringBuffer response;
        try (BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()))) {
            String inputLine;
            response = new StringBuffer();
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
        }

        //print result
        //System.out.println(response.toString());
        return response.toString();
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

    public void setKey(String key) {
        accessKey(key, true);
    }

    public String getKey() {
        return accessKey(null, false);
    }

    private String accessKey(String key, boolean write) {
        if (write) {
            String fileName = "key.con";
            try {
                FileWriter fileWriter = new FileWriter(fileName);
                try (BufferedWriter bw = new BufferedWriter(fileWriter)) {
                    bw.write(key);
                }
            } catch (Exception ex) {
                System.out.println(ex.toString());
            }

            return "";
        } else {
            String fileName = "key.con";
            String line = "", keyAccess = "";
            try {
                FileReader fileReader = new FileReader(fileName);
                try (BufferedReader br = new BufferedReader(fileReader)) {
                    while ((line = br.readLine()) != null) {
                        keyAccess = line;
                    }
                }
            } catch (Exception ex) {
                System.out.println(ex.toString());
            }

            return keyAccess;
        }
    }

}
