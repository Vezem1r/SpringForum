package com.back_end.forum.controller;

import com.back_end.forum.dto.MessageDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

@Controller
@Slf4j
public class WebsocketController {
    @MessageMapping("/hello")
    @SendTo("/message/hello")
    public MessageDto hello(MessageDto message) {
        log.info("Message accepted");
        return new MessageDto("Hello world;" + message.getValue());
    }
}