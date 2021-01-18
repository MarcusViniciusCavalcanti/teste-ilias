package com.zonework.atm.rest.error;

import com.zonework.atm.struture.dto.ResponseError;
import com.zonework.atm.struture.dto.ValidationErrors;
import com.zonework.atm.struture.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Locale;

import javax.persistence.EntityNotFoundException;
import javax.servlet.http.HttpServletRequest;

@Slf4j
@ControllerAdvice
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class HandlerErrorResponse {

    private final ReloadableResourceBundleMessageSource reloadableResourceBundleMessageSource;

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ResponseError> exception(Exception exception, HttpServletRequest request) {
        log.error("Error in process request: {} cause by: {} method {}", request.getRequestURL(),
            exception.getClass().getSimpleName(), request.getMethod());

        var responseError = ResponseError.builder()
            .title(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase())
            .timestamp(LocalDateTime.now().toInstant(ZoneOffset.UTC).toEpochMilli())
            .error("Internal server error")
            .statusCode(HttpStatus.INTERNAL_SERVER_ERROR.value())
            .path(request.getServletPath())
            .build();

        return new ResponseEntity<>(responseError, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ResponseError> entityDataIntegrrity(DataIntegrityViolationException exception, HttpServletRequest request) {
        log.error("Error in process request: {} cause by: {} method {}", request.getRequestURL(),
            exception.getClass().getSimpleName(), request.getMethod());

        var responseError = ResponseError.builder()
            .title(HttpStatus.CONFLICT.getReasonPhrase())
            .timestamp(LocalDateTime.now().toInstant(ZoneOffset.UTC).toEpochMilli())
            .error("Entity exist in database with cpf or e-mail informed")
            .statusCode(HttpStatus.CONFLICT.value())
            .path(request.getServletPath())
            .build();

        return new ResponseEntity<>(responseError, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ResponseError> bussinesException(BusinessException exception, HttpServletRequest request) {
        log.error("Error in process request: {} cause by: {} method {}", request.getRequestURL(),
            exception.getClass().getSimpleName(), request.getMethod());

        var reason = reloadableResourceBundleMessageSource.getMessage(exception.getCode(), exception.getArgs(), Locale.getDefault());

        var responseError = ResponseError.builder()
            .title(HttpStatus.BAD_REQUEST.getReasonPhrase())
            .timestamp(LocalDateTime.now().toInstant(ZoneOffset.UTC).toEpochMilli())
            .error(reason)
            .statusCode(HttpStatus.BAD_REQUEST.value())
            .path(request.getServletPath())
            .build();

        return ResponseEntity.badRequest().body(responseError);
    }

    @ExceptionHandler(IlegalRequestBodyException.class)
    public ResponseEntity<ResponseError> handleIlegalRequestBodyException(IlegalRequestBodyException exception, HttpServletRequest request) {
        log.error("Error in process request: {} cause by: {} method {}", request.getRequestURL(),
            exception.getClass().getSimpleName(), request.getMethod());
        var errors = ResponseError.builder()
            .title(String.format("Validation Errors %s", exception.getTitle()))
            .timestamp(LocalDateTime.now().toInstant(ZoneOffset.UTC).toEpochMilli())
            .error(new ValidationErrors(exception.errors()))
            .statusCode(HttpStatus.UNPROCESSABLE_ENTITY.value())
            .path(request.getServletPath())
            .build();

        return ResponseEntity.unprocessableEntity().body(errors);
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ResponseError> handlerEntityNotFoundException(EntityNotFoundException exception, HttpServletRequest request) {
        return buildResponseEntityNotFound(request, exception.getClass().getSimpleName(), "Entity Not Found", exception.getMessage());
    }

    @ExceptionHandler(EmptyResultDataAccessException.class)
    public ResponseEntity<ResponseError> handlerEmptyResultDataAccessException(EmptyResultDataAccessException exception, HttpServletRequest request) {
        return buildResponseEntityNotFound(request, exception.getClass().getSimpleName(), "Entity Not Found", exception.getMessage());
    }

    private static ResponseEntity<ResponseError> buildResponseEntityNotFound(HttpServletRequest request,
                                                                 String simpleName,
                                                                 String title,
                                                                 String message) {
        log.error("Error in process request: {} cause by: {} method {}", request.getRequestURL(),
            simpleName, request.getMethod());
        var errors = ResponseError.builder()
            .title(title)
            .timestamp(LocalDateTime.now().toInstant(ZoneOffset.UTC).toEpochMilli())
            .error(message)
            .statusCode(HttpStatus.NOT_FOUND.value())
            .path(request.getServletPath())
            .build();

        return new ResponseEntity<>(errors, HttpStatus.NOT_FOUND);
    }
}
