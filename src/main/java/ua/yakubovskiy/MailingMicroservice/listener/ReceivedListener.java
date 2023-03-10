package ua.yakubovskiy.MailingMicroservice.listener;


import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import ua.yakubovskiy.MailingMicroservice.dto.ReceivedMessage;
import ua.yakubovskiy.MailingMicroservice.service.MessageService;

@Component
@RequiredArgsConstructor
public class ReceivedListener {

  private final MessageService service;

  @KafkaListener(topics = "${kafka.topic.messageReceived}")
  public void processReceived(ReceivedMessage message) {
    service.processReceived(message);
  }

}
