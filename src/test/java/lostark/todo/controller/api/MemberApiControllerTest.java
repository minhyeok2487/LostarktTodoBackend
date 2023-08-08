package lostark.todo.controller.api;

import lostark.todo.config.TokenProvider;
import lostark.todo.controller.dto.characterDto.CharacterListResponeDto;
import lostark.todo.controller.dto.characterDto.CharacterUpdateDto;
import lostark.todo.controller.dto.characterDto.CharacterUpdateListDto;
import lostark.todo.controller.dto.memberDto.MemberLoginDto;
import lostark.todo.controller.dto.memberDto.MemberResponseDto;
import lostark.todo.domain.character.Character;
import lostark.todo.exhandler.ErrorResponse;
import lostark.todo.service.CharacterService;
import lostark.todo.service.MemberService;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.*;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Transactional
class MemberApiControllerTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate testRestTemplate;

    @Autowired
    TokenProvider tokenProvider;

    @Autowired
    CharacterService characterService;

    @Autowired
    MemberService memberService;


    String username = "test";
    String token;
    
    @BeforeEach
    void login() {
        //PATCH 오류 해결
        testRestTemplate.getRestTemplate().setRequestFactory(new HttpComponentsClientHttpRequestFactory());

        String password = "1234";
        MemberLoginDto memberLoginDto = MemberLoginDto.builder()
                .username(username)
                .password(password)
                .build();

        String url = "http://localhost:"+port+"/api/auth/login";
        
        ResponseEntity<MemberResponseDto> responseEntity = testRestTemplate.postForEntity(url, memberLoginDto, MemberResponseDto.class);
        token = responseEntity.getBody().getToken();


    }
    
    @Test
    @DisplayName("캐릭터 리스트 호출 성공")
    void getCharacterList() {
        //given
        String url = "http://localhost:"+port+"/api/member/characterList";

        MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
        headers.add("Content-Type", "application/json");
        headers.set("Authorization", "Bearer " + token);

        //when
        ResponseEntity<CharacterListResponeDto> responseEntity = new TestRestTemplate().exchange(url, HttpMethod.GET,
                new HttpEntity<Object>(headers), CharacterListResponeDto.class);

        //then
        Assertions.assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);

        List<Character> characters = memberService.findMember(username).getCharacters();
        Assertions.assertThat(responseEntity.getBody().getCharacters().size()).isEqualTo(characters.size());
        System.out.println("responseEntity.getBody() = " + responseEntity.getBody());
    }

    @Test
    @DisplayName("캐릭터 리스트 호출 실패 - Token 미인증 (403 에러)")
    void getCharacterListTokenError() {
        //given
        String url = "http://localhost:"+port+"/api/member/characterList";

        MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
        headers.add("Content-Type", "application/json");

        //when
        ResponseEntity<ErrorResponse> responseEntity = testRestTemplate.exchange(url, HttpMethod.GET,
                new HttpEntity<Object>(headers), ErrorResponse.class);

        //then
        Assertions.assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN); // 403 에러
    }

    @Test
    @DisplayName("캐릭터 리스트 업데이트 성공")
    void updateCharacterList() {
        //given
        String characterName = "마볼링";
        MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
        headers.add("Content-Type", "application/json");
        headers.set("Authorization", "Bearer " + token);

        List<CharacterUpdateDto> characterUpdateDtoList = new ArrayList<>();
        CharacterUpdateDto characterUpdateDto = CharacterUpdateDto.builder()
                .characterName(characterName)
                .chaosCheck(2)
                .chaosSelected(false)
                .guardianCheck(0)
                .guardianSelected(false)
                .build();

        characterUpdateDtoList.add(characterUpdateDto);

        CharacterUpdateListDto body = CharacterUpdateListDto.builder()
                .characterUpdateDtoList(characterUpdateDtoList)
                .build();

        String url = "http://localhost:"+port+"/api/member/characterList";

        //when
        ResponseEntity<CharacterUpdateListDto> responseEntity = testRestTemplate.exchange(url, HttpMethod.PATCH,
                new HttpEntity<Object>(body, headers), CharacterUpdateListDto.class);


        //then
        Character after = characterService.findCharacter(characterName);

        Assertions.assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        CharacterUpdateDto updateDto = responseEntity.getBody().getCharacterUpdateDtoList().get(0);


        Assertions.assertThat(updateDto.getChaosCheck()).isEqualTo(after.getCharacterDayContent().getChaosCheck());
        Assertions.assertThat(updateDto.getChaosSelected()).isEqualTo(after.getCharacterDayContent().isChaosSelected());
        Assertions.assertThat(updateDto.getGuardianCheck()).isEqualTo(after.getCharacterDayContent().getGuardianCheck());
        Assertions.assertThat(updateDto.getChaosSelected()).isEqualTo(after.getCharacterDayContent().isChaosSelected());
    }

    @Test
    @DisplayName("캐릭터 리스트 업데이트 실패 - Token 미인증 (403 에러)")
    void updateCharacterListTokenError() {
        //given
        MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
        headers.add("Content-Type", "application/json");

        List<CharacterUpdateDto> characterUpdateDtoList = new ArrayList<>();
        CharacterUpdateDto characterUpdateDto = CharacterUpdateDto.builder()
                .characterName("마볼링")
                .chaosCheck(2)
                .chaosSelected(false)
                .guardianCheck(0)
                .guardianSelected(false)
                .build();

        characterUpdateDtoList.add(characterUpdateDto);

        CharacterUpdateListDto body = CharacterUpdateListDto.builder()
                .characterUpdateDtoList(characterUpdateDtoList)
                .build();

        String url = "http://localhost:"+port+"/api/member/characterList";


        //when
        ResponseEntity<ErrorResponse> responseEntity = testRestTemplate.exchange(url, HttpMethod.PATCH,
                new HttpEntity<Object>(body, headers), ErrorResponse.class);

        //then
        Assertions.assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN); // 403 에러
    }

    @Test
    @DisplayName("캐릭터 리스트 업데이트 실패 - @Valid Error")
    void updateCharacterListValidError() {
        //given
        MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
        headers.add("Content-Type", "application/json");
        headers.set("Authorization", "Bearer " + token);


        List<CharacterUpdateDto> characterUpdateDtoList = new ArrayList<>();
        CharacterUpdateDto characterUpdateDto = CharacterUpdateDto.builder()
                .characterName("마볼링")
                .chaosCheck(21)
                .guardianCheck(0)
                .guardianSelected(false)
                .build();

        characterUpdateDtoList.add(characterUpdateDto);

        CharacterUpdateListDto body = CharacterUpdateListDto.builder()
                .characterUpdateDtoList(characterUpdateDtoList)
                .build();

        String url = "http://localhost:"+port+"/api/member/characterList";


        //when
        ResponseEntity<ErrorResponse> responseEntity = testRestTemplate.exchange(url, HttpMethod.PATCH,
                new HttpEntity<Object>(body, headers), ErrorResponse.class);

        //then
        Assertions.assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST); // 403 에러
        Assertions.assertThat(responseEntity.getBody().getErrorMessage()).containsOnly(
                "[characterUpdateDtoList[0].chaosCheck](은)는 2 이하여야 합니다 입력된 값: [21]",
                "[characterUpdateDtoList[0].chaosSelected](은)는 널이어서는 안됩니다 입력된 값: [null]");
    }
}