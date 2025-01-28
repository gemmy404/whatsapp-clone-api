package com.whatsappclone.repository;

import com.whatsappclone.entity.ChatEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface ChatRepository extends JpaRepository<ChatEntity, String>,
        JpaSpecificationExecutor<ChatEntity> {
}
