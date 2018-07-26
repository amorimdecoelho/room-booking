package com.mls.roombooking.api;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.AutoConfigureJson;
import org.springframework.boot.test.autoconfigure.json.AutoConfigureJsonTesters;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.http.MediaType.TEXT_PLAIN_VALUE;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureJson
@AutoConfigureJsonTesters
public class SmokeTest {

    @Autowired
    private MockMvc mockMvc;


    @Test
    public void respondWithEmptyTimetable() throws Exception {
        this.mockMvc.perform(post("/timetable-creation").contentType(TEXT_PLAIN_VALUE)
                .content("0900 1700\n"))
                .andExpect(status().isOk())
                .andExpect(content().string(""));
    }

    @Test
    public void respondWithTimetable() throws Exception {

        final String input = "0900 1730\n" +
                "2018-05-17 10:17:06 EMP001\n" +
                "2018-05-21 09:00 2\n" +
                "2018-05-16 12:34:56 EMP002\n" +
                "2018-05-21 09:00 2\n" +
                "2018-05-16 09:28:23 EMP003\n" +
                "2018-05-22 14:00 2\n" +
                "2018-05-17 11:23:45 EMP004\n" +
                "2018-05-22 16:00 1\n" +
                "2018-05-15 17:29:12 EMP005\n" +
                "2018-05-21 16:00 3\n" +
                "2018-05-30 17:29:12 EMP006\n" +
                "2018-05-21 10:00 3";

        final String expected = "2018-05-21\n" +
                "09:00 11:00 EMP002\n" +
                "2018-05-22\n" +
                "14:00 16:00 EMP003\n" +
                "16:00 17:00 EMP004";


        this.mockMvc.perform(post("/timetable-creation").contentType(TEXT_PLAIN_VALUE)
                .content(input))
                .andExpect(status().isOk())
                .andExpect(content().string(expected));
    }

    @Test
    public void respondWithPartialTimetable() throws Exception {

        final String input = "0900 1700\n" +
                "2018-02-17 10:17:06 EMP001\n" +
                "2018-02-21 08:00 1\n" +
                "2018-02-16 12:34:56 EMP002\n" +
                "2018-03-21 09:00 2\n" +
                "2018-04-16 12:34:56 EMP012\n" +
                "2018-05-05 19:00 5\n" +
                "2018-05-07 09:28:23 EMP003\n" +
                "2018-05-08 09:00 8\n" +
                "2018-05-09 11:23:45 EMP004\n" +
                "2018-05-11 16:00 1\n" +
                "2018-05-07 09:28:26 EMP015\n" +
                "2018-05-11 09:00 2\n" +
                "2018-05-15 17:29:12 EMP005\n" +
                "2018-05-13 16:00 3\n" +
                "2018-05-15 17:29:12 EMP006\n" +
                "2018-05-30 10:00 24\n" +
                "2018-05-17 17:29:12 EMP008\n" +
                "2018-05-30 10:00 -3\n" +
                "2018-05-19 17:29:12 EMP009\n" +
                "2018-05-30 10:00 0\n" +
                "2018-05-20 17:29:12 EMP010\n" +
                "2018-05-30 110:00 0\n" +
                "2018-05-23 17:29:12 EMP011\n" +
                "2018-05-30 10:00 ,4\n" +
                "2018-05-10 17:29:12 EMP013\n" +
                "2018-09-20 10:00 1\n" +
                "2018-05-10 17:28:12 EMP014\n" +
                "2018-09-20 09:00 1.50\n" +
                "2018-05-25 17:29:12 EMP007\n" +
                "2018-05-30 10:00 1.5";

        final String expected = "2018-03-21\n" +
                "09:00 11:00 EMP002\n" +
                "2018-05-08\n" +
                "09:00 17:00 EMP003\n" +
                "2018-05-11\n" +
                "09:00 11:00 EMP015\n" +
                "16:00 17:00 EMP004\n" +
                "2018-05-30\n" +
                "10:00 10:24 EMP011\n" +
                "2018-09-20\n" +
                "09:00 10:30 EMP014";


        this.mockMvc.perform(post("/timetable-creation").contentType(TEXT_PLAIN_VALUE)
                .content(input))
                .andExpect(status().isOk())
                .andExpect(content().string(expected));
    }

}
