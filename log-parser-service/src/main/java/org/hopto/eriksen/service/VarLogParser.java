package org.hopto.eriksen.service;

import org.hopto.eriksen.domain.SshLogEntry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

/**
 * Created by jens on 2016-12-09.
 */
public class VarLogParser implements DirectoryChangedObserver {

    private static Logger logger = LoggerFactory.getLogger(VarLogParser.class);
    private final StringToSshLogEntryMaper mapper = new StringToSshLogEntryMaper();

    // Store the date and time of the last parsed SshLogEntry
    private static LocalDateTime lastUpdated = LocalDateTime.MIN;

    @Override
    // IDEA: DI the POST client func here, it will make it much easier to test this.
    public void onFileChanged(Path path) {
        if("auth.log".equals(path.getFileName().toString()) ) {
            logger.info("The auth.log has changed, will parse it");
            //path.
            try {
                Optional<SshLogEntry> lastEntry = Files.lines(path)
                        .map(mapper)
                        .filter(p -> p != null)
                        .filter(p -> p.getDate().isAfter(lastUpdated))
                        .filter(p -> {logger.info(p.toString());
                            return true;
                        })
                        // TODO Create a post rest request here !!!
                        .reduce((first, second) -> second);

                // If the sshLogEntryService is down will this be updated anyway?
                // It shall only be updated for the entries that is posted with a return code of 201
                if(lastEntry.isPresent()) {
                    lastUpdated = lastEntry.get().getDate();
                    logger.debug("Last parsed ssh log entry had a timestamp of " + lastUpdated.format(DateTimeFormatter.ISO_DATE_TIME));
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
