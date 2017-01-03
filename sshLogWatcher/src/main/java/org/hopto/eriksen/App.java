package org.hopto.eriksen;

import org.hopto.eriksen.service.DirectoryMonitor;
import org.hopto.eriksen.service.VarLogParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.*;

/**
 * Hello world!
 *
 */
public class App {

    private static final Logger logger = LoggerFactory.getLogger(App.class);
    private static final Path path = Paths.get("/var/log/");

    public static void main(String[] args) {
        logger.info("Application started, will monitor " + path.toString());
        DirectoryMonitor directoryMonitor = new DirectoryMonitor(path);
        directoryMonitor.register(new VarLogParser());
        directoryMonitor.run();

    }
}