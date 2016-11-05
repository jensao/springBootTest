package org.hopto.eriksen.domain;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.net.InetAddress;
import java.time.LocalDateTime;

/**
 * A Class that represents a ssh log entry in the auth.log
 */
@Entity
public class SshLogEntry {

    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    @Column(nullable = false)
    private Long id;

    @NotNull
    @Column(nullable = false)
    private InetAddress ipNumber;

    @NotNull
    @Column(nullable = false)
    // This was needed when I used the LocalDateTime but not when only using LocalDate
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    // Change this member name to something else than "date" and you will get duplicated date records in the json
    private LocalDateTime date;

    @NotNull
    @Size(min = 2, max = 100)
    @Column(nullable = false, length = 100)
    private String userName;

    @NotNull
    @Column(nullable = false)
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
