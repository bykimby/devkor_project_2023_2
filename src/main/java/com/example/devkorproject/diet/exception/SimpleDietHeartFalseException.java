package com.example.devkorproject.diet.exception;

import com.example.devkorproject.common.constants.ErrorCode;
import com.example.devkorproject.common.exception.GeneralException;

public class SimpleDietHeartFalseException extends GeneralException {
    public  SimpleDietHeartFalseException(){
        super(ErrorCode.SIMPLE_DIET_HEART_FALSE);
    }
}
