package lostark.todo.exhandler;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ErrorListResponse {

    private int errorCode;
    private String exceptionName;
    private List<String> errorMessage;

    public static ErrorListResponse of(int errorCode, String exceptionName, List<String> errorMessage) {
        return new ErrorListResponse(errorCode, exceptionName, errorMessage);
    }
}

