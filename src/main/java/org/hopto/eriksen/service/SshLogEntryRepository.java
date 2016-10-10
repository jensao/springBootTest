package org.hopto.eriksen.service;

import org.hopto.eriksen.domain.SshLogEntry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.QueryByExampleExecutor;

import java.util.List;

/**
 * Created by jens on 2016-10-05.
 *
 * JpaRepository extends PagingAndSortingRepository extends CrudRepository
 */
public interface SshLogEntryRepository extends JpaRepository<SshLogEntry, Long>, QueryByExampleExecutor<SshLogEntry> {
    List<SshLogEntry> findByUserName(String userName);
}
