package com.albert.springbootessentials2.exception;

import lombok.Data;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

@Data
@SuperBuilder
public class ExceptionDetails {
    protected LocalDateTime timeStamp;
    protected String title;
    protected int status;
    protected String error;
    protected String developerMessage;
    protected String details;
}
