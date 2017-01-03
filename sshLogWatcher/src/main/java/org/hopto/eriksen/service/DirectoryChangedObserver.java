package org.hopto.eriksen.service;

import java.nio.file.Path;

/**
 * Created by jens on 2016-12-09.
 */
public interface DirectoryChangedObserver {
    void onFileChanged(Path path);
}
