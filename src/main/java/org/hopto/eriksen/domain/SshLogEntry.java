package org.hopto.eriksen.domain;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.net.InetAddress;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * A Class that represents a ssh log entry in the auth.log
 */
@Entity
public class SshLogEntry {

    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private Long id;

    @Column(nullable = false)
    private InetAddress ipNumber;

    @Column(nullable = false)
    // This was needed when I used the LocalDateTime but not when only using LocalDate
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    // Change this member name to something else than "date" and you will get duplicated date records in the json
    private LocalDateTime date;

    @Column(nullable = false)
    private String userName;

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
