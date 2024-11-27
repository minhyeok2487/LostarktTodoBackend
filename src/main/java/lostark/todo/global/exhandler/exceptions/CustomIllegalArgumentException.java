package lostark.todo.global.exhandler.exceptions;

import lombok.Getter;
import lostark.todo.domain.member.entity.Member;

@Getter
public class CustomIllegalArgumentException extends IllegalArgumentException {

    private String method;

    private Member member;

    public CustomIllegalArgumentException(String method, String message, Member member ) {
        super(message);
        this.method = method;
        this.member = member;
    }

}
