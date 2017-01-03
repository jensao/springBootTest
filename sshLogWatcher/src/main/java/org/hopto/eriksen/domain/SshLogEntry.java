package org.hopto.eriksen.domain;

import java.net.InetAddress;
import java.time.LocalDateTime;
//import javax.validation.constraints.Size;

/**
 * Created by jens on 2016-12-02.
 */
public class SshLogEntry {
    private Long id;

    private InetAddress ipNumber;

    // This was needed when I used the LocalDateTime but not when only using LocalDate
  //  @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    // Change this member name to something else than "date" and you will get duplicated date records in the json
    private LocalDateTime date;

    private String userName;

    private boolean loggedIn;

    protected SshLogEntry() {}

    public SshLogEntry(InetAddress ipNumber, LocalDateTime dateTime, String userName, boolean loggedIn) {
        this.ipNumber = ipNumber;
        this.date = dateTime;
        this.userName = userName;
        this.loggedIn = loggedIn;
    }

    public Long getId() {
        return id;
    }

    public InetAddress getIpNumber() {
        return ipNumber;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public String getUserName() {
        return userName;
    }

    public boolean isLoggedIn() {
        return loggedIn;
    }


    // This is needed so that the MVC endpoint can parse request parameters directly
    // into an instance of this class. Is it possible to do it without losing imutability?
    public void setId(Long id) {
        this.id = id;
    }

    public void setIpNumber(InetAddress ipNumber) {
        this.ipNumber = ipNumber;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void setLoggedIn(boolean loggedIn) {
        this.loggedIn = loggedIn;
    }

    @Override
    public String toString() {
        return "SshLogEntry{" +
                "id=" + id +
                ", ipNumber=" + ipNumber +
                ", dateTime=" + date +
                ", userName='" + userName + '\'' +
                ", loggedIn=" + loggedIn +
                '}';
    }
}
