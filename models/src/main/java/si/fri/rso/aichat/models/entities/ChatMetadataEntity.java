package si.fri.rso.aichat.models.entities;

import javax.persistence.*;

@Entity
@Table(name = "chat_metadata")
@NamedQueries(value =
        {
                @NamedQuery(name = "ChatMetadataEntity.getAll",
                        query = "SELECT im FROM ChatMetadataEntity im")
        })
public class ChatMetadataEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;


    @Column(name = "created")
    private String created;

    @Column(name = "userCreated")
    private String userCreated;


    @Column(name = "text", length = 1000)
    private String text;

    @Column(name = "userText", length = 1000)
    private String userText;

    public Integer getChatId() {
        return id;
    }

    public void setChatId(Integer id) {
        this.id = id;
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



    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getUserText() {
        return userText;
    }

    public void setUserText(String userText) {
        this.userText = userText;
    }


}