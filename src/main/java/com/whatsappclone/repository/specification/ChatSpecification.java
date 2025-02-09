package com.whatsappclone.repository.specification;

import com.whatsappclone.entity.ChatEntity;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

public class ChatSpecification {

    public static Specification<ChatEntity> findAllChatsBySenderId(String senderId) {
        // SenderId(connectedUser) can be in sender or recipient, this depends on the first one who create/initiate the chat
        return (root, query, criteriaBuilder) -> {
//            query.distinct(true);
            Predicate chatFilter = criteriaBuilder.or(
                    criteriaBuilder.equal(root.get("sender").get("id"), senderId),
                    criteriaBuilder.equal(root.get("recipient").get("id"), senderId));
            query.orderBy(criteriaBuilder.desc(root.get("lastModifiedDate")));
            return chatFilter;
        };
    }

    public static Specification<ChatEntity> findChatBySenderIdAndRecipientId(String senderId, String recipientId) {
        // Open specific chat when you click it
        return (root, query, criteriaBuilder) -> {
//            query.distinct(true);
            Predicate chatFilter = criteriaBuilder.or(
                    criteriaBuilder.and(
                            criteriaBuilder.equal(root.get("sender").get("id"), senderId),
                            criteriaBuilder.equal(root.get("recipient").get("id"), recipientId)),
                    criteriaBuilder.and(
                            criteriaBuilder.equal(root.get("sender").get("id"), recipientId),
                            criteriaBuilder.equal(root.get("recipient").get("id"), senderId))
            );
            query.orderBy(criteriaBuilder.desc(root.get("createdDate")));
            return chatFilter;
        };
    }

}
