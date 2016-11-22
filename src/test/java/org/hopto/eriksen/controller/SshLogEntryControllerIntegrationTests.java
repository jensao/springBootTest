package org.hopto.eriksen.controller;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.ReadContext;
import org.hopto.eriksen.domain.SshLogEntry;
import org.hopto.eriksen.service.SshLogEntryRepository;
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
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.charset.Charset;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.regex.Pattern;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.*;

/**
 * Created by jens on 2016-10-27.
 */
@RunWith(SpringRunner.class)												// SpringRunner is the new name for SpringJUnit4ClassRunner.class
@SpringBootTest(webEnvironment= SpringBootTest.WebEnvironment.RANDOM_PORT)	// Since spring 1.4 - the new way to do it instead of @SpringApplicationConfiguration(Application.class)
@ActiveProfiles("test")
//@TestPropertySource(locations = {"classpath:application-test.yml"})         // This only works for properties files!
public class SshLogEntryControllerIntegrationTests {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    private MediaType contentType = new MediaType(MediaType.APPLICATION_JSON.getType(),
            MediaType.APPLICATION_JSON.getSubtype(),
            Charset.forName("utf8"));

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private SshLogEntryRepository sshLogEntryRepository;

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

        log.info("The response headers looks like: " + responseEntity.getHeaders().toString());
    }

    /**
     * If two or more identically log entries are posted shall the seconde return a 400!?
     */
    @Test
    public void postOfTwoIdenticallyLogEntries() {
        // TODO implement me
    }

    /*
       Beaten bu the ugly stick...

       Lessons learned: AssertThat(...) is probably only good if you have a object, since it makes it difficult
       to read the reasons why a TC has failed when (as below) you compare to a boolean
     */
    @Test
    public void testSearchEndPoint() throws IOException {
        LocalDateTime localDateTime = LocalDateTime.now();
        InetAddress inetAddress1 = InetAddress.getByName("192.168.1.1");
        InetAddress inetAddress2 = InetAddress.getByName("192.168.1.2");

        SshLogEntry sshLogEntry1 = new SshLogEntry(inetAddress1, localDateTime.plusDays(2L), "kalle", true);
        sshLogEntryRepository.save(sshLogEntry1);
        SshLogEntry sshLogEntry2 = new SshLogEntry(inetAddress1, localDateTime.plusDays(1L), "kalle", false);
        sshLogEntryRepository.save(sshLogEntry2);
        SshLogEntry sshLogEntry3 = new SshLogEntry(inetAddress2, localDateTime, "kalle", false);
        sshLogEntryRepository.save(sshLogEntry3);

        // Teoretiskt skulle det vara bättre att getForEntitry istället returnerade Page<SshLogEntry> men det är bökigt se SO
        ResponseEntity<String> responseEntity = restTemplate.getForEntity("/sshlog/search?page=1&loggedIn=false&userName=kalle", String.class);
        log.debug("The response body from the search endpoint looks like: " + responseEntity.getBody());
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(responseEntity.getHeaders().getContentType()).isEqualTo(contentType);

        ReadContext ctx = JsonPath.parse(responseEntity.getBody());

        // Some fragile test that just concludes that it's a paged response that is returned
        assertThat((Integer) ctx.read("$.totalElements")).isSameAs(2);
        assertThat((String) ctx.read("$.sort[0].direction")).isEqualTo("ASC");
        assertThat((String) ctx.read("$.sort[0].property")).isEqualTo("date");


        // This can't be the best way to write this tests? It would have been better if the map was a SshLogEntry
        // Test that we get the newest first, it shall be sshLogEntry3
        Map first = ctx.read("$.content[0]");   // Cast this to a SshLogEntry somehow?
        assertThat((Integer) first.get("id")).isPositive();
        assertThat(first.containsValue(inetAddress2.getHostAddress())).isTrue();
        assertThat(first.containsValue(sshLogEntry3.getUserName())).isTrue();
        assertThat(first.get("userName")).isEqualTo("kalle");
        assertThat(first.get("loggedIn")).isEqualTo(false);
        assertEquals(localDateTime.format(DateTimeFormatter.ISO_DATE_TIME), first.get("date"));

        Map second = ctx.read("$.content[1]");
        assertEquals(inetAddress1.getHostAddress(), second.get("ipNumber"));

    }

}
