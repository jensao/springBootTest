package org.hopto.eriksen;

import org.hopto.eriksen.domain.SshLogEntry;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.autoconfigure.ShellProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.logging.Logger;

import static org.junit.Assert.assertEquals;

/**
 * Egentligen så borde integrations tester och unit tester vara skilda åt,
 * men denna klass skulle ju kunna användas för att köra någon typ av full integrations test?
 */
@RunWith(SpringRunner.class)												// SpringRunner is the new name for SpringJUnit4ClassRunner.class
@SpringBootTest(webEnvironment= SpringBootTest.WebEnvironment.RANDOM_PORT)	// Since spring 1.4 - the new way to do it instead of @SpringApplicationConfiguration(Application.class)
public class SpringReceptApplicationTests {

	@Autowired
	private TestRestTemplate restTemplate;


	@Test
	public void contextLoads() {
		Logger.getGlobal().info("Starting  a test ...=======================================");

		ResponseEntity<SshLogEntry> responseEntity = restTemplate.getForEntity("/sshlog", SshLogEntry.class);
		assertEquals(HttpStatus.OK, responseEntity.getStatusCode() );

		Logger.getGlobal().info("The a get request to /sshlog returnd: " + responseEntity.getBody());
	}

}
