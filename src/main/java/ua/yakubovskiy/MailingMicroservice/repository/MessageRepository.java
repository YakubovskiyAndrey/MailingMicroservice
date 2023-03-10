package ua.yakubovskiy.MailingMicroservice.repository;

import org.springframework.data.repository.CrudRepository;
import ua.yakubovskiy.MailingMicroservice.data.MessageData;

public interface MessageRepository extends CrudRepository<MessageData, String>, MessageRepositoryCustom {
}
