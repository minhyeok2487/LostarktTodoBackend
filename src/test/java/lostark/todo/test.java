package lostark.todo;

import lostark.todo.controller.dto.characterDto.CharacterResponseDto;
import lostark.todo.domain.character.Character;
import lostark.todo.domain.character.CharacterRepository;
import lostark.todo.domain.member.Member;
import lostark.todo.service.CharacterService;
import lostark.todo.service.MemberService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.transaction.Transactional;
import java.util.*;
import java.util.stream.Collectors;

@SpringBootTest
@Transactional
public class test {

    @Autowired
    MemberService memberService;

    @Autowired
    CharacterService characterService;

    @Autowired
    CharacterRepository characterRepository;
    

    @Test
    void test2() {
        Member member = memberService.findMember("neungdya@gmail.com");
        if(member.getCharacters().isEmpty()) {
            throw new IllegalArgumentException("등록된 캐릭터가 없습니다.");
        }
        List<Character> characters = member.getCharacters();
        characters.sort(Comparator.comparingInt(Character::getSortNumber));
        Map<String, List<Character>> serverNameToCharacters = new HashMap<>();

        for (Character character : characters) {
            String serverName = character.getServerName();

            // 서버 이름을 키로 사용하여 캐릭터 리스트를 가져옴
            List<Character> serverCharacters = serverNameToCharacters.get(serverName);

            // 만약 해당 서버 이름에 대한 리스트가 없으면 생성
            if (serverCharacters == null) {
                serverCharacters = new ArrayList<>();
                serverNameToCharacters.put(serverName, serverCharacters);
            }

            // 캐릭터를 해당 서버 이름의 리스트에 추가
            serverCharacters.add(character);
        }
        System.out.println("serverNameToCharacters = " + serverNameToCharacters);
    }
}
