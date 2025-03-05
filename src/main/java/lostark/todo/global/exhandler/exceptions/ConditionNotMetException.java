package lostark.todo.global.exhandler.exceptions;

import lombok.Getter;

@Getter
public class ConditionNotMetException extends IllegalArgumentException {

    public ConditionNotMetException(String s) {
        super(s);
    }
}
