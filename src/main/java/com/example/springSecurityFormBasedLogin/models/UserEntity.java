package com.example.springSecurityFormBasedLogin.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String email;
    private String password;
    private String authorities;
    private String providers;


    public List<String> extractAuthorities(){
        return authorities != null ?  Arrays.stream(authorities.split(",")).toList() : Collections.emptyList();
    }
}
