package org.example.productservice.command.rest;


import org.axonframework.config.EventProcessingConfigurer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/management")
public class EventsReplayController {

    @Autowired
    private EventProcessingConfigurer eventProcessingConfigurer;

    @PostMapping("/eventProcessor/processorName/reset")
    public ResponseEntity<String> replayEvents(@PathVariable String processorName) {

        return null;
    }

}
