package com.whatsappclone.mapper;

import com.whatsappclone.dto.UserResponse;
import com.whatsappclone.entity.UserEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.time.LocalDateTime;
import java.util.Map;

@Mapper
public interface UserMapper {

    @Mapping(target = "isOnline", expression = "java(userEntity.isUserOnline())")
    UserResponse toUserResponse(UserEntity userEntity);

    default UserEntity fromTokenAttributes(Map<String, Object> attributes) {
        UserEntity user = new UserEntity();

        if (attributes.containsKey("sub")) {
            user.setId(attributes.get("sub").toString());
        }

        if (attributes.containsKey("given_name")) {
            user.setFirstName(attributes.get("given_name").toString());
        } else if (attributes.containsKey("nickname")) {
            // FirstName sometimes is not "given_name", like when using social login is "nickname"
            user.setFirstName(attributes.get("nickname").toString());
        }

        if (attributes.containsKey("family_name")) {
            user.setLastName(attributes.get("family_name").toString());
        }

        if (attributes.containsKey("email")) {
            user.setEmail(attributes.get("email").toString());
        }
        // Every time a user sends a request, the lastSeen is updated
        // Because this method will being called in the filter (filter -> synchronizeWithIdp -> fromTokenAttributes)
        user.setLastSeen(LocalDateTime.now());
        return user;
    }

}
