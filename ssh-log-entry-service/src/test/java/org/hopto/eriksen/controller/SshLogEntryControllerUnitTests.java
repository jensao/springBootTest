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
import org.springframework.data.domain.Example;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.net.InetAddress;
import java.nio.charset.Charset;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.Matchers.containsString;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
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
@ActiveProfiles("test")
public class SshLogEntryControllerUnitTests {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    private MediaType contentType = new MediaType(MediaType.APPLICATION_JSON.getType(),
            MediaType.APPLICATION_JSON.getSubtype(),
            Charset.forName("utf8"));

    private SshLogEntry sshLogEntry1;
    {
        try {
            sshLogEntry1 = new SshLogEntry(InetAddress.getByName("192.168.1.3"), LocalDateTime.now(), "jens", false);
        }
        catch(java.net.UnknownHostException e) {
            fail("Foo bar: " + e);
        }
        sshLogEntry1.setId(1L);
    }

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private SshLogEntryRepository sshLogEntryRepositoryMock;

    @Test
    public void testGetEntryPoint() throws Exception{
        List<SshLogEntry> sshLogEntryList = new ArrayList<>();
        sshLogEntryList.add(sshLogEntry1);
        PageImpl<SshLogEntry> page = new PageImpl<>(sshLogEntryList);

        when(sshLogEntryRepositoryMock.findAll(any(PageRequest.class) )).thenReturn(page);

        MvcResult result =  mockMvc.perform(
                get("/sshlog")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(contentType))
                .andReturn();

        log.debug("content from the response: " + result.getResponse().getContentAsString() );

        // Shall be possible to ad a "page=2" request param
        mockMvc.perform(
                get("/sshlog?page=2")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(contentType));
    }

    @Test
    public void testGetKnownId() throws Exception {
        given(this.sshLogEntryRepositoryMock.findOne(anyLong()))
                .willReturn(sshLogEntry1);

        mockMvc.perform(get("/sshlog/1").accept(MediaType.APPLICATION_JSON))
//                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(contentType))
                .andExpect(jsonPath("$.ipNumber", is(sshLogEntry1.getIpNumber().getHostAddress())))
                .andExpect(jsonPath("$.userName", is(sshLogEntry1.getUserName())));
    }

    @Test
    public void testPostNewEntity() throws Exception {
        when(sshLogEntryRepositoryMock.save(any(SshLogEntry.class))).thenReturn(sshLogEntry1) ;

        // See: http://stackoverflow.com/questions/20504399/testing-springs-requestbody-using-spring-mockmvc
        // It took time before I figured this out :-(
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        String sshLogEntryAsStr = mapper.writeValueAsString(sshLogEntry1);
//      log.info("The json string that will be the body looks like: " + sshLogEntryAsStr);

        mockMvc.perform(post("/sshlog")
                .contentType(MediaType.APPLICATION_JSON)
                .content(sshLogEntryAsStr))
//                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(content().contentType(contentType))
                .andExpect(header().string("Location", containsString("http://localhost/sshlog/" + sshLogEntry1.getId())))   // Location header sshlog/\\d+
                .andExpect(jsonPath("$.ipNumber", is(sshLogEntry1.getIpNumber().getHostAddress())))
                .andExpect(jsonPath("$.userName", is(sshLogEntry1.getUserName())));
    }

    @Test
    public void testSearch() throws Exception {

        List<SshLogEntry> sshLogEntryList = new ArrayList<>();
        sshLogEntryList.add(sshLogEntry1);
        PageImpl<SshLogEntry> page = new PageImpl<>(sshLogEntryList);

        // TODO this gives compiler warnings, fixme
        when(sshLogEntryRepositoryMock.findAll(any(Example.class), any(PageRequest.class))).thenReturn(page);

        // Since the PageImpl is created in the test isn't mush we actual can test
        mockMvc.perform(get("/sshlog/search?page=1&loggedIn=false&userName=root"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(contentType));
    }
}
