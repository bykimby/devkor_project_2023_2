package com.example.devkorproject.customer.exception;

import com.example.devkorproject.common.constants.ErrorCode;
import com.example.devkorproject.common.exception.GeneralException;

public class CustomerDoesNotExistException extends GeneralException {
    public  CustomerDoesNotExistException(){
        super(ErrorCode.BABY_DOES_NOT_EXIST);
    }
}
