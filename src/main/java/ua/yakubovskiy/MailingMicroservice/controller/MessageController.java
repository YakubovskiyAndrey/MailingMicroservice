package ua.yakubovskiy.MailingMicroservice.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ua.yakubovskiy.MailingMicroservice.data.MessageData;
import ua.yakubovskiy.MailingMicroservice.service.MessageService;

@RestController
@RequestMapping("/messages")
@RequiredArgsConstructor
public class MessageController {

    private final MessageService service;

    @PostMapping("/sendMail")
    public String sendMail(@RequestBody MessageData details) {
        String status = service.sendSimpleMail(details);
        return status;
    }

}
