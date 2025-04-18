package com.whatsappclone.repository.specification;

import com.whatsappclone.entity.ChatEntity;
import com.whatsappclone.enums.MessageState;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

public class ChatSpecification {

    public static Specification<ChatEntity> findAllChatsByUserId(String userId) {
        // UserId(connectedUser) can be in sender or recipient, this depends on the first one who create/initiate the chat
        return (root, query, criteriaBuilder) -> {
//            query.distinct(true);
            Predicate chatFilter = criteriaBuilder.or(
                    criteriaBuilder.equal(root.get("sender").get("id"), userId),
                    criteriaBuilder.equal(root.get("recipient").get("id"), userId));
            query.orderBy(criteriaBuilder.desc(root.get("lastModifiedDate")));
            return chatFilter;
        };
    }

    public static Specification<ChatEntity> findUnreadChatsByUserId(String userId) {
        return (root, query, criteriaBuilder) -> {
            Predicate chatFilter = criteriaBuilder.and(
                    criteriaBuilder.or(
                            criteriaBuilder.equal(root.get("sender").get("id"), userId),
                            criteriaBuilder.equal(root.get("recipient").get("id"), userId)),
                    criteriaBuilder.equal(root.get("messages").get("receiverId"), userId),
                    criteriaBuilder.equal(root.get("messages").get("state"), MessageState.SENT));

            query.orderBy(criteriaBuilder.desc(root.get("lastModifiedDate")))
                    .distinct(true);
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

    public static Specification<ChatEntity> findChatsByName(String userId, String name) {
        return (root, query, criteriaBuilder) -> {
            String searchName = "%" + name.toLowerCase().replaceAll("\\s", "") + "%";

            Expression<String> senderFullName = criteriaBuilder.lower(
                    criteriaBuilder.concat(
                            root.get("sender").get("firstName"),
                            root.get("sender").get("lastName"))
            );

            Expression<String> recipientFullName = criteriaBuilder.lower(
                    criteriaBuilder.concat(
                            root.get("recipient").get("firstName"),
                            root.get("recipient").get("lastName"))
            );

            // Fetch the chats of the current user
            Predicate userIsSender = criteriaBuilder.equal(root.get("sender").get("id"), userId);
            Predicate userIsRecipient = criteriaBuilder.equal(root.get("recipient").get("id"), userId);

            Predicate searchInRecipientName = criteriaBuilder.and(
                    userIsSender,
                    criteriaBuilder.like(recipientFullName, searchName)
            );
            Predicate searchInSenderName = criteriaBuilder.and(
                    userIsRecipient,
                    criteriaBuilder.like(senderFullName, searchName)
            );

            return criteriaBuilder.or(searchInRecipientName, searchInSenderName);
        };
    }

}
