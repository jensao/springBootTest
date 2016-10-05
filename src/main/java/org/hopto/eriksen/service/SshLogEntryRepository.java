package org.hopto.eriksen.service;

import org.hopto.eriksen.domain.SshLogEntry;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

/**
 * Created by jens on 2016-10-05.
 */
public interface SshLogEntryRepository extends PagingAndSortingRepository<SshLogEntry, Long> {
    List<SshLogEntry> findByUserName(String userName);
}
