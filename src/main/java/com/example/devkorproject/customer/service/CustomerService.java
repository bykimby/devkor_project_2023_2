package com.example.devkorproject.customer.service;

import com.example.devkorproject.common.constants.ErrorCode;
import com.example.devkorproject.common.exception.GeneralException;
import com.example.devkorproject.customer.dto.TempCustomer;
import com.example.devkorproject.customer.entity.CustomerEntity;
import com.example.devkorproject.customer.repository.CustomerRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

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

    public CustomerEntity searchByMemberId(Long customerId) {
        Optional<CustomerEntity> opCustomer = customerRepository.findCustomerEntityByCustomerId(customerId);
        if(opCustomer.isEmpty())
            throw new GeneralException(ErrorCode.CUSTOMER_DOES_NOT_EXIST.getMessage());
        CustomerEntity customer = opCustomer.get();
        return customer;
    }

    public void saveFCMToken(Long customerId, String fcmToken){
        Optional<CustomerEntity> opCustomer = customerRepository.findCustomerEntityByCustomerId(customerId);
        if(opCustomer.isEmpty())
            throw new GeneralException(ErrorCode.CUSTOMER_DOES_NOT_EXIST.getMessage());
        CustomerEntity customer = opCustomer.get();
        customer.setFcmToken(fcmToken);
    }

    public String searchFCMTokenByCustomerId(Long customerId){
        Optional<CustomerEntity> opCustomer = customerRepository.findCustomerEntityByCustomerId(customerId);
        if(opCustomer.isEmpty())
            throw new GeneralException(ErrorCode.CUSTOMER_DOES_NOT_EXIST.getMessage());

        Optional<String> opFcmToken = customerRepository.findFcmTokenByCustomerId(customerId);
        if(opFcmToken.isEmpty())
            throw new GeneralException(ErrorCode.FCMTOKEN_DOES_NOT_EXIST.getMessage());

        return opFcmToken.get();
    }
}
