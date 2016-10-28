package org.hopto.eriksen.controller;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.hopto.eriksen.domain.SshLogEntry;
import org.hopto.eriksen.service.SshLogEntryRepository;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.net.InetAddress;
import java.nio.charset.Charset;
import java.time.LocalDateTime;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.Matchers.containsString;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyLong;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * This is the supposed way to do (semi) unit testing in spring boot.
 *
 * See: http://memorynotfound.com/unit-test-spring-mvc-rest-service-junit-mockito/#unit-test-http-get
 * for a good introduction
 */
@RunWith(SpringRunner.class)
@WebMvcTest(SshLogEntryController.class)
public class SshLogEntryControllerUnitTests {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    private MediaType contentType = new MediaType(MediaType.APPLICATION_JSON.getType(),
            MediaType.APPLICATION_JSON.getSubtype(),
            Charset.forName("utf8"));

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    SshLogEntryRepository sshLogEntryRepositoryMock;

    @Test
    @Ignore("Couldn't get the mocking to work")
    public void testGetEntryPoint() throws Exception{

        PageRequest pageRequest = new PageRequest(0, 3, Sort.Direction.DESC, "date");

        // F-ck!!
//        given(this.sshLogEntryRepositoryMock.findAll() )
//                .willReturn(Page<SshLogEntry>);

        MvcResult result =  mockMvc.perform(
                get("/sshlog")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()).andReturn();

        String foo =  result.getResponse().getHeader("Content-Type");

      assertEquals(contentType, foo);
    }

    @Test
    public void testGetKnownId() throws Exception {
        InetAddress inetAddress = InetAddress.getByName("192.168.1.1");
        LocalDateTime localDateTime = LocalDateTime.now();
        SshLogEntry sshLogEntry = new SshLogEntry(inetAddress, localDateTime, "nisse", false);

        given(this.sshLogEntryRepositoryMock.findOne(anyLong()))
                .willReturn(sshLogEntry);

        mockMvc.perform(get("/sshlog/1").accept(MediaType.APPLICATION_JSON))
//                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(contentType))
                .andExpect(jsonPath("$.ipNumber", is(sshLogEntry.getIpNumber().getHostAddress())))
                .andExpect(jsonPath("$.userName", is(sshLogEntry.getUserName())));
    }

    @Test
    public void testPostNewEntity() throws Exception {
        InetAddress inetAddress = InetAddress.getByName("192.168.1.1");
        LocalDateTime localDateTime = LocalDateTime.now();
        SshLogEntry sshLogEntry = new SshLogEntry(inetAddress, localDateTime, "perta", false);
        sshLogEntry.setId(1L);

        when(sshLogEntryRepositoryMock.save(any(SshLogEntry.class))).thenReturn(sshLogEntry) ;

        // See: http://stackoverflow.com/questions/20504399/testing-springs-requestbody-using-spring-mockmvc
        // It took time before I figured this out :-(
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        String sshLogEntryAsStr = mapper.writeValueAsString(sshLogEntry);
//      log.info("The json string that will be the body looks like: " + sshLogEntryAsStr);

        mockMvc.perform(post("/sshlog")
                .contentType(MediaType.APPLICATION_JSON)
                .content(sshLogEntryAsStr))
//                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(content().contentType(contentType))
                .andExpect(header().string("Location", containsString("http://localhost/sshlog/" + sshLogEntry.getId())))   // Location header sshlog/\\d+
                .andExpect(jsonPath("$.ipNumber", is(inetAddress.getHostAddress())))
                .andExpect(jsonPath("$.userName", is(sshLogEntry.getUserName())));
    }
}
