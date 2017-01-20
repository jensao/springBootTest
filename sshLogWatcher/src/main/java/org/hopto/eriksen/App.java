package org.hopto.eriksen;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Hello world!
 *
 */
// @EnableEurekaClient
@SpringBootApplication
public class App {

    private static final Logger logger = LoggerFactory.getLogger(App.class);

    public static void main(String[] args) {

        logger.info("SpringApplication is about to start");
        SpringApplication.run(App.class, args);

//        CountDownLatch latch = new CountDownLatch(1);
    }

    // See: http://stackoverflow.com/questions/40941845/how-to-start-and-eventually-stop-a-daemon-thread-in-spring-boot
    // See: http://stackoverflow.com/questions/28017784/how-to-prevent-spring-boot-daemon-server-application-from-closing-shutting-down  (countDownLatch)
}