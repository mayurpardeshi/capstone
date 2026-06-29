package com.mayur.DesiCart.shop.auth.conf;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.mayur.DesiCart.shop.user.models.User;
import com.mayur.DesiCart.shop.user.repositories.UserRepository;

import java.util.List;
import java.util.stream.Collectors;


/*
* To make Spring security understand our User entity, we are trying to map roles to GrantedAuthority
* */
@Service
public class CustomUserDetails implements UserDetailsService {
    @Autowired
    private UserRepository userRepository;


    /*
    * fetch the user,
    * verify they exist,
    * then map their database roles into Spring's GrantedAuthority objects.
    * */
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        // fetch the user from db
        User user = userRepository.findByUserId(email).orElseThrow(() -> new UsernameNotFoundException("User not found with email : "+email));
        // map roles to granted authority
        List<SimpleGrantedAuthority> authorities = user.getRoles().stream()
                .map(role -> new SimpleGrantedAuthority(role.getName()))
                .collect(Collectors.toList());
        // return Spring security User object
        return new org.springframework.security.core.userdetails.User(user.getUserId(), user.getPassword(), authorities);

    }
}
