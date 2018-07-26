package com.mls.roombooking.controllers;

import com.mls.roombooking.services.TimetableService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.http.MediaType.TEXT_PLAIN_VALUE;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

@RestController
public class TimetableController {

    @Autowired
    private TimetableService timetableService;

    @RequestMapping(method={POST}, path = "/timetable-creation", consumes = TEXT_PLAIN_VALUE)
    public ResponseEntity<String> create(@RequestBody String body) {
        final String timetable = timetableService.createTimetable(body);
        return new ResponseEntity<>(timetable, HttpStatus.OK);
    }

}
