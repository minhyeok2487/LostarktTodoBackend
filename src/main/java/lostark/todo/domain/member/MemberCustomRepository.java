package lostark.todo.domain.member;

import lostark.todo.controller.dtoV2.member.MemberResponse;

public interface MemberCustomRepository {
    MemberResponse findMemberResponse(String username);
}
