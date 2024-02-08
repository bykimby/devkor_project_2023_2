package com.example.devkorproject.auth.jwt;

import com.example.devkorproject.customer.entity.CustomerEntity;
import com.example.devkorproject.customer.repository.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Component
public class CustomAuthenticationProvider implements AuthenticationProvider {
    private final CustomerRepository customerRepository;

    @Autowired
    public CustomAuthenticationProvider(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }
    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String username = authentication.getName();
        String role = "ROLE_USER"; // CustomerEntity에서 가져온 역할

        Optional<CustomerEntity> customer = customerRepository.findCustomerEntityByCustomerName(username);
        if (customer.isPresent()) {
            // 사용자가 존재하면 ROLE_USER 권한을 부여
            List<GrantedAuthority> authorities = Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"));
            return new UsernamePasswordAuthenticationToken(username, null, authorities);
        } else {
            throw new BadCredentialsException("Customer does not exist");
        }
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication);
    }

}
