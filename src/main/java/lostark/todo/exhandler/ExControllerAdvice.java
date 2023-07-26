package lostark.todo.exhandler;

import jdk.jshell.spi.ExecutionControl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.sql.SQLException;

@Slf4j
@RestControllerAdvice(basePackages = "lostark.todo.controller")
public class ExControllerAdvice {

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)

    @ExceptionHandler
    public ResponseEntity<ErrorResult> ExceptionHandler(RuntimeException e) {
        log.error(e.getMessage());
        ErrorResult errorResult = new ErrorResult(HttpStatus.BAD_REQUEST, e.getMessage());
        return new ResponseEntity<>(errorResult, HttpStatus.BAD_REQUEST);
    }

}
