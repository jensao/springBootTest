package org.hopto.eriksen.controller;

import org.hopto.eriksen.domain.SshLogEntry;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.charset.Charset;
import java.time.LocalDateTime;
import java.util.regex.Pattern;

import static org.assertj.core.api.Assertions.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Created by jens on 2016-10-27.
 */
@RunWith(SpringRunner.class)												// SpringRunner is the new name for SpringJUnit4ClassRunner.class
@SpringBootTest(webEnvironment= SpringBootTest.WebEnvironment.RANDOM_PORT)	// Since spring 1.4 - the new way to do it instead of @SpringApplicationConfiguration(Application.class)
public class SshLogEntryControllerIntegrationTests {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    private MediaType contentType = new MediaType(MediaType.APPLICATION_JSON.getType(),
            MediaType.APPLICATION_JSON.getSubtype(),
            Charset.forName("utf8"));

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    public void testPostNewEntity() throws UnknownHostException {
        InetAddress inetAddress = InetAddress.getByName("192.168.1.1");
        LocalDateTime localDateTime = LocalDateTime.now();
        SshLogEntry sshLogEntry = new SshLogEntry(inetAddress, localDateTime, "root", false);
        ResponseEntity<SshLogEntry> responseEntity = restTemplate.postForEntity("/sshlog", sshLogEntry, SshLogEntry.class);

//        assertEquals(HttpStatus.CREATED, responseEntity.getStatusCode());
//        assertTrue(responseEntity.getHeaders().getLocation().toString().contains("sshlog/"));
//        assertEquals(contentType, responseEntity.getHeaders().getContentType() );

        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(responseEntity.getHeaders().getLocation().toString()).containsPattern(Pattern.compile("sshlog/\\d+"));
        assertThat(responseEntity.getHeaders().getContentType()).isEqualTo(contentType);

        SshLogEntry entryReturned = responseEntity.getBody();
        assertThat(entryReturned.getId()).isPositive();
        assertThat(entryReturned.getDate()).isEqualTo(localDateTime);
        assertThat(entryReturned.getIpNumber()).isEqualTo(inetAddress);
        assertThat(entryReturned.getUserName()).isEqualTo("root");
        assertFalse(entryReturned.isLoggedIn());

//        assertThat()

        log.info("The response headers looks like: " + responseEntity.getHeaders().toString());
    }
}
