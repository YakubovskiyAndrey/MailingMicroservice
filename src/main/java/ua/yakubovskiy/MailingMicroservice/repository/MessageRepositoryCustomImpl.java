package ua.yakubovskiy.MailingMicroservice.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.Criteria;
import org.springframework.data.elasticsearch.core.query.CriteriaQuery;
import org.springframework.data.elasticsearch.core.query.Query;
import org.springframework.stereotype.Repository;
import ua.yakubovskiy.MailingMicroservice.data.MessageData;
import ua.yakubovskiy.MailingMicroservice.data.MessageStatus;

import java.util.List;


@Repository
@RequiredArgsConstructor
public class MessageRepositoryCustomImpl implements MessageRepositoryCustom{

    @Autowired
    private final ElasticsearchOperations operations;

    @Override
    public List<MessageData> searchUnsent() {

        Query query = new CriteriaQuery(Criteria
                .where(MessageData.Fields.status).is(MessageStatus.ERROR)
                .or(MessageData.Fields.status).is(MessageStatus.NEW));

        SearchHits<MessageData> hits = operations.search(query, MessageData.class);

        return hits.stream()
                .map(o -> o.getContent())
                .toList();
    }
}
