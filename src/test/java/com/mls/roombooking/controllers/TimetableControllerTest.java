package com.mls.roombooking.controllers;

import com.mls.roombooking.services.TimetableService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.AutoConfigureJson;
import org.springframework.boot.test.autoconfigure.json.AutoConfigureJsonTesters;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.TEXT_PLAIN_VALUE;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@WebMvcTest(TimetableController.class)
@AutoConfigureJson
@AutoConfigureJsonTesters
public class TimetableControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TimetableService timetableService;

    @Test
    public void respondWithTimetabledBookings() throws Exception {
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

        when(timetableService.createTimetable(anyString())).thenReturn(expected);
        this.mockMvc.perform(post("/timetable-creation")
                .contentType(TEXT_PLAIN_VALUE)
                .content(input))
                .andExpect(status().isOk())
                .andExpect(content().string(expected));
    }
}