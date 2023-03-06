package ua.yakubovskiy.MailingMicroservice.service;

import ua.yakubovskiy.MailingMicroservice.data.MessageData;

public interface MessageService {

    String sendSimpleMail(MessageData details);

}
