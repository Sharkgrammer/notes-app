package notes;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import static java.lang.Runtime.getRuntime;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.*;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import javax.swing.filechooser.FileSystemView;

public class Database {

    private final String USER_AGENT = "Mozilla/5.0", dir = FileSystemView.getFileSystemView().getDefaultDirectory().getPath() + "/JavaNotes";
    private final String DRIVER = "org.apache.derby.jdbc.EmbeddedDriver";
    private final String DB_URL = "jdbc:derby:notes;create=true";
    private Connection conn;
    private String key = "0";
    private final String url = "http://notesapp.gearhostpreview.com";
    private String modUrl = "notesapp.gearhostpreview.com";
    private boolean onlineCheck = false;

    public Database() {
        try {
            Class.forName(DRIVER);
            conn = DriverManager.getConnection(DB_URL);
        } catch (ClassNotFoundException | SQLException e) {
            System.out.println(e.toString());
        }
        key = getKey();
    }

    public Database(int mode) {
        Properties p = System.getProperties();
        p.setProperty("derby.system.home", dir);
        try {
            Class.forName(DRIVER);
            conn = DriverManager.getConnection(DB_URL);
        } catch (ClassNotFoundException | SQLException e) {
            System.out.println(e.toString());
        }
        Path path = Paths.get(dir + "/keys");
        if (!Files.exists(path)) {
            try {
                Files.createDirectories(path);
                System.out.println("huh");
            } catch (Exception ex) {
                System.out.println(ex.toString());
            }

            try {
                //this is annoying but better then messing with stupid files for the time being
                String createNote = "CREATE TABLE note (NOTE_ID INTEGER NOT NULL, USER_ID INTEGER NOT NULL, NOTE_TITLE LONG VARCHAR NOT NULL, NOTE_CONTENT LONG VARCHAR NOT NULL,"
                        + "NOTE_DATE VARCHAR(30) NOT NULL, NOTE_TYPE VARCHAR(20) NOT NULL, THEME_ID VARCHAR(30) NOT NULL,"
                        + "LOCAL_ID INTEGER PRIMARY KEY GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1), NOTE_CHANGED INT, NOTE_MOD VARCHAR(40))";
                String createTheme = "CREATE TABLE THEME (THEME_ID INTEGER PRIMARY KEY GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1), THEME_NAME VARCHAR(20) NOT NULL,"
                        + "PRIM_COL VARCHAR(10) NOT NULL, SECO_COL VARCHAR(10) NOT NULL, "
                        + "TEXT_COL VARCHAR(10) NOT NULL, HINT_COL VARCHAR(10) NOT NULL, ACCE_COL VARCHAR(10) NOT NULL, BUT_COL VARCHAR(20) NOT NULL)";

                try (Statement currentStatement = conn.createStatement()) {
                    currentStatement.execute(createNote);
                    currentStatement.execute(createTheme);
                }
            } catch (Exception ex) {
                System.out.println(ex.toString());
            }
        }
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
        } catch (Exception ex) {
            System.out.println(ex.toString());
        }
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

