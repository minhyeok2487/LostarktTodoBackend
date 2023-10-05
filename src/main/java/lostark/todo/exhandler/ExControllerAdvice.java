package lostark.todo.exhandler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;

import javax.servlet.ServletException;
import java.util.ArrayList;
import java.util.List;

@RestControllerAdvice
@Slf4j
public class ExControllerAdvice {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(MethodArgumentNotValidException ex) {
        BindingResult bindingResult = ex.getBindingResult();
        List<String> message = new ArrayList<>();
        for (FieldError fieldError : bindingResult.getFieldErrors()) {
            StringBuilder builder = new StringBuilder();
            builder.append("[");
            builder.append(fieldError.getField());
            builder.append("](은)는 ");
            builder.append(fieldError.getDefaultMessage());
            builder.append(" 입력된 값: [");
            builder.append(fieldError.getRejectedValue());
            builder.append("]");
            message.add(builder.toString());
        }
        ErrorResponse errorResponse = ErrorResponse.of(HttpStatus.BAD_REQUEST.value(), ex.getClass().getSimpleName(), message);
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler({IllegalArgumentException.class, NullPointerException.class, IllegalStateException.class,
            HttpMessageNotReadableException.class, RuntimeException.class, Exception.class})
    public ResponseEntity<ErrorResponse> handlerException(Exception ex) {
        List<String> message = new ArrayList<>();
        message.add(ex.getMessage());
        ErrorResponse errorResponse = ErrorResponse.of(HttpStatus.BAD_REQUEST.value(), ex.getClass().getSimpleName(), message);
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }
}

