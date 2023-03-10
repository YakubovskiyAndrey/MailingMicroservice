package ua.yakubovskiy.MailingMicroservice.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaOperations;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ua.yakubovskiy.MailingMicroservice.dto.ReceivedMessageDetails;
import ua.yakubovskiy.MailingMicroservice.dto.ReceivedMessage;
import ua.yakubovskiy.MailingMicroservice.service.MessageService;

import java.util.UUID;

@RestController
@RequestMapping("/messages")
@RequiredArgsConstructor
public class MessageController {

    private final MessageService service;

    @Value("${kafka.topic.messageReceived}")
    private String messageReceivedTopic;

    private final KafkaOperations<String, ReceivedMessage> kafkaOperations;

    @PostMapping("/sendMail")
    public void sendMail(@RequestBody ReceivedMessageDetails details) {
        kafkaOperations.send(messageReceivedTopic, toMessage(details));
    }

    private static ReceivedMessage toMessage(ReceivedMessageDetails dto) {
        return ReceivedMessage.builder()
                .subject(dto.getSubject())
                .emails(dto.getEmails())
                .content(dto.getContent())
                .transactionId(UUID.randomUUID().toString())
                .build();
    }

}
