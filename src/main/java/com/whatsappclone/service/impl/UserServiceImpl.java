package com.whatsappclone.service.impl;

import com.whatsappclone.dto.UserResponse;
import com.whatsappclone.mapper.UserMapper;
import com.whatsappclone.repository.UserRepository;
import com.whatsappclone.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.whatsappclone.repository.specification.UserSpecification.findAllUsersExceptSelf;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Override
    public List<UserResponse> getAllUsersExceptSelf(Authentication currentUser) {
        return userRepository.findAll(findAllUsersExceptSelf(currentUser.getName()))
                .stream()
                .map(userMapper::toUserResponse)
                .toList();
    }
}
