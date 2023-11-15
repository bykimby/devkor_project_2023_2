package com.example.devkorproject.post.exception;

import com.example.devkorproject.common.constants.ErrorCode;
import com.example.devkorproject.common.exception.GeneralException;

public class PostDoesNotExistException extends GeneralException {
    public PostDoesNotExistException(){
        super(ErrorCode.POST_DOES_NOT_EXIST);
    }
}
