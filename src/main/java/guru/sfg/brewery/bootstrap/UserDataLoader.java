/*
 * Copyright 2020 Russian Post
 *
 * This source code is Russian Post Confidential Proprietary.
 * This software is protected by copyright. All rights and titles are reserved.
 * You shall not use, copy, distribute, modify, decompile, disassemble or reverse engineer the software.
 * Otherwise this violation would be treated by law and would be subject to legal prosecution.
 * Legal use of the software provides receipt of a license from the right holder only.
 */

package guru.sfg.brewery.bootstrap;

import guru.sfg.brewery.domain.security.Authority;
import guru.sfg.brewery.domain.security.User;
import guru.sfg.brewery.repositories.security.AuthorityRepository;
import guru.sfg.brewery.repositories.security.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class UserDataLoader implements CommandLineRunner {

    private final UserRepository userRepository;

    private final AuthorityRepository authorityRepository;

    private final PasswordEncoder passwordEncoder;


    @Transactional
    @Override
    public void run(String... args) throws Exception {
        loadAuthoritiesData();
        loadUserData();
    }

    private void loadAuthoritiesData() {
        var saved = authorityRepository.findByRoleIn(List.of("ADMIN", "USER", "CUSTOMER"));

        var authoritiesList = Arrays.asList(
                Authority.builder().role("ADMIN").build(),
                Authority.builder().role("USER").build(),
                Authority.builder().role("CUSTOMER").build()
        );

        authoritiesList.removeAll(saved);

        if(!authoritiesList.isEmpty()) {
            authorityRepository.saveAll(authoritiesList);
        }
    }

    private void loadUserData() {
        log.debug("Users in DB at start : " + userRepository.count());

        List<User> userList = new ArrayList<>();

        var springUser = userRepository.findByUsername("spring");

        if (springUser.isEmpty()) {
            userList.add(
                    User.builder()
                            .username("spring")
                            .password(passwordEncoder.encode("guru"))
                            .authority(
                                    authorityRepository.findByRole("ADMIN").get()
                            )
                            .build()
            );
        }

        var userUser = userRepository.findByUsername("user");

        if (userUser.isEmpty()) {
            userList.add(
                    User.builder()
                            .username("user")
                            .password(passwordEncoder.encode("password"))
                            .authority(
                                    authorityRepository.findByRole("USER").get()
                            )
                            .build()
            );
        }

        var scottUser = userRepository.findByUsername("scott");

        if (scottUser.isEmpty()) {
            userList.add(
                    User.builder()
                            .username("scott")
                            .password(passwordEncoder.encode("tiger"))
                            .authority(
                                    authorityRepository.findByRole("CUSTOMER").get()
                            )
                            .build()
            );
        }

        userRepository.saveAll(userList);

        log.debug("Saved users : " + userRepository.count());
    }
}
