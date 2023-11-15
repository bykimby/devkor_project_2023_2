package com.example.devkorproject.post.exception;

import com.example.devkorproject.common.constants.ErrorCode;
import com.example.devkorproject.common.exception.GeneralException;

public class CustomerDoesNotMatchException extends GeneralException {
    public CustomerDoesNotMatchException(){
        super(ErrorCode.CUSTOMER_DOES_NOT_MATCH);
    }
}
