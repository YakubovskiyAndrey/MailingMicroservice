package ua.yakubovskiy.MailingMicroservice.service;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.context.ContextConfiguration;
import ua.yakubovskiy.MailingMicroservice.MailingMicroserviceApplication;
import ua.yakubovskiy.MailingMicroservice.config.TestElasticsearchConfiguration;
import ua.yakubovskiy.MailingMicroservice.data.MessageData;
import ua.yakubovskiy.MailingMicroservice.data.MessageStatus;
import ua.yakubovskiy.MailingMicroservice.repository.MessageRepository;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;

@SpringBootTest
@ContextConfiguration(classes = {MailingMicroserviceApplication.class, TestElasticsearchConfiguration.class})
class MessageServiceImplTest {

    private static final String EMAIL = "nelo9997@mail.com";
    private static final String CONTENT = "Some contents";
    private static final String SUBJECT = "Some subject";

    @Autowired
    private ElasticsearchOperations elasticsearchOperations;

    @MockBean
    private JavaMailSender javaMailSender;

    @Autowired
    private MessageRepository repository;

    private MessageService service;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        service = new MessageServiceImpl(javaMailSender, repository);
        elasticsearchOperations.indexOps(MessageData.class).createMapping();
    }

    @AfterEach
    void afterEach() {
        elasticsearchOperations.indexOps(MessageData.class).delete();
    }

    @Test
    void testGenerateAndSendMessageSuccess() {
        List<String> emails = new ArrayList<>();
        emails.add(EMAIL);

        MessageData data = new MessageData();
        data.setEmails(emails);
        data.setContent(CONTENT);
        data.setSubject(SUBJECT);

        service.sendMail(data);

        ArgumentCaptor<SimpleMailMessage> emailCaptor =
                ArgumentCaptor.forClass(SimpleMailMessage.class);
        verify(javaMailSender, times(1)).send((emailCaptor.capture()));

        List<SimpleMailMessage> actualList = emailCaptor.getAllValues();
        assertThat(actualList).hasSize(1);
        assertThat(actualList.get(0).getText()).isEqualTo(CONTENT);
        assertThat(actualList.get(0).getSubject()).isEqualTo(SUBJECT);
    }

    @Test
    void testGenerateAndSendMessageRecipientListIsEmpty() {
        MessageData data = new MessageData();
        data.setEmails(new ArrayList<>());
        data.setContent(CONTENT);
        data.setSubject(SUBJECT);

        assertThrows(IllegalArgumentException.class, () -> service.sendMail(data));
    }

    @Test
    void testGenerateAndSendMessageRecipientListIsNull() {
        MessageData data = new MessageData();
        data.setEmails(null);
        data.setContent(CONTENT);
        data.setSubject(SUBJECT);

        assertThrows(IllegalArgumentException.class, () -> service.sendMail(data));
    }

    @Test
    void testSearchUnsentMessages() {
        repository.deleteAll();

        MessageData data = new MessageData();
        data.setContent("Test");
        data.setSubject(SUBJECT);
        data.setStatus(MessageStatus.ERROR);

        MessageData dataNew = new MessageData();
        dataNew.setSubject(SUBJECT);
        dataNew.setContent("Test");
        dataNew.setStatus(MessageStatus.NEW);

        repository.save(dataNew);
        repository.save(data);

        List<MessageData> messageDataList = repository.searchUnsent();
        assertThat(messageDataList).hasSize(2);
        assertThat(messageDataList.get(0).getContent()).isEqualTo("Test");
        assertThat(messageDataList.get(1).getContent()).isEqualTo("Test");
    }
}