package com.whatsappclone.repository.specification;

import com.whatsappclone.entity.UserEntity;
import org.springframework.data.jpa.domain.Specification;

public class UserSpecification {

    public static Specification<UserEntity> findUserByEmail(String email) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("email"), email);
    }

    public static Specification<UserEntity> findAllUsersExceptSelf(String userId) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.notEqual(root.get("id"), userId);
    }

}
