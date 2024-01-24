package si.fri.rso.aichat.lib;

public class ChatMetadata {
    private Integer chatId;
    private String created;
    private String userCreated;
    private String text;
    private String userText;

    public String getText() {
        return text; }

    public void setText(String text) {
        this.text = text;
    }

    public String getUserText() {
        return userText; }

    public void setUserText(String userText) {
        this.userText = userText;
    }

    public String getCreated() {
        return created;
    }

    public void setCreated(String created) {
        this.created = created;
    }

    public String getUserCreated() {
        return userCreated;
    }

    public void setUserCreated(String userCreated) {
        this.userCreated = userCreated;
    }


    public Integer getChatId() {
        return chatId;
    }

    public void setChatId(Integer chatId) {
        this.chatId = chatId;
    }


}


