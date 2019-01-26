package notes;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
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

        Note note;
        for (String noteStr : response.split(";")) {
            note = new Note();
            note.setId(Integer.valueOf(noteStr.split("---")[0]));
            note.setUser_id(Integer.valueOf(noteStr.split("---")[1]));
            note.setTitle(noteStr.split("---")[2]);
            note.setContent(noteStr.split("---")[3]);
            note.setDate(noteStr.split("---")[4]);
            note.setType(Integer.valueOf(noteStr.split("---")[5]));

            noteList.add(note);
        }

        System.out.println(noteList.size());

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
        
        return response.equals("deleted");
    }

    private String sendPost(List<String> parms) throws Exception {

        String url = "http://pokergamelabs.gearhostpreview.com/notes";
        URL obj = new URL(url);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();

        //add reuqest header
        con.setRequestMethod("POST");
        con.setRequestProperty("User-Agent", USER_AGENT);
        con.setRequestProperty("Accept-Language", "en-US,en;q=0.5");

        String urlParameters = "";

        for (int x = 0; x < parms.size(); x += 2) {
            if (x != 0) {
                urlParameters += "&";
            }
            urlParameters += parms.get(x) + "=" + parms.get(x + 1);
        }

        System.out.println(urlParameters);

        // Send post request
        con.setDoOutput(true);
        try (DataOutputStream wr = new DataOutputStream(con.getOutputStream())) {
            wr.writeBytes(urlParameters);
            wr.flush();
        }

        int responseCode = con.getResponseCode();
        System.out.println("URL response: " + responseCode);

        StringBuffer response;
        try (BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()))) {
            String inputLine;
            response = new StringBuffer();
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
        }

        //print result
        System.out.println(response.toString());
        return response.toString();

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

        System.out.println(url);
        
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
        System.out.println(response.toString());

        return response.toString();
    }

}
