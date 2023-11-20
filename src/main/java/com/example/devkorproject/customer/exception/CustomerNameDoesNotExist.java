package com.example.devkorproject.customer.exception;

import com.example.devkorproject.common.constants.ErrorCode;
import com.example.devkorproject.common.exception.GeneralException;

public class CustomerNameDoesNotExist extends GeneralException {
    public CustomerNameDoesNotExist(){
        super(ErrorCode.CUSTOMER_NAME_DOES_NOT_EXIST);
    }
}
