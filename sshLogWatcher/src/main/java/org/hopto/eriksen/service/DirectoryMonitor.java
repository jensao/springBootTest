package org.hopto.eriksen.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.*;
import java.util.HashSet;
import java.util.Set;

import static java.nio.file.StandardWatchEventKinds.ENTRY_CREATE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_MODIFY;
import static java.nio.file.StandardWatchEventKinds.OVERFLOW;

/**
 * Created by jens on 2016-12-09.
 */
public class DirectoryMonitor {

    Set<DirectoryChangedObserver> observers = new HashSet<>();
    private static Logger logger = LoggerFactory.getLogger(DirectoryMonitor.class);

    Path path;
    public DirectoryMonitor(Path path) {
        this.path = path;
    }

    public void register(DirectoryChangedObserver observer) {
        observers.add(observer);
    }

    public void run() {

        WatchService watchService = null;
        try {
            watchService = FileSystems.getDefault().newWatchService();
            path.register(watchService, ENTRY_CREATE, ENTRY_MODIFY);
            logger.info("Watch Service registered for dir: " + path.toString());

            WatchKey watchKey = null;
            while (true) {
                try {
                    watchKey = watchService.take();
                } catch (InterruptedException ex) {
                    return;
                }

                for (WatchEvent<?> watchEvent : watchKey.pollEvents()) {
                    WatchEvent.Kind<?> kind = watchEvent.kind();

                    // OVERFLOW event can occur regardless if events
                    // are lost or discarded.
                    if (kind == OVERFLOW) {
                        continue;
                    }

                    if (kind == ENTRY_MODIFY) {

                        // The filename is the
                        // context of the event.
                        WatchEvent<Path> ev = (WatchEvent<Path>) watchEvent;
                        Path relativeFileName = ev.context();

//                        logger.debug("The {} file has been changed, will concat the absolute path {} to it", relativeFileName, path);
                        Path absolutePathToFile = path.resolve(relativeFileName).normalize();
                        logger.debug("The filesystem reported the file {} to have been modified as {}", absolutePathToFile, kind.name());
                        notifyObservers(absolutePathToFile);
                    }
                }

                boolean valid = watchKey.reset();
                if (!valid) {
                    break;
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void notifyObservers(Path path) {
        for(DirectoryChangedObserver observer : observers) {
            observer.onFileChanged(path);
        }
    }

}
