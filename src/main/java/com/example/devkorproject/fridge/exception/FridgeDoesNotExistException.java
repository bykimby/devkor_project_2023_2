package com.example.devkorproject.fridge.exception;

import com.example.devkorproject.common.constants.ErrorCode;
import com.example.devkorproject.common.exception.GeneralException;

public class FridgeDoesNotExistException extends GeneralException {
    public FridgeDoesNotExistException() {super(ErrorCode.FRIDGE_DOES_NOT_EXIST);}
}
