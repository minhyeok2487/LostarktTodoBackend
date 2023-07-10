package lostark.todo.controller.dto.memberDto;

import lombok.Data;
import lostark.todo.domain.character.Character;
import lostark.todo.domain.member.Member;

import java.util.List;

@Data
public class MemberResponseDto {

    private String username;

    private List<Character> characterList;
    public MemberResponseDto(Member member) {
        username = member.getUsername();
        characterList = member.getCharacters();
    }
}
