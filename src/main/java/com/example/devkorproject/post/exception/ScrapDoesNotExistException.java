package com.example.devkorproject.post.exception;

import com.example.devkorproject.common.constants.ErrorCode;
import com.example.devkorproject.common.exception.GeneralException;

public class ScrapDoesNotExistException extends GeneralException {
    public ScrapDoesNotExistException(){
        super(ErrorCode.SCRAP_DOES_NOT_EXIST);
    }
}
