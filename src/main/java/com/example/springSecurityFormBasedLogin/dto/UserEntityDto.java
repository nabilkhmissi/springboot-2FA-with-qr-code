package com.example.springSecurityFormBasedLogin.dto;

import com.example.springSecurityFormBasedLogin.models.UserEntity;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class UserEntityDto {
    private Long id;
    private String name;
    private String email;
    private String providers;
    private String authorities;

    public static UserEntityDto fromEntity(UserEntity userEntity){
        return UserEntityDto.builder()
                .id(userEntity.getId())
                .name(userEntity.getName())
                .email(userEntity.getEmail())
                .providers(userEntity.getProviders())
                .authorities(userEntity.getAuthorities())
                .build();
    }
}
