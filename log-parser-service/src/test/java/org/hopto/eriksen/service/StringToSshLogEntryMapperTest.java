package org.hopto.eriksen.service;

import org.hopto.eriksen.domain.SshLogEntry;
import org.junit.Assert;
import org.junit.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Month;
import java.util.Arrays;
import java.util.Calendar;
import java.util.stream.Stream;

/**
 * Created by jens on 2016-12-02.
 */
public class StringToSshLogEntryMapperTest {

    private final StringToSshLogEntryMaper mapper = new StringToSshLogEntryMaper();

    private final int currentYear = Calendar.getInstance().get(Calendar.YEAR);

    static final String invalidUserStr    = "Oct 22 10:06:38 halo sshd[3463]: Invalid user gavrilov from 211.100.124.201";
    static final String failedPassword    = "May 18 11:41:48 halo sshd[1963]: Failed password for root from 46.246.93.231 port 9472 ssh2";
    static final String acceptedPublickey = "May 18 15:08:11 halo sshd[6382]: Accepted publickey for jens from 192.168.1.2 port 33194 ssh2";
    static final String acceptedPassword  = "May 17 17:24:23 halo sshd[25515]: Accepted password for jens from 194.237.142.3 port 35079 ssh2";

    /*
     * invalidUserStr string.
     * E.g. "Oct 22 10:06:38 halo sshd[3463]: Invalid user gavrilov from 211.100.124.201";
     *
     */
    @Test
    public void testParseInvalidUserString() {
        SshLogEntry sshLogEntry = mapper.apply(invalidUserStr);

        Assert.assertEquals("gavrilov", sshLogEntry.getUserName());
        Assert.assertEquals("211.100.124.201", sshLogEntry.getIpNumber().getHostAddress() );
        Assert.assertEquals(false, sshLogEntry.isLoggedIn());

        LocalDate localDate = LocalDate.of(currentYear, Month.OCTOBER, 22);
        LocalTime localTime = LocalTime.of(10, 6, 38);
        LocalDateTime localDateTime = LocalDateTime.of(localDate, localTime);
        Assert.assertTrue(localDateTime.equals(sshLogEntry.getDate()));
    }

	/*
	 * failedPassword string
	 */
	@Test
    public void testFailedPasswordString() {
        SshLogEntry sshLogEntry = mapper.apply(failedPassword);

        Assert.assertEquals("root", sshLogEntry.getUserName());
        Assert.assertEquals("46.246.93.231", sshLogEntry.getIpNumber().getHostAddress());
        Assert.assertEquals(false, sshLogEntry.isLoggedIn());

        LocalDate localDate = LocalDate.of(currentYear, Month.MAY, 18);
        LocalTime localTime = LocalTime.of(11, 41, 48);
        LocalDateTime localDateTime = LocalDateTime.of(localDate, localTime);
        Assert.assertTrue(localDateTime.equals(sshLogEntry.getDate()));
    }

	/*
	 * acceptedPublickey  string
	  * E.g. "May 18 15:08:11 halo sshd[6382]: Accepted publickey for jens from 192.168.1.2 port 33194 ssh2";
	 */
    @Test
    public void testAcceptedPublickeyString() {
        SshLogEntry sshLogEntry = mapper.apply(acceptedPublickey);

        Assert.assertEquals("jens", sshLogEntry.getUserName() );
        Assert.assertEquals("192.168.1.2", sshLogEntry.getIpNumber().getHostAddress() );
        Assert.assertEquals(true, sshLogEntry.isLoggedIn());

        LocalDate localDate = LocalDate.of(currentYear, Month.MAY, 18);
        LocalTime localTime = LocalTime.of(15, 8, 11);
        LocalDateTime localDateTime = LocalDateTime.of(localDate, localTime);

        Assert.assertTrue(localDateTime.equals(sshLogEntry.getDate()));
    }


	/*
	 * acceptedPassword string
	 * E.g. "May 17 17:24:23 halo sshd[25515]: Accepted password for jens from 194.237.142.3 port 35079 ssh2"
	 */
    @Test
    public void testAcceptedPasswordString() {
        SshLogEntry sshLogEntry = mapper.apply(acceptedPassword);

        Assert.assertEquals("jens", sshLogEntry.getUserName());
        Assert.assertEquals("194.237.142.3", sshLogEntry.getIpNumber().getHostAddress());
        Assert.assertEquals(true, sshLogEntry.isLoggedIn());

        LocalDate localDate = LocalDate.of(currentYear, Month.MAY, 17);
        LocalTime localTime = LocalTime.of(17, 24, 23);
        LocalDateTime localDateTime = LocalDateTime.of(localDate, localTime);

        Assert.assertTrue(localDateTime.equals(sshLogEntry.getDate()));
    }

    @Test
    public void noMatchingStringShallReturnNull() {
        Assert.assertNull(mapper.apply("A non matching line"));
    }

    @Test
    public void testMultipleLinesFromDebian() {

        String[] log =  {
        "Dec  4 17:05:02 halo sshd[12448]: Failed password for root from 218.65.30.134 port 49050 ssh2",
        "Dec  4 17:05:04 halo sshd[12448]: Failed password for root from 218.65.30.134 port 49050 ssh2",
        "Dec  4 17:05:04 halo sshd[12448]: Disconnecting: Too many authentication failures for root from 218.65.30.134 port 49050 ssh2 [preauth]",
        "Dec  4 17:05:04 halo sshd[12448]: PAM 5 more authentication failures; logname= uid=0 euid=0 tty=ssh ruser= rhost=218.65.30.134  user=root",
        "Dec  4 17:05:04 halo sshd[12448]: PAM service(sshd) ignoring max retries; 6 > 3",
        "Dec  4 19:57:39 halo sshd[12552]: Address 123.31.34.219 maps to localhost, but this does not map back to the address - POSSIBLE BREAK-IN ATTEMPT!",
        "Dec  4 19:57:39 halo sshd[12552]: Invalid user pi from 123.31.34.219",
        "Dec  4 19:57:39 halo sshd[12552]: input_userauth_request: invalid user pi [preauth]",
        "Dec  4 19:57:40 halo sshd[12552]: pam_unix(sshd:auth): check pass; user unknown",
        "Dec  4 19:57:40 halo sshd[12552]: pam_unix(sshd:auth): authentication failure; logname= uid=0 euid=0 tty=ssh ruser= rhost=123.31.34.219",
        "Dec  4 19:57:41 halo sshd[12552]: Failed password for invalid user pi from 123.31.34.219 port 61212 ssh2",
        "Dec  4 19:57:42 halo sshd[12552]: error: Received disconnect from 123.31.34.219: 3: com.jcraft.jsch.JSchException: Auth fail [preauth]"
        };

//        Stream<String> logStream = Arrays.stream(log).parallel();
        Stream<String> logStream = Arrays.stream(log);

        Assert.assertEquals(3,
                logStream
                .map(mapper)
                .filter(p -> p != null)
                .count());
    }

}
