package org.hopto.eriksen;

import org.hopto.eriksen.service.SshLogEntryRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.stereotype.Component;

@SpringBootApplication	// same as @Configuration @EnableAutoConfiguration @ComponentScan
public class Application {


	/**
	 * This is another way to initialize test data or anything similar
	 * It breaks some test due to "No qualifying bean found ... SshLogEntryRepository"
	 */
//	@Bean
//	InitializingBean populateTestData(SshLogEntryRepository sshLogEntryRepository) {
//		return () -> {
////			sshLogEntryRepository.save(new SshLogEntry(...));
//		};
//	}

	public static void main(String[] args) {
		// Tell Boot to look for sshlogentry-server.yml
//		System.setProperty("spring.config.name", "sshlogentry-server");
		SpringApplication.run(Application.class, args);
	}
}

/**
 * This is the way to run a command from the command line, e.g. to create testdata
 * java -jar target/springBootTest-0.0.1-SNAPSHOT.jar --test --option
 */
@Component
class CommandLineHandler implements CommandLineRunner {

	private final Logger log = LoggerFactory.getLogger(this.getClass());
	private final SshLogEntryRepository sshLogEntryRepository;

	@Autowired
	public CommandLineHandler(SshLogEntryRepository sshLogEntryRepository) {
		this.sshLogEntryRepository = sshLogEntryRepository;
	}

	@Override
	public void run(String ...args) throws Exception {
//		System.out.println("HERE WOULD YOU BE ABLE TO INSERT TEST VALUES");

		StringBuilder stringBuilder = new StringBuilder();
		for(String arg : args) {
			stringBuilder.append(arg).append(" ");
		}
		log.info("Provided command lines: " + stringBuilder );

		if(stringBuilder.toString().contains("test") ) {
			// Add some test values to the database using SshLogEntryRepository
			log.info("Adding some test values to the database to have something to play with");
		}
	}
}
