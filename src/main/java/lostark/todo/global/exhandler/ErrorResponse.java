package lostark.todo.global.exhandler;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ErrorResponse {

    private int errorCode;
    private String exceptionName;
    private String errorMessage;

    public static ErrorResponse of(int errorCode, String exceptionName, String errorMessage) {
        return new ErrorResponse(errorCode, exceptionName, errorMessage);
    }
}

