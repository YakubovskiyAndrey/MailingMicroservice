package ua.yakubovskiy.MailingMicroservice.listener;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.kafka.core.KafkaOperations;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import ua.yakubovskiy.MailingMicroservice.MailingMicroserviceApplication;
import ua.yakubovskiy.MailingMicroservice.config.TestElasticsearchConfiguration;
import ua.yakubovskiy.MailingMicroservice.data.MessageData;
import ua.yakubovskiy.MailingMicroservice.dto.ReceivedMessage;
import ua.yakubovskiy.MailingMicroservice.repository.MessageRepository;
import ua.yakubovskiy.MailingMicroservice.service.MessageService;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;

@SpringBootTest
@DirtiesContext
@ActiveProfiles("test")
@EmbeddedKafka(partitions = 1, brokerProperties = { "listeners=PLAINTEXT://localhost:9093", "port=9093" })
@ContextConfiguration(classes = {MailingMicroserviceApplication.class, TestElasticsearchConfiguration.class})
class ReceivedListenerTest {

    @Value("${kafka.topic.messageReceived}")
    private String messageReceivedTopic;

    @Autowired
    KafkaOperations<String, ReceivedMessage> kafkaOperations;

    @Autowired
    private ElasticsearchOperations elasticsearchOperations;

    @Autowired
    MessageRepository repository;

    @SpyBean
    private MessageService service;

    @BeforeEach
    void beforeEach() {
        elasticsearchOperations.indexOps(MessageData.class).createMapping();
    }

    @AfterEach
    void afterEach() {
        elasticsearchOperations.indexOps(MessageData.class).delete();
    }

    @Test
    void testProcessReceived() {

        String subject = "some subject";
        String content = "some content";
        String transactionId = "111";
        List<String> emails = new ArrayList<>();
        emails.add("someMail@someDomen.com");

        ReceivedMessage receivedMessage = ReceivedMessage.builder()
                .subject(subject)
                .content(content)
                .emails(emails)
                .transactionId(transactionId)
                .build();

        service.processReceived(receivedMessage);

        kafkaOperations.send(messageReceivedTopic, receivedMessage);

        verify(service, times(1)).processReceived(any());

        Iterable<MessageData> dataIterable = repository.findAll();
        assertThat(dataIterable.iterator().next().getTransactionId()).isEqualTo(transactionId);
        assertThat(dataIterable.iterator().next().getContent()).isEqualTo(content);
        assertThat(dataIterable.iterator().next().getSubject()).isEqualTo(subject);
    }

}