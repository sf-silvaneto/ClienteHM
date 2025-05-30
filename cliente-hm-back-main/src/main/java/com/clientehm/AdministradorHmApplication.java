package com.clientehm;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean; // Adicionado
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder; // Adicionado
import org.springframework.security.crypto.password.PasswordEncoder; // Adicionado

@SpringBootApplication
public class AdministradorHmApplication {
    public static void main(String[] args) {
        SpringApplication.run(AdministradorHmApplication.class, args);
    }
}