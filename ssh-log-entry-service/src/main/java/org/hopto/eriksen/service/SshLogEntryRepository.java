package org.hopto.eriksen.service;

import org.hopto.eriksen.domain.SshLogEntry;
import org.hopto.eriksen.domain.UserNameStatistics;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.QueryByExampleExecutor;

import java.util.List;
import java.util.Map;

/**
 * Created by jens on 2016-10-05.
 *
 * JpaRepository extends PagingAndSortingRepository extends CrudRepository
 */
public interface SshLogEntryRepository extends JpaRepository<SshLogEntry, Long>, QueryByExampleExecutor<SshLogEntry> {
    List<SshLogEntry> findByUserName(String userName);

    // This is a raw (My)SQL query, not that portable
//    @Query(value = "SELECT user_name, COUNT(*) AS antal FROM ssh_log_entry GROUP BY user_name ORDER BY antal DESC", nativeQuery=true)

    @Query("SELECT s.userName, COUNT(s) AS numbers FROM SshLogEntry s GROUP BY s.userName ORDER BY numbers")
    List<Object[]> mostFrequentLoginNames();
//  List<UserNameStatistics> mostFrequentLoginNames(); // Cant get this to work maybe related to https://jira.spring.io/browse/DATAJPA/fixforversion/15763/?selectedTab=com.atlassian.jira.plugins.jira-development-integration-plugin:release-report-tabpanel
}
