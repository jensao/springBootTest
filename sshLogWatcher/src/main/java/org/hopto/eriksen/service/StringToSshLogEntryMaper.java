package org.hopto.eriksen.service;

import org.hopto.eriksen.domain.SshLogEntry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Creates SshLogEntry objects from a auth.log
 * It (shall, not that well tested) creates only one sshLogEntry object per log in attempt,
 * even when one login attempt creates multiple log rows
 *
 */
public class StringToSshLogEntryMaper implements Function<String, SshLogEntry> {


    private static Logger logger = LoggerFactory.getLogger(StringToSshLogEntryMaper.class);

    // E.g: Oct 22 10:06:38 halo sshd[3463]: Invalid user gavrilov from 211.100.124.201
    private static final String UNKNOWN_USER_REGEX = "(.*\\d\\d:\\d\\d).*sshd.*user\\s(\\w+).*from\\s(\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3})";            // staic final ...makes constants
    private static final Pattern UNKNOWN_USER_PATTERN = Pattern.compile(UNKNOWN_USER_REGEX);

    // E.g: Oct 22 10:06:35 halo sshd[3461]: Failed password for root from 211.210.124.201 port 42665 ssh2
    private static final String FAILED_PW_REGEX = "(.*\\d\\d:\\d\\d).*sshd.*Failed password for (\\w+) from\\s(\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}).*";
    private static final Pattern FAILED_PW_PATTERN = Pattern.compile(FAILED_PW_REGEX);

    // THIS WILL LEAD TO DUPLICATED SSHLOGENTRY FOR ONE LOGIN ATTEMPT...
    // E.g: Dec  4 19:57:41 halo sshd[12552]: Failed password for invalid user pi from 123.31.34.219 port 61212 ssh2
//    private static final String FAILED_PW_INVALID_USER_REGEX = "(.*\\d\\d:\\d\\d).*sshd.*Failed password for invalid user (\\w+) from\\s(\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}).*";
//    private static final Pattern FAILED_PW_INVALID_USER_PATTERN = Pattern.compile(FAILED_PW_INVALID_USER_REGEX);

    // E.g: Oct 22 19:48:36 halo sshd[6584]: Accepted publickey for jens from 192.168.1.2 port 38937 ssh2
    private static final String PUBLICKEY_ACCEPTED_REGEX = "(.*\\d\\d:\\d\\d).*sshd.*Accepted publickey for (\\w+) from (\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}).*";
    private static final Pattern PUBLICKEY_ACCEPTED_PATTERN = Pattern.compile(PUBLICKEY_ACCEPTED_REGEX);

    // E.g:	Nov  1 10:57:22 halo sshd[15214]: Accepted password for jens from 194.237.142.3 port 21250 ssh2
    private static final String PW_ACCEPTED_REGEX = "(.*\\d\\d:\\d\\d).*sshd.*Accepted password for (\\w+) from (\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}).*";
    private static final Pattern PW_ACCEPTED_PATTERN = Pattern.compile(PW_ACCEPTED_REGEX);

   	/*
     * If possible will this function create a sshLogEntry from a text string,
	 * if not possible will it return null
	 */
    @Override
    public SshLogEntry apply(String logRow) {

        Matcher unknownUserMatcher = UNKNOWN_USER_PATTERN.matcher(logRow);
        Matcher failedPwPMatcher = FAILED_PW_PATTERN.matcher(logRow);
        Matcher publicKeyAcceptedMatcher = PUBLICKEY_ACCEPTED_PATTERN.matcher(logRow);
        Matcher pwAcceptedMatcher = PW_ACCEPTED_PATTERN.matcher(logRow);
//        Matcher failedPwInvalidUserMatcher = FAILED_PW_INVALID_USER_PATTERN.matcher(logRow);

        String dateStr = "";    // The dateStr must be on format "MMM d, HH:mm:ss"
        String userName = "";
        String ipNbrStr = "";
        boolean loggedIn = true;

        if (unknownUserMatcher.matches()) {
            dateStr = unknownUserMatcher.group(1);
            userName = unknownUserMatcher.group(2);
            ipNbrStr = unknownUserMatcher.group(3);
            loggedIn = false;
            logger.trace("Successfully matched a unknownUserMatcher, info: " + dateStr + ", " + userName + ", " + ipNbrStr);
        } else if (failedPwPMatcher.matches()) {
            dateStr = failedPwPMatcher.group(1);
            userName = failedPwPMatcher.group(2);
            ipNbrStr = failedPwPMatcher.group(3);
            loggedIn = false;
            logger.trace("Successfully matched a failedPwPMatcher, info: " + dateStr + ", " + userName + ", " + ipNbrStr);
//        } else if(failedPwInvalidUserMatcher.matches()) {
//            dateStr = failedPwInvalidUserMatcher.group(1);
//            userName = failedPwInvalidUserMatcher.group(2);
//            ipNbrStr = failedPwInvalidUserMatcher.group(3);
//            loggedIn = false;
//            logger.trace("Successfully matched a failedPwInvalidUserMatcher, info: " + dateStr + ", " + userName + ", " + ipNbrStr);
        } else if (publicKeyAcceptedMatcher.matches()) {
            dateStr = publicKeyAcceptedMatcher.group(1);
            userName = publicKeyAcceptedMatcher.group(2);
            ipNbrStr = publicKeyAcceptedMatcher.group(3);
            loggedIn = true;
            logger.trace("Successfully matched a publicKeyAcceptedMatcher, info: " + dateStr + ", " + userName + ", " + ipNbrStr);
        } else if (pwAcceptedMatcher.matches()) {
            dateStr = pwAcceptedMatcher.group(1);
            userName = pwAcceptedMatcher.group(2);
            ipNbrStr = pwAcceptedMatcher.group(3);
            loggedIn = true;
            logger.trace("Successfully matched a pwAcceptedMatcher, info: " + dateStr + ", " + userName + ", " + ipNbrStr);
        }

        if (dateStr.length() > 10 && userName.length() > 0 && ipNbrStr.length() > 6) {

            LocalDateTime date = null;
            InetAddress ipNumber = null;

            try {

                // TODO if it was possible to provide a year somehow to LocalDateTime.parse
                // would all this junk below be possible to remove

                DateFormat format = new SimpleDateFormat("MMM d HH:mm:ss", Locale.US);
                Date tmpDate = format.parse(dateStr);

                int currentYear = Calendar.getInstance().get(Calendar.YEAR);    // Använd filePath to get correct year?
                Calendar c = Calendar.getInstance();
                c.setTime(tmpDate);
                c.set(Calendar.YEAR, currentYear);
                tmpDate = c.getTime();

                Instant instant = Instant.ofEpochMilli(tmpDate.getTime());
                date = LocalDateTime.ofInstant(instant, ZoneId.systemDefault());


            } catch (DateTimeParseException pe) {
                logger.error("ERROR: could not parse date in string \"" + dateStr + "\" The fault was reported as: " + pe + " failed to create sshLogEntry object");
                return null;
            } catch (ParseException e) {
                e.printStackTrace();
                return null;
            }

            try {
                ipNumber = InetAddress.getByName(ipNbrStr);
            } catch (UnknownHostException uhe) {
                logger.error("ERROR: could not create a InetAddress obj from string \"" + ipNbrStr + "\", The fault was " + uhe + " failed to create sshLogEntry object");
                return null;
            }

            // TODO TEST for second order injection here??
            return new SshLogEntry(ipNumber, date, userName, loggedIn);
        }
        return null;
    }

}
