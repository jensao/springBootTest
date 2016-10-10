package org.hopto.eriksen.controller;

import org.hopto.eriksen.domain.SshLogEntry;
import org.hopto.eriksen.service.SshLogEntryRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.validation.Valid;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

/**
 * Created by jens on 2016-10-05.
 */
@RestController
@RequestMapping(value="/sshlog")
public class SshLogEntryController {

    private static final int PAGE_SIZE = 5;
    private final Logger log = LoggerFactory.getLogger(this.getClass());

    private final SshLogEntryRepository sshLogEntryRepository;

    @Autowired
    SshLogEntryController(SshLogEntryRepository sshLogEntryRepository) {
        this.sshLogEntryRepository = sshLogEntryRepository;
    }

    /**
     * Responds with all sshLogEntry sorted by date in descending order.
     * The first page == 1
     *
     * curl -i http://localhost:8080/sshlog
     * curl -i http://localhost:8080/sshlog?page=2
     *
     */
    @RequestMapping(method = RequestMethod.GET)
    Page<SshLogEntry> getAllEntries(
            @RequestParam(value="page", defaultValue = "1") Integer pageNumber) {

        log.debug("Received a the page number " + pageNumber);

        PageRequest pageRequest =
                new PageRequest(pageNumber - 1, PAGE_SIZE, Sort.Direction.DESC, "date");

        return sshLogEntryRepository.findAll(pageRequest);
    }

    /**
     * Search endpoint using Example.of()
     *
     * TODO: This method can't handle a request param which contains "&ipNumber=91.224.161.103"
     *       It would also be great if a more advance search functionality was implemented, e.g Specification<T>
     *
     * http://localhost:8080/sshlog/search?page=1&loggedIn=false&userName=root
     *
     */
    @RequestMapping(value="/search", method= RequestMethod.GET)
    Page<SshLogEntry> searchEntries(
            @RequestParam(value="page", defaultValue = "1") Integer pageNumber,
            SshLogEntry search) {

        log.debug("In search endpoint, received number=" + pageNumber);
        log.debug("In search endpoint, received a search= " + search);

        PageRequest pageRequest =
                new PageRequest(pageNumber - 1, PAGE_SIZE, Sort.Direction.DESC, "date");

        Example<SshLogEntry> example = Example.of(search);

        return sshLogEntryRepository.findAll(example, pageRequest);
    }


    /**
     * curl -i -H "Content-Type: application/json" -X POST \
     * -d '{"ipNumber":"192.168.1.1", "date":"2016-10-05T12:13:14", "userName":"root", "loggedIn":"false"}' http://localhost:8080/sshlog
     */
    @RequestMapping(method = RequestMethod.POST, consumes = APPLICATION_JSON_VALUE)
    public ResponseEntity<SshLogEntry> createSshLogEntry(@Valid @RequestBody SshLogEntry sshLogEntry) {

        log.debug("SshLogEntry received for storage: " + sshLogEntry.toString());

        // TODO Validate that we don't store duplicates entries
        sshLogEntryRepository.save(sshLogEntry);

        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.setLocation(ServletUriComponentsBuilder
                .fromCurrentRequest().path("/{id}")
                .buildAndExpand(sshLogEntry.getId()).toUri());

        return new ResponseEntity<>(sshLogEntry, responseHeaders, HttpStatus.CREATED);

    }


    /**
     * GET - a specific item
     *
     * curl -i http://localhost:8080/sshlog/1
     */
    @RequestMapping(value="/{id}", method= RequestMethod.GET)
    public SshLogEntry getSshLogEntry(@PathVariable("id") long id)  {

        SshLogEntry sshLogEntry = sshLogEntryRepository.findOne(id);

        if(sshLogEntry != null) {
            log.debug("Will return a sshLogEntry: " + sshLogEntry);
            return sshLogEntry;
        }
        throw new NotFoundException("A ssh log entry with id " + id + " wasn't found");
    }

}
