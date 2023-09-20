package lostark.todo.memberApiControllerTest;

import lostark.todo.controller.dto.memberDto.MemberRequestDto;
import lostark.todo.controller.dto.memberDto.MemberResponseDto;
import lostark.todo.domain.character.Character;
import lostark.todo.exhandler.ErrorResponse;
import lostark.todo.service.CharacterService;
import lostark.todo.service.ContentService;
import lostark.todo.service.MemberService;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.test.annotation.Rollback;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import javax.transaction.Transactional;
import java.util.List;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Transactional
public class SignupCharacterV2Test {
    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate testRestTemplate;
    
    @Autowired
    CharacterService characterService;

    @Autowired MemberService memberService;
    @Autowired ContentService contentService;

    String token = "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJxd2UyNDg3QGFqb3UuYWMua3IiLCJpc3MiOiJMb3N0YXJrVG9kbyIsImlhdCI6MTY5NDc4NTExN30.IT2trQ9cVlQSfb-ouh5pVOnD2Ews_OArzTpDhVDlpkjocS2WWDBfauGDqQTDrPAQJMJSJluR_1Ry1DyBb-2jEA";

    String apiKey = "eyJ0eXAiOiJKV1QiLCJhbGciOiJSUzI1NiIsIng1dCI6IktYMk40TkRDSTJ5NTA5NWpjTWk5TllqY2lyZyIsImtpZCI6IktYMk40TkRDSTJ5NTA5NWpjTWk5TllqY2lyZyJ9.eyJpc3MiOiJodHRwczovL2x1ZHkuZ2FtZS5vbnN0b3ZlLmNvbSIsImF1ZCI6Imh0dHBzOi8vbHVkeS5nYW1lLm9uc3RvdmUuY29tL3Jlc291cmNlcyIsImNsaWVudF9pZCI6IjEwMDAwMDAwMDAzMjU2OTcifQ.foV7-Ixj53XnwWCnq-l0v-iiaRGZ0VP0CIavbWIqGB-xsUlJ2ZqoHbW712DdW23t1fCimNvZqh0JkLpDGFQ3Ynf_bEot2Fjdu58KhkUUvu29rcAR_2qtcKo6236RzNYO0j-olT-hu6zvd5t6z8xfBMIOTsPHBnN95sMWKhlKMIgqs1Kjv7_Koiji-x5bjz1ku6VGlBW6z7QpH496H_bVoW13Sk_TGW5BEgXtDjHBXLNYs910BMuzS_wN2rzlfoBcUaE5LrFCFMvMFaw2QazJEzpMTnKUIrt4V6LzRYoKsNh8fvg0tLisbnZl-X_f6k_v_MyF3jM6P14FjTAZa70WRA";

//    @Value("${Lostark-API-Test-Key}")
//    String apiKey;

    String url = "";
    MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();

    @BeforeEach
    void before() {
        //PATCH 오류 해결
        testRestTemplate.getRestTemplate().setRequestFactory(new HttpComponentsClientHttpRequestFactory());

        //url
        url = "http://localhost:"+port+"/member/signup";

        //headers
        headers.add("Content-Type", "application/json");
        headers.set("Authorization", "Bearer " + token);
    }

    @Test
    @DisplayName("캐릭터 데이터 추가 성공")
    @Rollback(value = false)
    void signupCharacterV2() {
        //given
        MemberRequestDto memberDto = MemberRequestDto.builder()
                .characterName("LivingAimTR")
                .apiKey(apiKey)
                .build();
        
        //when
        ResponseEntity<MemberResponseDto> responseEntity = new TestRestTemplate().exchange(url, HttpMethod.POST,
                new HttpEntity<Object>(memberDto, headers), MemberResponseDto.class);

        //then
        Assertions.assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        List<Character> characters = responseEntity.getBody().getCharacters();
//        for (Character character : characters) {
//            double contentLevel = character.getDayTodo().getChaos().getLevel();
//            Assertions.assertThat(contentLevel).isGreaterThanOrEqualTo(character.getCharacterLevel());
//        }

        for (Character character : characters) {
            System.out.println("character = " + character);
        }

        //다음 테스트를 위한 데이터 삭제
        characterService.deleteCharacter(memberService.findMember(responseEntity.getBody().getUsername()));
    }

    @Test
    @DisplayName("캐릭터 데이터 추가 실패 - 없는 캐릭터명")
    void signupCharacterV2_CharacterNameIsNull() {
        //given
        String characterName = "asdasdasdasvfvbwfqwdqwdxcacasdwqdwdzxcz";

        MemberRequestDto memberDto = MemberRequestDto.builder()
                .characterName(characterName)
                .apiKey(apiKey)
                .build();

        //when
        ResponseEntity<ErrorResponse> responseEntity = new TestRestTemplate().exchange(url, HttpMethod.POST,
                new HttpEntity<Object>(memberDto, headers), ErrorResponse.class);

        //then
        Assertions.assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        Assertions.assertThat(responseEntity.getBody().getErrorMessage().get(0)).isEqualTo(characterName +" 은(는) 존재하지 않는 캐릭터 입니다.");
    }

    @Test
    @DisplayName("캐릭터 데이터 추가 실패 - Validation")
    void signupCharacterV2_Validation() {
        //given
        MemberRequestDto memberDto = MemberRequestDto.builder()
                .apiKey(apiKey)
                .build();

        //when
        ResponseEntity<ErrorResponse> responseEntity = new TestRestTemplate().exchange(url, HttpMethod.POST,
                new HttpEntity<Object>(memberDto, headers), ErrorResponse.class);

        //then
        Assertions.assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        Assertions.assertThat(responseEntity.getBody().getErrorMessage().get(0)).isEqualTo("[characterName](은)는 비어 있을 수 없습니다 입력된 값: [null]");
    }

    @Test
    @DisplayName("캐릭터 데이터 추가 실패 - apikey")
    void signupCharacterV2_ApiKey() {
        //given
        MemberRequestDto memberDto = MemberRequestDto.builder()
                .characterName("이다")
                .apiKey("asdasdcxqw")
                .build();

        //when
        ResponseEntity<ErrorResponse> responseEntity = new TestRestTemplate().exchange(url, HttpMethod.POST,
                new HttpEntity<Object>(memberDto, headers), ErrorResponse.class);

        //then
        Assertions.assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        Assertions.assertThat(responseEntity.getBody().getErrorMessage().get(0)).isEqualTo("올바르지 않은 apiKey 입니다");
    }
}
