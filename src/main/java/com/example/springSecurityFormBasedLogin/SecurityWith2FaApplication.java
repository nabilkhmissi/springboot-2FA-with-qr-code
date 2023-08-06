package com.example.springSecurityFormBasedLogin;

import com.example.springSecurityFormBasedLogin.Repository.UserRepository;
import com.example.springSecurityFormBasedLogin.models.UserEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootApplication
@RequiredArgsConstructor
public class SecurityWith2FaApplication {

	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;

	public static void main(String[] args) {
		SpringApplication.run(SecurityWith2FaApplication.class, args);
	}

	@Bean
	public CommandLineRunner commandLineRunner(){
		return args -> {
			userRepository.save(new UserEntity(null, "user",
					"user@mail.com",
					passwordEncoder.encode("user"),
					"ROLE_USER",
					"local",
					false,
					null));
		};
	}

}
