package com.example.devkorproject.common.constants;

import com.example.devkorproject.common.exception.GeneralException;
import com.example.devkorproject.customer.exception.CustomerNameDoesNotExist;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

import java.util.Arrays;
import java.util.Optional;
import java.util.function.Predicate;

import static org.springframework.http.HttpMethod.values;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {
    OK(0, HttpStatus.OK, "OK"),
    BAD_REQUEST(10000, HttpStatus.BAD_REQUEST, "Bad request"),
    INTERNAL_ERROR(20000, HttpStatus.INTERNAL_SERVER_ERROR, "Internal error"),
    NOT_FOUND(10001, HttpStatus.NOT_FOUND, "Requested resource is not found"),
    BABY_DOES_NOT_EXIST(10001, HttpStatus.BAD_REQUEST, "Requested baby is not found"),
    CUSTOMER_DOES_NOT_EXIST(10001, HttpStatus.BAD_REQUEST, "Requested customer is not found"),
    SIMPLE_DIET_DOES_NOT_EXIST(10001, HttpStatus.BAD_REQUEST, "Requested diet is not found"),
    DIET_DOES_NOT_EXIST(10001, HttpStatus.BAD_REQUEST, "Requested diet is not found"),
    POST_DOES_NOT_EXIST(10001,HttpStatus.BAD_REQUEST,"Requested post is not found"),
    CUSTOMER_DOES_NOT_MATCH(10001,HttpStatus.BAD_REQUEST,"Requested customer does not match"),
    FRIDGE_DOES_NOT_EXIST(10001,HttpStatus.BAD_REQUEST,"Requested fridge does not found"),
    CUSTOMER_NAME_DOES_NOT_EXIST(10001,HttpStatus.BAD_REQUEST,"Requested customer name does not found"),
    SCRAP_DOES_NOT_EXIST(10001,HttpStatus.BAD_REQUEST,"Requested scrap does not found"),
    FCMTOKEN_DOES_NOT_EXIST(10001,HttpStatus.BAD_REQUEST, "Requested fcmToken does not found"),
    CUSTOMER_EXIST(10001,HttpStatus.BAD_REQUEST,"Requested customer exist"),
    BLANK_PASSWORD(10001,HttpStatus.BAD_REQUEST,"Password cannot be empty"),
    WRONG_PASSWORD(10001,HttpStatus.BAD_REQUEST,"Password is wrong"),
    WRONG_TOKEN(10001,HttpStatus.BAD_REQUEST,"Token is wrong"),
    COMMENT_DOES_NOT_EXIST(10001,HttpStatus.BAD_REQUEST,"Comment does not exist")
    ;
    private final Integer code;
    private final HttpStatus httpStatus;
    private final String message;
    public static ErrorCode valueOf(HttpStatus httpStatus) {
        if (httpStatus == null) {
            throw new GeneralException("HttpStatus is null.");
        }

        return Arrays.stream(values())
                .filter(errorCode -> errorCode.getHttpStatus() == httpStatus)
                .findFirst()
                .orElseGet(() -> {
                    if (httpStatus.is4xxClientError()) {
                        return ErrorCode.BAD_REQUEST;
                    } else if (httpStatus.is5xxServerError()) {
                        return ErrorCode.INTERNAL_ERROR;
                    } else {
                        return ErrorCode.OK;
                    }
                });
    }

    public String getMessage(Throwable e) {
        return this.getMessage(this.getMessage());
    }

    public String getMessage(String message) {
        return Optional.ofNullable(message)
                .filter(Predicate.not(String::isBlank))
                .orElse(this.getMessage());
    }

    @Override
    public String toString() {
        return String.format("%s (%d)", this.name(), this.getCode());
    }
}
