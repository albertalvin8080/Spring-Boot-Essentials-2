package com.albert.springbootessentials2.handler;

import com.albert.springbootessentials2.exception.*;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.*;
import org.springframework.lang.Nullable;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

// uncomment this to activate the handler
//@ControllerAdvice
public class RestExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<NotFoundExceptionDetails> handleNotFoundException
            (NotFoundException notFoundException) {
        return new ResponseEntity<>(
                NotFoundExceptionDetails.builder()
                        .timeStamp(LocalDateTime.now())
                        .status(HttpStatus.NOT_FOUND.value())
                        .title("Not Found Exception")
                        .error(HttpStatus.NOT_FOUND.getReasonPhrase())
                        .details(notFoundException.getMessage())
                        .developerMessage("Check the documentation.")
                        .build()
                , HttpStatus.NOT_FOUND);
    }

    //    @ExceptionHandler(MethodArgumentNotValidException.class)
    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid
    (MethodArgumentNotValidException exception, @NotNull HttpHeaders headers, HttpStatusCode statusCode, @NotNull WebRequest request) {
        final List<FieldError> fieldErrors = exception.getBindingResult().getFieldErrors();
        final String fields =
                fieldErrors.stream().map(FieldError::getField).collect(Collectors.joining(", "));
        final String fieldsErrors =
                fieldErrors.stream().map(FieldError::getDefaultMessage).collect(Collectors.joining(", "));

        var details = MethodArgumentNotValidExceptionDetails
                .builder()
                .timeStamp(LocalDateTime.now())
                .status(statusCode.value())
                .title("Method Argument Not Valid Exception")
                .details(exception.getClass().getName())
                .developerMessage("Check the documentation")
                .error(statusCode instanceof HttpStatus status ? status.getReasonPhrase() : "Not a HttpStatus")
                .fields(fields)
                .fieldsErrors(fieldsErrors)
                .build();

        return new ResponseEntity<>(details, headers, statusCode);
    }

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<Object> handleResponseStatusException(ResponseStatusException exception) {
        final HttpStatusCode statusCode = exception.getStatusCode();
        final var details = ResponseStatusExceptionDetails
                .builder()
                .timeStamp(LocalDateTime.now())
                .status(statusCode.value())
                .title("Response Status Exception")
                .details(exception.getReason())
                .developerMessage("Check the documentation")
                .error(statusCode instanceof HttpStatus status ? status.getReasonPhrase() : "Not a HttpStatus")
                .build();
        return new ResponseEntity<>(details, statusCode);
    }

    @Override
    protected ResponseEntity<Object> handleExceptionInternal(Exception exception, @Nullable Object body, @NotNull HttpHeaders headers, HttpStatusCode statusCode, @NotNull WebRequest request) {
        final var details = ExceptionDetails
                .builder()
                .timeStamp(LocalDateTime.now())
                .status(statusCode.value())
                .title("Internal Server Error")
                .details(exception.getMessage())
                .developerMessage("Check the documentation")
                .error(statusCode instanceof HttpStatus status ? status.getReasonPhrase() : "Not a HttpStatus")
                .build();
        return new ResponseEntity<>(details, headers, statusCode);
    }
}
