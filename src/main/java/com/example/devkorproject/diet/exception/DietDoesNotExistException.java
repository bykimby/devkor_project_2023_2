package com.example.devkorproject.diet.exception;

import com.example.devkorproject.common.constants.ErrorCode;
import com.example.devkorproject.common.exception.GeneralException;

public class DietDoesNotExistException extends GeneralException {
    public  DietDoesNotExistException(){
        super(ErrorCode.DIET_DOES_NOT_EXIST);
    }
}
