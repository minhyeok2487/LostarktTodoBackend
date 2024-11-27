package lostark.todo.global.exhandler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lostark.todo.global.exhandler.exceptions.CustomIllegalArgumentException;
import lostark.todo.global.service.webHook.WebHookService;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;

import java.util.ArrayList;
import java.util.List;

@RestControllerAdvice
@RequiredArgsConstructor
@Slf4j
public class ExControllerAdvice {

    private final WebHookService webHookService;

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorListResponse> handleValidationException(MethodArgumentNotValidException ex) {
        BindingResult bindingResult = ex.getBindingResult();
        List<String> message = new ArrayList<>();
        for (FieldError fieldError : bindingResult.getFieldErrors()) {
            StringBuilder builder = new StringBuilder();
            builder.append(fieldError.getDefaultMessage());
            builder.append(" 입력된 값: [");
            builder.append(fieldError.getRejectedValue());
            builder.append("]");
            message.add(builder.toString());
        }
        ErrorListResponse errorListResponse = ErrorListResponse.of(HttpStatus.BAD_REQUEST.value(), ex.getClass().getSimpleName(), message);
        return new ResponseEntity<>(errorListResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler({IllegalArgumentException.class, NullPointerException.class, IllegalStateException.class,
            HttpMessageNotReadableException.class, RuntimeException.class, Exception.class})
    public ResponseEntity<ErrorResponse> handlerException(Exception ex) {
        webHookService.callEvent(ex);
        log.warn(ex.getMessage());
        ErrorResponse errorResponse = ErrorResponse.of(HttpStatus.BAD_REQUEST.value(), ex.getClass().getSimpleName(), ex.getMessage());
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler({CustomIllegalArgumentException.class})
    public ResponseEntity<ErrorResponse> handlerExceptionByMember(CustomIllegalArgumentException ex) {
        webHookService.callEvent(ex);
        String message = "";
        if(ex.getMember() != null) {
            message += ex.getMember().getId() + " / " + ex.getMember().getUsername() + " / ";
        }
        message += ex.getMessage();
        log.warn(message);
        ErrorResponse errorResponse = ErrorResponse.of(HttpStatus.BAD_REQUEST.value(), ex.getClass().getSimpleName(), message);
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }
}

