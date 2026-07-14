package com.Sistema.PrestamosBancarios.mapper;

import com.Sistema.PrestamosBancarios.dto.request.RegisterRequest;
import com.Sistema.PrestamosBancarios.dto.response.UserResponse;
import com.Sistema.PrestamosBancarios.model.Role;
import com.Sistema.PrestamosBancarios.model.User;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    public User toEntity(RegisterRequest request, String encodedPassword) {
        return User.builder()
                .email(request.getEmail())
                .username(request.getUsername())
                .password(encodedPassword)
                .role(Role.USER)
                .build();
    }

    public UserResponse toResponse(User user) {
        return new UserResponse(
                user.getId(),
                user.getEmail(),
                user.getUsername(),
                user.getRole().name()
        );
    }
}
