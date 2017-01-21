package org.hopto.eriksen;

import org.hopto.eriksen.service.DirectoryChangedObserver;
import org.hopto.eriksen.service.DirectoryMonitor;
import org.hopto.eriksen.service.VarLogParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Created by jens on 2017-01-18.
 */
@Configuration
public class ApplicationConfiguration {

    private static final Path path = Paths.get("/var/log/");

    @Bean
    public DirectoryChangedObserver directoryChangedObserver() {
        return new VarLogParser();
    }

    @Bean
    public DirectoryMonitor directoryMonitor() {
        return new DirectoryMonitor(path);
    }

    @Bean
    public CommandLineRunner runner() {

        return new CommandLineRunner() {

            @Override
            public void run(String... args) throws Exception {
                DirectoryMonitor directoryMonitor = new DirectoryMonitor(path);
                directoryMonitor.register(new VarLogParser());
                directoryMonitor.run();
            }
        };
    }
}