        return response.equals("Table Updated");
    }

    public int addNote(int user_id, String title, String content, int type) {
        return addNote(user_id, title, content, type, false, 1, 0, "", 0, "");
    }

    public int addNoteLocal(Note note) {
        return addNote(note.getUser_id(), note.getTitle(), note.getContent(), note.getType(), true,
                note.getTheme_id(), note.getId(), note.getDate(), note.getChanged(), note.getModified());
    }

    private int addNote(int user_id, String title, String content, int type, boolean local, int theme, int note_id, String date, int changed, String modified) {
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
        parms.add("theme");
        parms.add(String.valueOf(theme));
        parms.add("noteID");
        parms.add(String.valueOf(note_id));
        parms.add("date");
        parms.add(date);
        parms.add("changed");
        parms.add(String.valueOf(changed));
        parms.add("modifed");
        parms.add(modified);
        String response = "";

        try {
            if (local) {
                response = sendLocal(parms);
            } else {
                if (!Notes.online) {
                    return 0;
                }
                response = sendPost(parms);
            }
        } catch (Exception ex) {
            System.out.println(ex.toString());
        }

        return Integer.valueOf(response);
    }

    public List<Note> retrieveAllNotes(int ID) {
        return retrieveAllNotes(ID, false);
    }

    private List<Note> retrieveAllLocalNotes(int ID) {
        return retrieveAllNotes(ID, true);
    }

    private List<Note> retrieveAllNotes(int ID, boolean local) {
        List<Note> noteList = new ArrayList<>();
        List<String> parms = new ArrayList<>();
        parms.add("type");
        parms.add("4");
        parms.add("ID");
        parms.add(String.valueOf(ID));
        String response = "";
        try {
            if (local) {
                response = sendLocal(parms);
            } else {
                onlineCheck = true;
                response = sendGet(parms);
            }
        } catch (Exception ex) {
            System.out.println(ex.toString());
        }

        if (response.equals("Error")) {
            return noteList;
        }

        flushLocalDatabase(true);

        Note note;
        int noteid = 0;
        for (String noteStr : response.split("/split2/")) {
            note = new Note();

            //System.out.println("NoteStr: " + noteStr);
            int notePointer = 0;
            String[] noteArr = noteStr.split("/split1/");
            note.setList_id(noteid++);
            note.setId(Integer.valueOf(noteArr[notePointer++]));
            note.setUser_id(Integer.valueOf(noteArr[notePointer++]));
            note.setTitle(noteArr[notePointer++]);
            note.setContent(noteArr[notePointer++].replace("/para/", "\n"));
            note.setDate(toVisualDate(noteArr[notePointer++], "-"));
            note.setType(Integer.valueOf(noteArr[notePointer++]));
            note.setTheme_id(Integer.valueOf(noteArr[notePointer++]));
            //System.out.println("Changed: " + noteArr[notePointer]);
            note.setChanged(Integer.valueOf(noteArr[notePointer++]));
            note.setModified(noteArr[notePointer++]);
            note.setLocal_id(addNoteLocal(note));
            noteList.add(note);
        }

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

        return response.equals("deleted");
    }

    public void updateNote(Note note) {
        System.out.println("halp with this update");
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
        parms.add("local");
        parms.add(String.valueOf(note.getLocal_id()));
        String response = "";
        try {
            //response = sendPost(parms);
            if (note.getId() == 0) {
                response = sendLocal(parms);
            } else {
                //System.out.println("halp with this update");
                response = sendPost(parms);
            }
        } catch (Exception ex) {
            System.out.println(ex.toString());
        }
    }

    public List<Theme> loadThemes() {
        List<Theme> themes = new ArrayList<>();
        List<String> parms = new ArrayList<>();
        parms.add("type");
        parms.add("7");
        String response = "";
        try {
            response = sendPost(parms);
        } catch (Exception ex) {
            System.out.println(ex.toString());
        }

        flushLocalDatabase(false);

        Theme theme;
        for (String x : response.split(";")) {
            String[] y = x.split(",");
            theme = new Theme();

            int yPointer = 0;
            theme.setTheme_id(Integer.parseInt(y[yPointer++]));
            theme.setName(y[yPointer++]);
            theme.setPrimaryColour(y[yPointer++]);
            theme.setSecondaryColour(y[yPointer++]);
            theme.setTextColour(y[yPointer++]);
            theme.setHintColour(y[yPointer++]);
            theme.setAccentColour(y[yPointer++]);
            theme.setButtonColour(y[yPointer++]);

            try {
                parms = new ArrayList<>();
                //the first one doesn't matter for this internal call
                parms.add("f");
                parms.add("20");
                parms.add("f");
                parms.add(theme.getPrimaryColour());
                parms.add("f");
                parms.add(theme.getSecondaryColour());
                parms.add("f");
                parms.add(theme.getTextColour());
                parms.add("f");
                parms.add(theme.getHintColour());
                parms.add("f");
                parms.add(theme.getAccentColour());
                parms.add("f");
                parms.add(theme.getButtonColour());
                parms.add("f");
                parms.add(theme.getName());

                sendLocal(parms);
            } catch (Exception e) {
                //hmm
            }

            themes.add(theme);
        }

        return themes;
    }

    public void updateTheme(Note note) {
        List<String> parms = new ArrayList<>();
        parms.add("type");
        parms.add("8");
        parms.add("note");
        parms.add(String.valueOf(note.getId()));
        parms.add("theme");
        parms.add(String.valueOf(note.getTheme_id()));
        String response = "";
        try {
            //response = sendPost(parms);
            response = sendPost(parms);
        } catch (Exception ex) {
            System.out.println(ex.toString());
        }
    }

    private String sendPost(List<String> parms) throws Exception {
        boolean temp = !isOnline(parms);
        System.out.println("Post temp: " + temp);
        if (temp) {
            System.out.println("sendPostLocal");
            return sendLocal(parms);
        }
        
        System.out.println("sendPostOnline");
        
        parms.add("key");
        parms.add(key);

        URL obj = new URL(url);
        String data = "";
        StringBuilder response = new StringBuilder();

        for (int x = 0; x < parms.size(); x += 2) {
            if (x != 0) {
                data += "&";
            }
            data += URLEncoder.encode(parms.get(x), "UTF-8") + "=" + URLEncoder.encode(parms.get(x + 1), "UTF-8");
        }

        try {
            System.out.println(data);
            URLConnection conn = obj.openConnection();
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
                System.out.println("Post res: " + response);
            }
        } catch (Exception e) {
            System.out.println(e.toString());
        }

        //System.out.println(response);
        return response.toString().replace("\"", "");
    }

    private String sendGet(List<String> parms) throws Exception {
        if (!isOnline(parms)) {
            System.out.println("sendGetLocal");
            return sendLocal(parms);
        }

        parms.add("key");
        parms.add(key);

        String urlParameters = "";

        for (int x = 0; x < parms.size(); x += 2) {
            if (x != 0) {
                urlParameters += "&";
            }
            urlParameters += parms.get(x) + "=" + parms.get(x + 1);
        }

        String urlGet = url + "?" + urlParameters;

        //System.out.println(url);
        URL obj = new URL(urlGet);
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
        return response.toString();
    }

    private boolean isOnline(List<String> parms) {
        try {
            Process process = getRuntime().exec("ping " + modUrl);
            int connected = process.waitFor();
            boolean onlinetemp = connected == 0;
            
            if (onlinetemp != Notes.online){
                Notes.online = onlinetemp;
                Notes.initial = true;
                System.out.println("huh?");
            }
            
            System.out.println("Online: " + Notes.online);
            System.out.println("OnlineCheck: " + onlineCheck);
            System.out.println("initial: " + Notes.initial);
            
            if (onlineCheck && Notes.online && Notes.initial) {
                onlineCheck = false;
                Notes.initial = false;
                System.out.println("Checks get ran " + Notes.initial);
                //checks are run here
                String send = "", newNotes = "", delNotes = "", id = parms.get(3);
                List<Note> noteList = retrieveAllLocalNotes(Integer.parseInt(id));

                if (noteList.isEmpty()) {
                    return Notes.online;
                }

                for (Note note : noteList) {
                    if (note.getChanged() == 1) {
                        send += note.getId() + ";";
                    } else if (note.getChanged() == 2) {
                        delNotes += note.getLocal_id() + ";";
                    } else if (note.getId() == 0) {
                        newNotes += note.getLocal_id() + ";";
                    }
                    
                    System.out.println(note.getChanged());
                }
                
                System.out.println("Send " + send);
                System.out.println("New " + newNotes);
                System.out.println("Delete " + delNotes);

                if (send.equals("") && delNotes.equals("") && newNotes.equals("")) {
                    return Notes.online;
                }

                String[] respArr = null;
                String[] noteIdArr = null;
                if (!send.equals("")) {
                    noteIdArr = send.split(";");
                    List<String> intparms = new ArrayList<String>();
                    intparms.add("type");
                    intparms.add("10");
                    intparms.add("ID");
                    intparms.add(id);
                    intparms.add("IDARR");
                    intparms.add(send);

                    String response = sendGet(intparms);

                    //System.out.println("send  " + response);
                    respArr = response.split(";");
                }

                if (respArr != null) {
                    String sql = "";
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

                    //sdf.parse("2014-01-16T10:25:00");
                    java.sql.Date resDate, noteDate;
                    PreparedStatement stmnt;

                    for (int x = 0; x < respArr.length; x++) {
                        resDate = new java.sql.Date(sdf.parse(respArr[x]).getTime());

                        for (Note note : noteList) {
                            if (note.getId() == Integer.parseInt(noteIdArr[x])) {
                                noteDate = new java.sql.Date(sdf.parse(note.getModified()).getTime());

                                if (resDate.compareTo(noteDate) >= 0) {
                                    //server updates local - reset changed to false
                                    sql = "update note set note_changed = ? where note_id = ?";
                                    stmnt = conn.prepareStatement(sql);
                                    stmnt.setInt(1, 0);
                                    stmnt.setInt(2, note.getId());
                                    stmnt.executeUpdate();
                                } else {
                                    //local updates server
                                    updateNote(note);
                                    sql = "update note set note_changed = ? where note_id = ?";
                                    stmnt = conn.prepareStatement(sql);
                                    stmnt.setInt(1, 0);
                                    stmnt.setInt(2, note.getId());
                                    stmnt.executeUpdate();
                                }

                                break;
                            }
                        }
                    }
                }
                respArr = null;
                
                if (!delNotes.equals("")){
                    respArr = delNotes.split(";");
                }
                
                if (respArr != null) {
                    for (int x = 0; x < respArr.length; x++) {
                        deleteNote(Integer.parseInt(respArr[x]));
                    }
                }
                respArr = null;
                
                if (!newNotes.equals("")){
                    respArr = newNotes.split(";");
                }
                
                if (respArr != null) {
                    for (int x = 0; x < respArr.length; x++) {
                        for (Note note : noteList) {
                            if (note.getLocal_id() == Integer.parseInt(respArr[x])) {
                                // int user_id, String title, String content, int type, boolean local, int theme, int note_id, String date, int changed, String modified) {
                                addNote(Integer.parseInt(id), note.getTitle(), note.getContent(), note.getType(), false, note.getTheme_id(), 0, note.getDate(), 0, note.getModified());
                                break;
                            }
                        }
                    }
                }
            }

        } catch (Exception e) {
            System.out.println(e.toString());
        }

        return Notes.online;
    }

    private void flushLocalDatabase(boolean note) {
        try {
            Statement query = conn.createStatement();
            String sql = "";
            if (note) {
                sql = "delete from note";
                query.execute(sql);

                sql = "ALTER TABLE note ALTER COLUMN local_id RESTART WITH 1";
                query.execute(sql);
            } else {
                sql = "delete from theme";
                query.execute(sql);

                sql = "ALTER TABLE theme ALTER COLUMN theme_id RESTART WITH 1";
                query.execute(sql);
            }
        } catch (Exception e) {
            //shrug face
        }
    }

    private String sendLocal(List<String> parms) throws Exception {
        //Local database handler
        //To make my life easier it'll be modeled off of the PHP file

        String data = "";
        for (int x = 0; x < parms.size(); x += 2) {
            if (x != 0) {
                data += "&";
            }
            data += parms.get(x) + "=" + parms.get(x + 1);
        }

        String[] dataArr = data.split("&");
        int type = Integer.parseInt(dataArr[0].split("=")[1]), ntype, ID, themeID, userID, localID, counter = 1, changed;
        String queryResult = "", sql, title, content, date, modifed;
        PreparedStatement stmnt;
        Statement query;
        ResultSet rs;

        //System.out.println(data);

        //1 and 2 deal with users
        switch (type) {
            case 3:
                //insert into note
                userID = Integer.parseInt(dataArr[counter++].split("=")[1]);
                try {
                    title = dataArr[counter++].split("=")[1];
                } catch (Exception e) {
                    title = "";
                }
                try {
                    content = dataArr[counter++].split("=")[1];
                } catch (Exception e) {
                    content = "";
                }
                ntype = Integer.parseInt(dataArr[counter++].split("=")[1]);
                themeID = Integer.parseInt(dataArr[counter++].split("=")[1]);
                ID = Integer.parseInt(dataArr[counter++].split("=")[1]);
                try {
                    date = dataArr[counter].split("=")[1];
                } catch (Exception e) {
                    DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                    LocalDateTime now = LocalDateTime.now();
                    //System.out.println(dtf.format(now));
                    date = dtf.format(now);
                }
                changed = Integer.parseInt(dataArr[++counter].split("=")[1]);
                try {
                    modifed = dataArr[++counter].split("=")[1];
                } catch (Exception e) {
                    DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                    LocalDateTime now = LocalDateTime.now();
                    //System.out.println(dtf.format(now));
                    modifed = dtf.format(now);
                }

                //System.out.println(userID + " " + title + " " + content + " " + date + " " + ntype + " " + themeID + " " + ID);
                sql = "insert into note (user_id, note_title, note_content, note_date, note_type, theme_id, note_id, note_changed, note_mod) values(?, ?, ?, ?, ?, ?, ?, ?, ?)";
                stmnt = conn.prepareStatement(sql, new String[]{"LOCAL_ID"});

                stmnt.setInt(1, userID);
                stmnt.setString(2, title);
                stmnt.setString(3, content);
                stmnt.setString(4, date);
                stmnt.setInt(5, ntype);
                stmnt.setInt(6, themeID);
                stmnt.setInt(7, ID);
                stmnt.setInt(8, changed);
                stmnt.setString(9, modifed);

                stmnt.execute();

                ResultSet key = stmnt.getGeneratedKeys();
                int keyInt = 0;
                while (key.next()) {
                    keyInt += key.getInt(1);
                }
                queryResult = String.valueOf(keyInt);
                break;
            case 4:
                //get all notes from db
                sql = "select * from note where note_changed != 2";
                query = conn.createStatement();
                rs = query.executeQuery(sql);
                while (rs.next()) {
                    queryResult += rs.getInt("note_id");
                    queryResult += "/split1/";
                    queryResult += rs.getInt("user_id");
                    queryResult += "/split1/";
                    queryResult += rs.getString("note_title");
                    queryResult += "/split1/";
                    queryResult += rs.getString("note_content");
                    queryResult += "/split1/";
                    queryResult += rs.getString("note_date");
                    queryResult += "/split1/";
                    queryResult += rs.getInt("note_type");
                    queryResult += "/split1/";
                    queryResult += rs.getInt("theme_id");
                    queryResult += "/split1/";
                    queryResult += rs.getInt("note_changed");
                    queryResult += "/split1/";
                    queryResult += rs.getString("note_mod");
                    queryResult += "/split1/";
                    queryResult += rs.getInt("local_id");
                    queryResult += "/split2/";
                }
                //System.out.println(queryResult);
                break;
            case 5:
                //delete note via id
                sql = "update note set note_changed = 2 where local_id = " + dataArr[counter].split("=")[1];
                query = conn.createStatement();
                query.execute(sql);
                
                for (stageControl x : Notes.stages){
                    if (x.noteID() == Integer.valueOf(dataArr[counter].split("=")[1])){
                        x.getNote().setChanged(2);
                        break;
                    }
                }
                
                break;
            case 6:
                //update note
                //type=6&ID=133&title=fff&content=fff&ntype=0&local=2424
                ID = Integer.parseInt(dataArr[counter++].split("=")[1]);
                try {
                    title = dataArr[counter++].split("=")[1];
                } catch (Exception e) {
                    title = "";
                }
                try {
                    content = dataArr[counter++].split("=")[1];
                } catch (Exception e) {
                    content = "";
                }
                ntype = Integer.parseInt(dataArr[counter++].split("=")[1]);
                localID = Integer.parseInt(dataArr[counter++].split("=")[1]);
                java.util.Date dt = new java.util.Date();
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String currentTime = sdf.format(dt);
                sql = "update note set note_title = ?, note_content = ?, note_type = ?, note_id = ?, note_changed = ?, note_mod = ? where local_id = ?";

                //System.out.println(title + "  " + content + "  " + ID + "  " + localID);
                stmnt = conn.prepareStatement(sql);

                stmnt.setString(1, title);
                stmnt.setString(2, content);
                stmnt.setInt(3, ntype);
                stmnt.setInt(4, ID);
                stmnt.setInt(5, 1);
                stmnt.setString(6, currentTime);
                stmnt.setInt(7, localID);

                stmnt.executeUpdate();
                break;
            case 7:
                //get all themes
                sql = "select * from theme";
                query = conn.createStatement();
                rs = query.executeQuery(sql);
                while (rs.next()) {
                    queryResult += rs.getInt("theme_id");
                    queryResult += ",";
                    queryResult += rs.getString("theme_name");
                    queryResult += ",";
                    queryResult += rs.getString("prim_col");
                    queryResult += ",";
                    queryResult += rs.getString("seco_col");
                    queryResult += ",";
                    queryResult += rs.getString("text_col");
                    queryResult += ",";
                    queryResult += rs.getString("hint_col");
                    queryResult += ",";
                    queryResult += rs.getString("acce_col");
                    queryResult += ",";
                    queryResult += rs.getString("but_col");
                    queryResult += ";";
                }
                break;
            case 8:
                //update theme
                ID = Integer.parseInt(dataArr[counter++].split("=")[1]);
                themeID = Integer.parseInt(dataArr[counter++].split("=")[1]);
                sql = "update note set theme_id = ? where note_id = ?";
                stmnt = conn.prepareStatement(sql);
                stmnt.setInt(1, themeID);
                stmnt.setInt(2, ID);

                stmnt.execute();
                break;
            case 20:
                sql = "insert into theme(prim_col, seco_col, text_col, hint_col, acce_col, but_col, theme_name) values (?,?,?,?,?,?,?)";
                stmnt = conn.prepareStatement(sql);

                stmnt.setString(counter, dataArr[counter++].split("=")[1]);
                stmnt.setString(counter, dataArr[counter++].split("=")[1]);
                stmnt.setString(counter, dataArr[counter++].split("=")[1]);
                stmnt.setString(counter, dataArr[counter++].split("=")[1]);
                stmnt.setString(counter, dataArr[counter++].split("=")[1]);
                stmnt.setString(counter, dataArr[counter++].split("=")[1]);
                stmnt.setString(counter, dataArr[counter].split("=")[1]);

                stmnt.execute();

        }

        return queryResult;
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
        if (key.equals("0")) {
            return accessKey(null, false);
        } else {
            return key;
        }
    }

    private String accessKey(String key, boolean write) {
        String fileName = dir + "/keys/key.con";
        if (write) {
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
            String line, keyAccess = "";
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
