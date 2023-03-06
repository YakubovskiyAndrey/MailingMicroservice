package ua.yakubovskiy.MailingMicroservice.data;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;

@Getter
@Setter
@Data
@AllArgsConstructor
@NoArgsConstructor
public class MessageData {

    @Id
    private String id;

    private String subject;

    private String content;

    private String email;

    private MessageStatus status;

    private String errorMessage;
}
