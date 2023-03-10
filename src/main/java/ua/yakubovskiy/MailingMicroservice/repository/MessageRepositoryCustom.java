package ua.yakubovskiy.MailingMicroservice.repository;

import ua.yakubovskiy.MailingMicroservice.data.MessageData;

import java.util.List;

public interface MessageRepositoryCustom {

    List<MessageData> searchUnsent();

}
