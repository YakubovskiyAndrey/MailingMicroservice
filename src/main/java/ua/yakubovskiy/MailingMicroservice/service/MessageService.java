package ua.yakubovskiy.MailingMicroservice.service;

import ua.yakubovskiy.MailingMicroservice.data.MessageData;
import ua.yakubovskiy.MailingMicroservice.dto.ReceivedMessage;

public interface MessageService {

    void sendMail(MessageData data);

    void processReceived(ReceivedMessage message);

}
