package com.albert.springbootessentials2.exception;

import lombok.Getter;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
// remember, it represents the details of a class which already exists in the Spring API
public class MethodArgumentNotValidExceptionDetails extends ExceptionDetails {
    private String fields;
    private String fieldsErrors;
}
