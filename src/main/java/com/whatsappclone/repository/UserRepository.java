package com.whatsappclone.repository;

import com.whatsappclone.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface UserRepository extends JpaRepository<UserEntity, String>,
        JpaSpecificationExecutor<UserEntity> {
}
