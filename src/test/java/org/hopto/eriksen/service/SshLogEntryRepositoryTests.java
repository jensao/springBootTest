package org.hopto.eriksen.service;

import org.hopto.eriksen.domain.SshLogEntry;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.time.LocalDateTime;
import java.util.List;


import static org.junit.Assert.assertEquals;

/**
 * Some silly test to learn more about jpa-data and integration tests
 *
 * See: https://spring.io/blog/2016/04/15/testing-improvements-in-spring-boot-1-4
 */
@RunWith(SpringRunner.class)    // SpringRunner is the new name for SpringJUnit4ClassRunner.class
@SpringBootTest()               // Since spring 1.4 - the new way to do it instead of @SpringApplicationConfiguration(Application.class)
public class SshLogEntryRepositoryTests {

    @Autowired
    SshLogEntryRepository sshLogEntryRepository;

    @Before
    public void init() throws UnknownHostException {
        SshLogEntry entry1 = new SshLogEntry(InetAddress.getByName("196.37.77.66"), LocalDateTime.now(), "dup", false);
        sshLogEntryRepository.save(entry1);
        SshLogEntry entry2 = new SshLogEntry(InetAddress.getByName("192.168.1.1"), LocalDateTime.now(), "root", false);
        sshLogEntryRepository.save(entry2);
    }

    @Test
    public void testFindByUserName() {
        List<SshLogEntry> sshLogEntries =  sshLogEntryRepository.findByUserName("root");
        assertEquals(1, sshLogEntries.size());
    }
}
