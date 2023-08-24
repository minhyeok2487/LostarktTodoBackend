package lostark.todo.controller.api;

import lostark.todo.config.TokenProvider;
import lostark.todo.controller.dto.characterDto.CharacterGaugeDto;
import lostark.todo.controller.dto.characterDto.CharacterResponseDto;
import lostark.todo.controller.dto.characterDto.CharacterUpdateListDto;
import lostark.todo.controller.dto.memberDto.MemberDto;
import lostark.todo.controller.dto.memberDto.MemberLoginDto;
import lostark.todo.controller.dto.memberDto.MemberResponseDto;
import lostark.todo.exhandler.ErrorResponse;
import lostark.todo.service.CharacterService;
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
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Transactional
@Rollback(value = false)
class CharacterApiControllerTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate testRestTemplate;

    @Autowired
    CharacterService characterService;

    @Autowired
    TokenProvider tokenProvider;

    @Value("${Lostark-API-Test-Key}")
    String apiKey;

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
    @DisplayName("캐릭터 휴식게이지 업데이트 성공")
    void updateCharacter() {
        //given
        CharacterGaugeDto body = CharacterGaugeDto.builder()
                .characterName("마볼링")
                .chaosGauge(10)
                .guardianGauge(10)
                .build();

        String url = "http://localhost:"+port+"/api/character";

        MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
        headers.add("Content-Type", "application/json");
        headers.set("Authorization", "Bearer " + token);

        //when
        ResponseEntity<CharacterResponseDto> responseEntity = testRestTemplate.exchange(url, HttpMethod.PATCH,
                new HttpEntity<Object>(body, headers), CharacterResponseDto.class);

        //then
        Assertions.assertThat(body.getCharacterName()).isEqualTo(responseEntity.getBody().getCharacterName());
        Assertions.assertThat(body.getChaosGauge()).isEqualTo(responseEntity.getBody().getChaosGauge());
        Assertions.assertThat(body.getGuardianGauge()).isEqualTo(responseEntity.getBody().getGuardianGauge());
    }

    @Test
    @DisplayName("캐릭터 휴식게이지 업데이트 실패 : username에 연결된 캐릭터가 아님")
    void updateCharacterError() {
        //given
        CharacterGaugeDto body = CharacterGaugeDto.builder()
                .characterName("마볼링2222")
                .chaosGauge(10)
                .guardianGauge(10)
                .build();

        String url = "http://localhost:"+port+"/api/character";

        MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
        headers.add("Content-Type", "application/json");
        headers.set("Authorization", "Bearer " + token);

        //when
        ResponseEntity<ErrorResponse> responseEntity = testRestTemplate.exchange(url, HttpMethod.PATCH,
                new HttpEntity<Object>(body, headers), ErrorResponse.class);

        //then
        Assertions.assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        Assertions.assertThat(responseEntity.getBody().getErrorMessage().get(0))
                .isEqualTo("characterName = 마볼링2222 / username = test : 존재하지 않는 캐릭터");
    }
}