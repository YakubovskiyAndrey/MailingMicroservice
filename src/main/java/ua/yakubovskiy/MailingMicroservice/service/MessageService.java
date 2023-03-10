package ua.yakubovskiy.MailingMicroservice.service;

import ua.yakubovskiy.MailingMicroservice.dto.ReceivedMessage;

public interface MessageService {

    void processReceived(ReceivedMessage message);

}
