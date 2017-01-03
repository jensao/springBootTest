package org.hopto.eriksen.controller;

import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.Vector;
import java.util.concurrent.atomic.AtomicLong;

import org.hopto.eriksen.domain.Greeting;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;


/**
 * CRUD REST interface for a very simple domain object.
 * Implements; GET, POST, DELETE, PATCH
 *
 * From: https://spring.io/guides/gs/rest-service/
 */
@RestController
@RequestMapping(value="/greeting")
public class GreetingController {

    private final AtomicLong counter = new AtomicLong();
    private final List<Greeting> greetings = new Vector<>();
    {
        greetings.add(new Greeting(counter.incrementAndGet(), "jens"));
        greetings.add(new Greeting(counter.incrementAndGet(), "eriksen"));
    }

    /**
     * GET - all items
     *
     * curl -i http://localhost:8080/greeting
     */
    @RequestMapping(method = RequestMethod.GET)
    public List<Greeting> getAllGreetings() {
        return greetings;
    }

    /**
     * POST - a new item
     *
     * curl -i -H "Content-Type: application/json" -X POST -d '{"content":"FooooBarrr"}' http://localhost:8080/greeting
     * See: http://spring.io/guides/tutorials/bookmarks/ for response header
     */
    // @PostMapping(consumes = "application/json")
    @RequestMapping(method = RequestMethod.POST, consumes = APPLICATION_JSON_VALUE)
    public ResponseEntity<Greeting> createGreeting(@RequestBody Greeting greeting) {

        Greeting newGreeting = new Greeting(counter.incrementAndGet(), greeting.getContent() + "_" + counter.get());
        greetings.add(newGreeting);

        //Taget fån http://spring.io/guides/tutorials/bookmarks/
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.setLocation(ServletUriComponentsBuilder
                .fromCurrentRequest().path("/{id}")
                .buildAndExpand(newGreeting.getId()).toUri());

        return new ResponseEntity<>(newGreeting, responseHeaders, HttpStatus.CREATED);

    }

    /**
     * GET - a specific item
     *
     * curl -i http://localhost:8080/greeting/1
     */
    @RequestMapping(value="/{id}", method= RequestMethod.GET)
    public Greeting getGreeting(@PathVariable("id") long id)  {

        return greetings.stream()
                .filter(g -> g.getId() == id)
                .findAny()
                .orElseThrow(() -> new NotFoundException("A greeting with id " + id + " wasn't found"));

//        for (Greeting greeting : greetings ) {
//            if(greeting.getId() == id ) {
//                return greeting;
//            }
//        }
//        throw new NotFoundException("A greeting with id " + id + " wasn't found");
    }

    /**
     * PATCH - a specific item
     *
     * curl -i -H "Content-Type: application/json" -X PATCH -d '{"content":"Updated content"}' http://localhost:8080/greeting/1
     */
    @RequestMapping(value="/{id}", method= RequestMethod.PATCH, consumes = APPLICATION_JSON_VALUE )
    public Greeting uppdateGreating(@PathVariable("id") long id, @RequestBody Greeting patchGreeting) {

        Optional<Greeting> existingGreeting = greetings.stream()
                .filter(g -> g.getId() == id).findAny();

        if(existingGreeting.isPresent()) {
            if (patchGreeting.getContent() != null && !patchGreeting.getContent().isEmpty()) {    // ...multi spaces will pass
                existingGreeting.get().setContent(patchGreeting.getContent());
                return existingGreeting.get();  // Detta genererar en 200 är det korrekt?
            }
            throw new BadRequestException("An empty content is not allowed");
        }
        throw new NotFoundException("A greeting with id " + id + " wasn't found");
    }

    /**
     * DELETE - a specific item
     *
     * curl -i -X DELETE "http://localhost:8080/greeting/1"
     */
    @RequestMapping(value="/{id}", method= RequestMethod.DELETE)
    @ResponseStatus(value = HttpStatus.OK)
    public void deleteGreeting(@PathVariable("id") long id) {
        for(Iterator<Greeting> gIterator = greetings.listIterator(); gIterator.hasNext();) {
            if(gIterator.next().getId() == id) {
                gIterator.remove();
                return;
            }
        }
        throw new NotFoundException("A greeting with id " + id + " wasn't found");
    }

}
