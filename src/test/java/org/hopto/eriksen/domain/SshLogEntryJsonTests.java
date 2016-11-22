package org.hopto.eriksen.domain;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.net.InetAddress;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.*;

/**
 *
 *
 */
@RunWith(SpringRunner.class)
@JsonTest
@ActiveProfiles("test")
public class SshLogEntryJsonTests {

    @Autowired
    private JacksonTester<SshLogEntry> json;

    private LocalDateTime dateTime2 = LocalDateTime.now();
    private InetAddress inetAddress2;


    // NOTE!!! It's probably better to use: assertThat(this.json.write(sshLogEntry)).isEqualToJson("expected.json");
    @Test
    public void testSerialize() throws IOException {
        inetAddress2 = InetAddress.getByName("192.168.1.1");
        SshLogEntry sshLogEntry = new SshLogEntry(inetAddress2, dateTime2, "root", false);

        assertThat(json.write(sshLogEntry)).hasJsonPathStringValue("@.ipNumber");
        assertThat(this.json.write(sshLogEntry)).extractingJsonPathStringValue("@.ipNumber").isEqualTo(inetAddress2.getHostAddress());
    }
}
