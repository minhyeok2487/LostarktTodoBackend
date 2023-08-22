package lostark.todo.controller.api;

import lostark.todo.config.TokenProvider;
import lostark.todo.controller.dto.characterDto.CharacterListResponeDto;
import lostark.todo.controller.dto.characterDto.CharacterResponseDto;
import lostark.todo.controller.dto.characterDto.CharacterCheckDto;
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
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Transactional
@Rollback(value = false)
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
    @DisplayName("캐릭터 Todo 업데이트 성공")
    void updateTodo() {
        //given
        String characterName = "마볼링";
        MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
        headers.add("Content-Type", "application/json");
        headers.set("Authorization", "Bearer " + token);

        List<CharacterCheckDto> characterCheckDtoList = new ArrayList<>();
        CharacterCheckDto characterCheckDto = CharacterCheckDto.builder()
                .characterName(characterName)
                .chaosCheck(2)
                .guardianCheck(0)
                .build();

        characterCheckDtoList.add(characterCheckDto);

        CharacterUpdateListDto body = CharacterUpdateListDto.builder()
                .characterCheckDtoList(characterCheckDtoList)
                .build();

        String url = "http://localhost:"+port+"/api/member/todo";

        //when
        ResponseEntity<CharacterUpdateListDto> responseEntity = testRestTemplate.exchange(url, HttpMethod.PATCH,
                new HttpEntity<Object>(body, headers), CharacterUpdateListDto.class);


        //then
        Character after = characterService.findCharacter(characterName);

        Assertions.assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        CharacterCheckDto updateDto = responseEntity.getBody().getCharacterCheckDtoList().get(0);


        Assertions.assertThat(updateDto.getChaosCheck()).isEqualTo(after.getCharacterDayContent().getChaosCheck());
        Assertions.assertThat(updateDto.getGuardianCheck()).isEqualTo(after.getCharacterDayContent().getGuardianCheck());
    }

    @Test
    @DisplayName("캐릭터 Todo 업데이트 실패 - Token 미인증 (403 에러)")
    void updateTodoTokenError() {
        //given
        MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
        headers.add("Content-Type", "application/json");

        List<CharacterCheckDto> characterCheckDtoList = new ArrayList<>();
        CharacterCheckDto characterCheckDto = CharacterCheckDto.builder()
                .characterName("마볼링")
                .chaosCheck(2)
                .guardianCheck(0)
                .build();

        characterCheckDtoList.add(characterCheckDto);

        CharacterUpdateListDto body = CharacterUpdateListDto.builder()
                .characterCheckDtoList(characterCheckDtoList)
                .build();

        String url = "http://localhost:"+port+"/api/member/todo";


        //when
        ResponseEntity<ErrorResponse> responseEntity = testRestTemplate.exchange(url, HttpMethod.PATCH,
                new HttpEntity<Object>(body, headers), ErrorResponse.class);

        //then
        Assertions.assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN); // 403 에러
    }

    @Test
    @DisplayName("캐릭터 Todo 업데이트 실패 - @Valid Error")
    void updateTodoValidError() {
        //given
        MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
        headers.add("Content-Type", "application/json");
        headers.set("Authorization", "Bearer " + token);


        List<CharacterCheckDto> characterCheckDtoList = new ArrayList<>();
        CharacterCheckDto characterCheckDto = CharacterCheckDto.builder()
                .characterName("마볼링")
                .chaosCheck(21)
                .guardianCheck(0)
                .build();

        characterCheckDtoList.add(characterCheckDto);

        CharacterUpdateListDto body = CharacterUpdateListDto.builder()
                .characterCheckDtoList(characterCheckDtoList)
                .build();

        String url = "http://localhost:"+port+"/api/member/todo";


        //when
        ResponseEntity<ErrorResponse> responseEntity = testRestTemplate.exchange(url, HttpMethod.PATCH,
                new HttpEntity<Object>(body, headers), ErrorResponse.class);

        //then
        Assertions.assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST); // 403 에러
        Assertions.assertThat(responseEntity.getBody().getErrorMessage()).contains(
                "[characterUpdateDtoList[0].chaosCheck](은)는 2 이하여야 합니다 입력된 값: [21]",
                "[characterUpdateDtoList[0].chaosSelected](은)는 널이어서는 안됩니다 입력된 값: [null]");
    }

    @Test
    @DisplayName("캐릭터 리스트 업데이트 성공")
    void updateCharacterList() {
        // given
        MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
        headers.add("Content-Type", "application/json");
        headers.set("Authorization", "Bearer " + token);

        String url = "http://localhost:" + port + "/api/member/characterList";

        // when
        ResponseEntity<CharacterResponseDto[]> responseEntity = testRestTemplate.exchange(
                url, HttpMethod.PATCH, new HttpEntity<>(headers), CharacterResponseDto[].class);

        // then
        CharacterResponseDto[] responseArray = responseEntity.getBody();
        if (responseArray != null) {
            List<CharacterResponseDto> responseList = Arrays.asList(responseArray);
            for (CharacterResponseDto dto : responseList) {
                System.out.println("CharacterResponseDto: " + dto);
            }
        } else {
            System.out.println("Response array is null.");
        }
    }

}