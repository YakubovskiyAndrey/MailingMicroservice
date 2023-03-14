package ua.yakubovskiy.MailingMicroservice.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import ua.yakubovskiy.MailingMicroservice.data.MessageData;
import ua.yakubovskiy.MailingMicroservice.data.MessageStatus;
import ua.yakubovskiy.MailingMicroservice.dto.ReceivedMessage;
import ua.yakubovskiy.MailingMicroservice.repository.MessageRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
@EnableScheduling
public class MessageServiceImpl implements MessageService{

    @Autowired
    private final JavaMailSender javaMailSender;

    @Value("${spring.mail.username}")
    private String sender;

    private final MessageRepository repository;

    @Override
    public void sendMail(MessageData data) {
        if (data.getEmails() == null || data.getEmails().isEmpty()){
            throw new IllegalArgumentException("recipient list cannot be empty");
        }
        try {
            SimpleMailMessage mailMessage = new SimpleMailMessage();
            mailMessage.setFrom(sender);
            mailMessage.setTo(data.getEmails().toArray(new String[0]));
            mailMessage.setText(data.getContent());
            mailMessage.setSubject(data.getSubject());
            javaMailSender.send(mailMessage);

            data.setStatus(MessageStatus.SENT);
        } catch (MailException e) {
            data.setStatus(MessageStatus.ERROR);
            data.setErrorMessage(e.getClass().getName()+": "+e.getMessage());
        }
        repository.save(data);
    }

    @Override
    public void processReceived(ReceivedMessage message) {
        MessageData data = new MessageData();
        data.setContent(message.getContent());
        data.setEmails(message.getEmails());
        data.setSubject(message.getSubject());
        data.setTransactionId(message.getTransactionId());
        data.setStatus(MessageStatus.NEW);
        repository.save(data);

        sendMail(data);
    }

    @Scheduled(fixedRateString = "300000")
    private void resendMessageProcess() {
        List<MessageData> dataList = repository.searchUnsent();
        if(!dataList.isEmpty()) {
            dataList.forEach(this::sendMail);
        }
    }
}
