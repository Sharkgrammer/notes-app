package notes;

public class Note {

    private int id;
    private int user_id;
    private int list_id;
    private String title;
    private String content;
    private String date;
    private int type;
    private int theme_id;
    private int local_id;
    private int changed;
    private String modified;

    public Note() {
        id = 0;
        user_id = 0;
        title = "";
        content = "";
        date = "";
        type = 0;
        theme_id = 1;
        local_id = 0;
        changed = 0;
        modified = "";
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = htmlParser(title);
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = htmlParser(content);
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getList_id() {
        return list_id;
    }

    public void setList_id(int list_id) {
        this.list_id = list_id;
    }

    private String htmlParser(String text) {
        String[] textArr = text.split("&");
        for (String x : textArr) {
            try {
                x = x.split(";")[0];
                switch (x) {
                    case "amp":
                        text = text.replace("&amp;", "&");
                        break;
                    case "quot":
                        text = text.replace("&quot;", "\"");
                        break;
                    case "lt":
                        text = text.replace("&lt;", "<");
                        break;
                    case "gt":
                        text = text.replace("&gt;", ">");
                        break;
                }
            } catch (Exception e) {
                //
            }
        }
        
        if (text.contains("\\\'")){
            text = text.replace("\\\'", "\'");
        }
        
        if (text.contains("\\\\")){
            text = text.replace("\\\\", "\\");
        }

        return text;
    }

    public int getTheme_id() {
        return theme_id;
    }

    public void setTheme_id(int theme_id) {
        this.theme_id = theme_id;
    }

    public int getLocal_id() {
        return local_id;
    }

    public void setLocal_id(int local_id) {
        this.local_id = local_id;
    }

    public int getChanged() {
        return changed;
    }

    public void setChanged(int changed) {
        this.changed = changed;
    }

    public String getModified() {
        return modified;
    }

    public void setModified(String modified) {
        this.modified = modified;
    }
}
