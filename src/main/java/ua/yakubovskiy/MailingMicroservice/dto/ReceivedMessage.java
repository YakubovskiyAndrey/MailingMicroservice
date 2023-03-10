package ua.yakubovskiy.MailingMicroservice.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.extern.jackson.Jacksonized;

import java.util.List;

@Getter
@Builder
@Jacksonized
public class ReceivedMessage {

  private String content;

  private List<String> emails;

  private String subject;

  private String transactionId;

}
