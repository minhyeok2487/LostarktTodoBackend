package lostark.todo.domain.character;

import lostark.todo.controller.adminDto.DashboardResponse;
import lostark.todo.domain.member.Member;

import java.util.List;

public interface CharacterCustomRepository  {

    long deleteByMember(Member member);

    List<DashboardResponse> searchCharactersDashBoard(int limit);
}