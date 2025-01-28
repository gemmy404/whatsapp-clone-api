package com.whatsappclone.repository.specification;

import com.whatsappclone.entity.MessageEntity;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

public class MessageSpecification {

    public static Specification<MessageEntity> findAllMessagesByChatId(String chatId) {
        return (root, query, criteriaBuilder) -> {
            Predicate messageFilter = criteriaBuilder.equal(root.get("chat").get("id"), chatId);
            query.orderBy(criteriaBuilder.asc(root.get("createdDate")));
            return messageFilter;
        };
    }

}
