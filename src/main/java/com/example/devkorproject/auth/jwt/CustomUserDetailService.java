package com.example.devkorproject.auth.jwt;

import com.example.devkorproject.customer.entity.CustomerEntity;
import com.example.devkorproject.customer.repository.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Collections;

@Service
public class CustomUserDetailService implements UserDetailsService {

    @Autowired
    private CustomerRepository customerRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        CustomerEntity customer = customerRepository.findCustomerEntityByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        return new User(customer.getEmail(), customer.getPassword(), getAuthorities(customer));
    }

    private Collection<? extends GrantedAuthority> getAuthorities(CustomerEntity customer) {
        return Collections.singletonList(new SimpleGrantedAuthority(customer.getAuthority()));
    }
}
