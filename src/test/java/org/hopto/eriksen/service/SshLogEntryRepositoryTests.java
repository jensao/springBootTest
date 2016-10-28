package org.hopto.eriksen.service;

import org.hopto.eriksen.domain.SshLogEntry;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.time.LocalDateTime;
import java.util.List;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

/**
 * Some silly test to learn more about jpa-data and integration tests
 *
 * See: https://spring.io/blog/2016/04/15/testing-improvements-in-spring-boot-1-4
 */
@RunWith(SpringRunner.class)    // SpringRunner is the new name for SpringJUnit4ClassRunner.class
@DataJpaTest                    // This is the correct annotation to use for testing the JPA slice
public class SshLogEntryRepositoryTests {

    @Autowired
    SshLogEntryRepository sshLogEntryRepository;

    LocalDateTime dateTime2 = LocalDateTime.now();
    InetAddress inetAddress2;

    @Before
    public void init() throws UnknownHostException {
        SshLogEntry entry1 = new SshLogEntry(InetAddress.getByName("196.37.77.66"), LocalDateTime.now(),"dup", false);
        sshLogEntryRepository.save(entry1);
        inetAddress2 = InetAddress.getByName("192.168.1.1");
        SshLogEntry entry2 = new SshLogEntry(inetAddress2, dateTime2, "root", false);
        sshLogEntryRepository.save(entry2);
    }

    /**
     * Test the created "findByUserName" method
     */
    @Test
    public void testFindByUserName() throws UnknownHostException {
        SshLogEntry sshLogEntry =  sshLogEntryRepository.findByUserName("root").get(0);
        assertEquals("root", sshLogEntry.getUserName());
        assertEquals(inetAddress2, sshLogEntry.getIpNumber());
        assertEquals(dateTime2, sshLogEntry.getDate());
        assertFalse(sshLogEntry.isLoggedIn());
    }
}
