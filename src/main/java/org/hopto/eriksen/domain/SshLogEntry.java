package org.hopto.eriksen.domain;

import javax.persistence.*;
import java.net.InetAddress;
import java.time.LocalDate;

/**
 * A Class that represents a ssh log entry in the auth.log
 */
@Entity
public class SshLogEntry {


    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private Long id;

//    @XmlJavaTypeAdapter(InetAddressAdapter.class)
    @Column(nullable = false)
    private InetAddress ipNumber;

    @Column(nullable = false)
    private LocalDate date;

    @Column(nullable = false)
    private String userName;

    @Column(nullable = false)
    private boolean loggedIn;

    protected SshLogEntry() {}

    public SshLogEntry(InetAddress ipNumber, LocalDate date, String userName, boolean loggedIn) {
        this.ipNumber = ipNumber;
        this.date = date;
        this.userName = userName;
        this.loggedIn = loggedIn;
    }


    public Long getId() {
        return id;
    }

    public InetAddress getIpNumber() {
        return ipNumber;
    }

    public LocalDate getDate() {
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
                ", date=" + date +
                ", userName='" + userName + '\'' +
                ", loggedIn=" + loggedIn +
                '}';
    }
}
