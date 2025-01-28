package com.whatsappclone.synchronizer;

import com.whatsappclone.entity.UserEntity;
import com.whatsappclone.mapper.UserMapper;
import com.whatsappclone.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserSynchronizer {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public void synchronizeWithIdp(Jwt token) { // Idp refer to Identity provider
        log.info("Synchronizing user with idp");
        getUserEmail(token).ifPresent(userEmail -> {
            log.info("Synchronizing user having email: {}", userEmail);
//            Optional<UserEntity> optionalUser = userRepository.findOne(findUserByEmail(userEmail));
            UserEntity user = userMapper.fromTokenAttributes(token.getClaims());
//            optionalUser.ifPresent(value -> user.setId(optionalUser.get().getId()));
            userRepository.save(user);
        });
    }

    private Optional<String> getUserEmail(Jwt token) {
        Map<String, Object> attributes = token.getClaims();
        if (attributes.containsKey("email")) {
            return Optional.of(attributes.get("email").toString());
        }
        return Optional.empty();
    }

}
