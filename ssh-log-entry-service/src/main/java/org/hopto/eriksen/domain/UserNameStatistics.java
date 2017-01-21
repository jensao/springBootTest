package org.hopto.eriksen.domain;

/**
 * Only used to return statistics regarding ...
 *
 * Created by jens on 2016-11-12.
 */
public class UserNameStatistics {
    private String userName;
    private Long numberOfAttempts;

    public UserNameStatistics(String userName, Long numberOfAttempts) {
        this.userName = userName;
        this.numberOfAttempts = numberOfAttempts;
    }

    @Override
    public String toString() {
        return "UserNameStatistics{" +
                "userName='" + userName + '\'' +
                ", numberOfAttempts=" + numberOfAttempts +
                '}';
    }
}
