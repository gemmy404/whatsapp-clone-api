package com.whatsappclone.service;

import com.whatsappclone.dto.UserResponse;
import org.springframework.security.core.Authentication;

import java.util.List;

public interface UserService {

    List<UserResponse> getAllUsersExceptSelf(Authentication currentUser);

}
