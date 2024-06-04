package lostark.todo.domain.character;

import lostark.todo.domain.member.Member;

public interface CharacterCustomRepository  {

    long deleteByMember(Member member);
}