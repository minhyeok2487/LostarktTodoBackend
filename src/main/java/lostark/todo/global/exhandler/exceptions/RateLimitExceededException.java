package lostark.todo.global.exhandler.exceptions;

import lombok.Getter;

@Getter
public class RateLimitExceededException extends RuntimeException {

    public RateLimitExceededException(String message) {
        super(message);
    }
}