package com.example.devkorproject.customer.repository;

import com.example.devkorproject.customer.entity.CustomerEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public interface CustomerRepository extends JpaRepository<CustomerEntity,Long> {
    Optional<CustomerEntity> findCustomerEntityByCustomerId(Long customerId);

    Optional<String> findFcmTokenByCustomerId(Long customerId);

    Optional<CustomerEntity> findCustomerEntityByCustomerName(String customerName);
    Optional<CustomerEntity> findCustomerEntityByEmail(String email);

}
