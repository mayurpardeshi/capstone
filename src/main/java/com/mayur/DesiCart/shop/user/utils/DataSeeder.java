package com.mayur.DesiCart.shop.user.utils;

import net.datafaker.Faker;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.mayur.DesiCart.shop.user.models.Role;
import com.mayur.DesiCart.shop.user.models.User;
import com.mayur.DesiCart.shop.user.repositories.RoleRepository;
import com.mayur.DesiCart.shop.user.repositories.UserRepository;

import java.util.Set;

@Component
@Profile("dev")
public class DataSeeder implements CommandLineRunner {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final Faker faker;

    public DataSeeder(UserRepository userRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder, Faker faker){
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.faker = faker;
        this.passwordEncoder = passwordEncoder;
    }

    /*
    * We will use @Transactional to ensure that if a user creation fails, the entire batch rolls back
    * */
    @Override
    @Transactional
    public void run(String... args) throws Exception {
        // Step 1: Seed role first, create or fetch
        Role adminRole = getOrCreateRole("ROLE_ADMIN");
        Role userRole = getOrCreateRole("ROLE_USER");

        // Step 2: Create a deterministic admin
        if (!userRepository.existsByUserId("admin@desicart.com")) {
            User adminUser = User.builder()
                    .firstName("System")
                    .lastName("Admin")
                    .userId("admin@desicart.com")
                    .password(passwordEncoder.encode("admin@123"))
                    .roles(Set.of(adminRole, userRole))
                    .build();
            userRepository.save(adminUser);
            System.out.println(">>>> Created default Admin admin@abc.com");
        }

        // Step 2.a: Create a deterministic user
        if (!userRepository.existsByUserId("user@desicart.com")) {
            User user = User.builder()
                    .firstName("User")
                    .lastName("Simply")
                    .userId("user@desicart.com")
                    .password(passwordEncoder.encode("user@123"))
                    .roles(Set.of(userRole))
                    .build();
            userRepository.save(user);
            System.out.println(">>>> Created default user user@desicart.com");
        }

        // 3. Seed Fake Users using Datafaker, create 15 users who has role User
        long currentUserCount = userRepository.count();
        if (currentUserCount < 20) {
            System.out.println(">>> Seeding fake users...");
            for (int i = 0; i < 15; i++) {
                User fakeUser = User.builder()
                        .firstName(faker.name().firstName())
                        .lastName(faker.name().lastName())
                        .userId(faker.internet().emailAddress())
                        .password(passwordEncoder.encode("password"))
                        .roles(Set.of(userRole))
                        .build();
                userRepository.save(fakeUser);
            }
            System.out.println(">>> Successfully seeded 15 fake users.");
        }

    }

    private Role getOrCreateRole(String roleName){
        return roleRepository.findByName(roleName)
                .orElseGet(() -> {
                    Role role = new Role();
                    role.setName(roleName);
                    return roleRepository.save(role);
                });
    }
}
