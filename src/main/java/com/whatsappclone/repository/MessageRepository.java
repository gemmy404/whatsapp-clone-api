package com.whatsappclone.repository;

import com.whatsappclone.entity.MessageEntity;
import com.whatsappclone.enums.MessageState;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface MessageRepository extends JpaRepository<MessageEntity, Long>,
        JpaSpecificationExecutor<MessageEntity> {

    @Query(name = "SET_MESSAGES_TO_SEEN_BY_CHAT_ID")
    @Modifying
    void setMessagesToSeenByChatId(@Param("chatId") String chatId, @Param("newState") MessageState state);

}
