package org.hopto.eriksen.controller;

import org.hopto.eriksen.domain.SshLogEntry;
import org.hopto.eriksen.service.SshLogEntryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

/**
 * Created by jens on 2016-10-05.
 */
@RestController
@RequestMapping(value="/sshlog")
public class SshLogEntryController {

    private final SshLogEntryRepository sshLogEntryRepository;

    @Autowired
    SshLogEntryController(SshLogEntryRepository sshLogEntryRepository) {
        this.sshLogEntryRepository = sshLogEntryRepository;
    }

    /**
     * curl -i http://localhost:8080/sshlog
     */
    @RequestMapping(method = RequestMethod.GET)
    Page<SshLogEntry> getAllEntries(){
        PageRequest pageRequest = new PageRequest(0, 10);
        return sshLogEntryRepository.findAll(pageRequest);
    }

    /**
     * curl -i -H "Content-Type: application/json" -X POST \
     * -d '{"ipNumber":"192.168.1.1", "date":"2016-10-05", "userName":"root", "loggedIn":"false"}' http://localhost:8080/sshlog
     */
    @RequestMapping(method = RequestMethod.POST, consumes = APPLICATION_JSON_VALUE)
    public ResponseEntity<SshLogEntry> createSshLogEntry(@RequestBody SshLogEntry sshLogEntry) {
        System.out.println("Received a SshLogEntry: " + sshLogEntry.toString());

        sshLogEntryRepository.save(sshLogEntry);

        // How log this instead?
        System.out.println("Saved a SshLogEntry: " + sshLogEntry.toString());

        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.setLocation(ServletUriComponentsBuilder
                .fromCurrentRequest().path("/{id}")
                .buildAndExpand(sshLogEntry.getId()).toUri());

        return new ResponseEntity<>(sshLogEntry, responseHeaders, HttpStatus.CREATED);

    }


}
