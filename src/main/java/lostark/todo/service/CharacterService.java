package lostark.todo.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lostark.todo.controller.dtos.CharacterSaveDto;
import lostark.todo.domain.character.Character;
import lostark.todo.domain.character.CharacterRepository;
import lostark.todo.domain.member.Member;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class CharacterService {

    private final MemberService memberService;
    private final CharacterRepository characterRepository;

    public List<Character> characterListByUsernameAndSelect(String username) throws Exception{
        Member member = memberService.findUser(username);
        List<Character> characterList = characterRepository.findByMember_IdAndSelectedOrderByItemLevelDesc(member.getId(), true);
        return characterList;
    }

    public Character changeSelected(Character character) {
        return characterRepository.findById(character.getId()).orElseThrow().changeSelected();
    }

    public Character findCharacterByName(String characterName) {
        return characterRepository.findByCharacterName(characterName);
    }

    public Character saveCharacter(CharacterSaveDto characterSaveDto) {
        Character character = characterRepository.findById(characterSaveDto.getId()).orElseThrow();
        character.update(characterSaveDto);
        return character;
    }
}
