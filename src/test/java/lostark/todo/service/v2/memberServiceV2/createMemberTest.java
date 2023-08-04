package lostark.todo.service.v2.memberServiceV2;

import lostark.todo.controller.dto.characterDto.CharacterUpdateDto;
import lostark.todo.controller.dto.memberDto.MemberSignupDto;
import lostark.todo.domain.Role;
import lostark.todo.domain.character.Character;
import lostark.todo.domain.member.Member;
import lostark.todo.service.v2.LostarkCharacterServiceV2;
import lostark.todo.service.v2.MemberServiceV2;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class createMemberTest {

    @Autowired
    LostarkCharacterServiceV2 lostarkCharacterService;

    @Autowired
    MemberServiceV2 memberService;

    @Autowired
    Validator validator;

    @Value("${Lostark-API-Test-Key}")
    private String apiKey;

    String username;

    String password;

    String characterName;

    List<Character> characterList;

    MemberSignupDto memberSignupDto;

    @BeforeEach
    void init() {
        username = "qwe2487";
        password = "1234";
        characterName = "바람불어야지요";

        memberSignupDto = MemberSignupDto.builder()
                .characterName(characterName)
                .apiKey(apiKey)
                .username(username)
                .password(password)
                .build();

        characterList = lostarkCharacterService.getCharacterList(memberSignupDto);
    }

    @Test
    @DisplayName("createMember 테스트 성공")
    void createMember() {
        // when
        Member signupMember = memberService.createMember(memberSignupDto, characterList);

        // then
        assertThat(signupMember.getUsername()).isEqualTo(username);
        assertThat(signupMember.getPassword()).isNotEqualTo(password); // 패스워드 암호화
        assertThat(signupMember.getCharacters().size()).isEqualTo(characterList.size()); // 캐릭터 리스트 저장
        assertThat(signupMember.getRole()).isEqualTo(Role.USER);
    }

    @Test
    @DisplayName("createMember 테스트 실패: 이미 존재하는 아이디")
    void createMemberDuplicateUsername() {
        // when
        memberSignupDto.setUsername("test");

        // then
        assertThrows(IllegalArgumentException.class, () -> {
            memberService.createMember(memberSignupDto, characterList);
        });
    }

    @Test
    @DisplayName("createMember 테스트 실패: 캐릭터 리스트 없음")
    void createMemberCharacterListIsNull() {
        // when
        List<Character> characterList = new ArrayList<>();

        // then
        assertThrows(IllegalArgumentException.class, () -> {
            memberService.createMember(memberSignupDto, characterList);
        });
    }

    @Test
    @DisplayName("createMember 테스트 실패: @Valid NotEmpty")
    void createMemberNotEmpty() {
        // when
        memberSignupDto.setUsername("");
        memberSignupDto.setCharacterName(null);
        memberSignupDto.setApiKey("");
        Set<ConstraintViolation<MemberSignupDto>> validation = validator.validate(memberSignupDto);

        // then
        Iterator<ConstraintViolation<MemberSignupDto>> iterator = validation.iterator();
        List<String> messages = new ArrayList<>();
        while (iterator.hasNext()) {
            ConstraintViolation<MemberSignupDto> next = iterator.next();
            String message = next.getPropertyPath() + " (은)는 " + next.getMessage();
            messages.add(message);
            System.out.println("message = " + message);
        }

        Assertions.assertThat(messages).contains(
                "apiKey (은)는 비어 있을 수 없습니다",
                "characterName (은)는 비어 있을 수 없습니다",
                "username (은)는 비어 있을 수 없습니다");
    }
}