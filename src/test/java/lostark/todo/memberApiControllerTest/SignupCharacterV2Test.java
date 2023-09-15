package lostark.todo.memberApiControllerTest;

import lostark.todo.controller.dto.memberDto.MemberDto;
import lostark.todo.controller.dto.memberDto.MemberResponseDto;
import lostark.todo.domain.character.Character;
import lostark.todo.domain.content.Category;
import lostark.todo.domain.content.DayContent;
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
@Rollback(value = false)
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

    @Value("${Lostark-API-Test-Key}")
    String apiKey;
    

    
    @BeforeEach
    void before() {
        //PATCH 오류 해결
        testRestTemplate.getRestTemplate().setRequestFactory(new HttpComponentsClientHttpRequestFactory());
    }

    @Test
    @DisplayName("캐릭터 데이터 추가 성공")
    void signupCharacterV2() {
        //given
        String url = "http://localhost:"+port+"/member/signup";

        MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
        headers.add("Content-Type", "application/json");
        headers.set("Authorization", "Bearer " + token);

        MemberDto memberDto = MemberDto.builder()
                .characterName("이다")
                .apiKey(apiKey)
                .build();
        
        //when
        ResponseEntity<MemberResponseDto> responseEntity = new TestRestTemplate().exchange(url, HttpMethod.POST,
                new HttpEntity<Object>(memberDto, headers), MemberResponseDto.class);

        //then
        Assertions.assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        List<Character> characters = responseEntity.getBody().getCharacters();
        for (Character character : characters) {
            double contentLevel = character.getDayTodo().getChaos().getLevel();
            Assertions.assertThat(contentLevel).isGreaterThanOrEqualTo(character.getCharacterLevel());
        }


        //다음 테스트를 위한 데이터 삭제
        characterService.deleteCharacter(memberService.findMember(responseEntity.getBody().getUsername()));
    }

    @Test
    @DisplayName("캐릭터 데이터 추가 실패 - 없는 캐릭터명")
    void signupCharacterV2_CharacterNameIsNull() {
        //given
        String url = "http://localhost:"+port+"/member/signup";

        MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
        headers.add("Content-Type", "application/json");
        headers.set("Authorization", "Bearer " + token);

        String characterName = "asdasdasdasvfvbwfqwdqwdxcacasdwqdwdzxcz";

        MemberDto memberDto = MemberDto.builder()
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
        String url = "http://localhost:"+port+"/member/signup";

        MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
        headers.add("Content-Type", "application/json");
        headers.set("Authorization", "Bearer " + token);

        MemberDto memberDto = MemberDto.builder()
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
        String url = "http://localhost:"+port+"/member/signup";

        MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
        headers.add("Content-Type", "application/json");
        headers.set("Authorization", "Bearer " + token);

        MemberDto memberDto = MemberDto.builder()
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
