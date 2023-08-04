package lostark.todo.exhandler;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.validation.BindingResult;
import java.util.stream.Collectors;

@Data
@AllArgsConstructor
public class ErrorResponse {

    private int errorCode;
    private String exceptionName;
    private String errorMessage;

    public static ErrorResponse of(int errorCode, String exceptionName, String errorMessage) {
        return new ErrorResponse(errorCode, exceptionName, errorMessage);
    }
}

