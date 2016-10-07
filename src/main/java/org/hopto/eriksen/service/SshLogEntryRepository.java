package org.hopto.eriksen.service;

import org.hopto.eriksen.domain.SshLogEntry;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

/**
 * Created by jens on 2016-10-05.
 *
 * JpaRepository extends PagingAndSortingRepository extends CrudRepository
 */
public interface SshLogEntryRepository extends JpaRepository<SshLogEntry, Long>, JpaSpecificationExecutor<SshLogEntry> {
    List<SshLogEntry> findByUserName(String userName);
    Page<SshLogEntry> findByUserName(String userName, Pageable pageable);
}
