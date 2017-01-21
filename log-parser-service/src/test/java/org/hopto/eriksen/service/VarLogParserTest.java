package org.hopto.eriksen.service;

import org.junit.Test;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Created by jens on 2016-12-11.
 */
public class VarLogParserTest {

    @Test
    public void testCorrectDetectionAndParsingOfAFileChange() throws IOException {

        // The VarLogParser has a method that is harcoded to this file
        Path authLogPath = Paths.get("auth.log");

        write(authLogPath, ""); // Create a log file here
        DirectoryMonitor directoryMonitor = new DirectoryMonitor(authLogPath);
        directoryMonitor.register(new VarLogParser());


        // directoryMonitor.run();
        // Touch the file here directly after the blocking run above?
        // And hove shall we count what varLogParser does?

    }

    private void write(Path path, String s) throws IOException {
        try (BufferedWriter bw = Files.newBufferedWriter(path, Charset.defaultCharset())) {
            bw.write(s);
        }
    }
}
