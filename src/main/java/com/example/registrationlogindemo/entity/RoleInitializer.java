package com.example.registrationlogindemo.entity;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.example.registrationlogindemo.repository.RoleRepository;

@Component
public class RoleInitializer implements CommandLineRunner {

    private final RoleRepository roleRepository;

    public RoleInitializer(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        if (roleRepository.findByName("ROLE_ADMIN") == null) {
            Role adminRole = new Role();
            adminRole.setName("ROLE_ADMIN");
            roleRepository.save(adminRole);
            System.out.println("Initialized ROLE_ADMIN.");
        }
    }
}
