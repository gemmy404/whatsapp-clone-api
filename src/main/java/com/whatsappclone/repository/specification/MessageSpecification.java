package com.whatsappclone.repository.specification;

import com.whatsappclone.entity.MessageEntity;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

public class MessageSpecification {

    public static Specification<MessageEntity> findAllMessagesByChatId(String chatId, String userId) {
        return (root, query, criteriaBuilder) -> {
            Predicate userIsSender = criteriaBuilder.equal(root.get("senderId"), userId);
            Predicate userIsReceiver = criteriaBuilder.equal(root.get("receiverId"), userId);

            Predicate senderMsgUnDeleted = criteriaBuilder.and(
                    criteriaBuilder.equal(root.get("chat").get("id"), chatId),
                    userIsSender,
                    criteriaBuilder.isFalse(root.get("senderDeleted"))
            );

            Predicate receiverMsgUnDeleted = criteriaBuilder.and(
                    criteriaBuilder.equal(root.get("chat").get("id"), chatId),
                    userIsReceiver,
                    criteriaBuilder.isFalse(root.get("receiverDeleted"))
            );

            Predicate messageFilter = criteriaBuilder.or(senderMsgUnDeleted, receiverMsgUnDeleted);

            query.orderBy(criteriaBuilder.asc(root.get("createdDate")));
            return messageFilter;
        };
    }

}
