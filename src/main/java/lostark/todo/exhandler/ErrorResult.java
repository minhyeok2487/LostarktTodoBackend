package lostark.todo.exhandler;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class ErrorResult {

    private int code;
    private String error;
    private String  message;

    public ErrorResult(HttpStatus httpStatus, String message) {
        this.code = httpStatus.value();
        this.error = httpStatus.name();
        this.message = message;
    }
}