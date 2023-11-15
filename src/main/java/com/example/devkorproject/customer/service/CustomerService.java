package com.example.devkorproject.customer.service;

import com.example.devkorproject.customer.dto.TempCustomer;
import com.example.devkorproject.customer.entity.CustomerEntity;
import com.example.devkorproject.customer.repository.CustomerRepository;
import org.springframework.stereotype.Service;

@Service
public class CustomerService {
    private final CustomerRepository customerRepository;

    public CustomerService(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    public TempCustomer TempRegister(TempCustomer tempCustomer){
        CustomerEntity customerEntity= CustomerEntity.builder()
                .customerName(tempCustomer.getCustomerName())
                .email(tempCustomer.getEmail())
                .password(tempCustomer.getPassword())
                .imageUrl(tempCustomer.getImageUrl())
                .rank(tempCustomer.getRank())
                .myPosts(tempCustomer.getMyPosts())
                .myLikes(tempCustomer.getMyLikes())
                .myComments(tempCustomer.getMyComments())
                .build();
        customerRepository.save(customerEntity);
        return tempCustomer;
    }

}
