package com.example.devkorproject.diet.exception;

import com.example.devkorproject.common.constants.ErrorCode;
import com.example.devkorproject.common.exception.GeneralException;

public class SimpleDietDoesNotExistException extends GeneralException {
    public SimpleDietDoesNotExistException(){
        super(ErrorCode.SIMPLE_DIET_DOES_NOT_EXIST);
    }
}
