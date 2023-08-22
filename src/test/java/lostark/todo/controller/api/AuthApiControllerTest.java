package lostark.todo.controller.api;

import lostark.todo.config.TokenProvider;
import lostark.todo.controller.dto.memberDto.MemberLoginDto;
import lostark.todo.controller.dto.memberDto.MemberResponseDto;
import lostark.todo.controller.dto.memberDto.MemberDto;
import lostark.todo.exhandler.ErrorResponse;
import lostark.todo.service.MemberService;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.MethodArgumentNotValidException;

import static org.assertj.core.api.Assertions.assertThat;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Transactional
@Rollback(value = false)
class AuthApiControllerTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate testRestTemplate;

    @Autowired
    MemberService memberService;

    @Autowired
    TokenProvider tokenProvider;

    @Value("${Lostark-API-Test-Key}")
    String apiKey;
    
    @Test
    @DisplayName("회원가입 성공")
    void signupMember() {
        //given
        String username = "testUser";
        String password = "11223344";
        String characterName = "바람불어야지요";
        MemberDto memberDto = MemberDto.builder()
                .username(username)
                .password(password)
                .characterName(characterName)
                .apiKey(apiKey)
                .build();

        String url = "http://localhost:"+port+"/api/auth/signup";

        //when
        ResponseEntity<MemberResponseDto> responseEntity = testRestTemplate.postForEntity(url, memberDto, MemberResponseDto.class);

        //then
        Assertions.assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        Assertions.assertThat(responseEntity.getBody().getId()).isGreaterThan(0L);
        Assertions.assertThat(responseEntity.getBody().getUsername()).isEqualTo(username);
        Assertions.assertThat(responseEntity.getBody().getCharacters().size()).isGreaterThan(0);

        memberService.deleteMember(username);
    }

    @Test
    @DisplayName("회원가입 실패 - username 중복")
    void signupMemberDuplicateUsername() {
        //given
        String username = "test";
        String password = "11223344";
        String characterName = "바람불어야지요";
        MemberDto memberDto = MemberDto.builder()
                .username(username)
                .password(password)
                .characterName(characterName)
                .apiKey(apiKey)
                .build();

        String url = "http://localhost:"+port+"/api/auth/signup";

        //when
        ResponseEntity<ErrorResponse> responseEntity = testRestTemplate.postForEntity(url, memberDto, ErrorResponse.class);

        //then
        Assertions.assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        Assertions.assertThat(responseEntity.getBody().getExceptionName()).isEqualTo(IllegalArgumentException.class.getSimpleName());
        Assertions.assertThat(responseEntity.getBody().getErrorMessage().get(0)).isEqualTo(username + " 이미 존재하는 username 입니다.");
    }

    @Test
    @DisplayName("회원가입 실패 - 존재하지 않는 캐릭터")
    void signupMemberCharacterIsNotExist() {
        //given
        String username = "testUser";
        String password = "11223344";
        String characterName = "cvsdfadsdfqwdqwdxczxc";
        MemberDto memberDto = MemberDto.builder()
                .username(username)
                .password(password)
                .characterName(characterName)
                .apiKey(apiKey)
                .build();

        String url = "http://localhost:"+port+"/api/auth/signup";

        //when
        ResponseEntity<ErrorResponse> responseEntity = testRestTemplate.postForEntity(url, memberDto, ErrorResponse.class);

        //then
        Assertions.assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        Assertions.assertThat(responseEntity.getBody().getExceptionName()).isEqualTo(RuntimeException.class.getSimpleName());
        Assertions.assertThat(responseEntity.getBody().getErrorMessage().get(0)).isEqualTo(characterName + " 은(는) 존재하지 않는 캐릭터 입니다.");
    }

    @Test
    @DisplayName("회원가입 실패 - 캐릭터 리스트 null")
    void signupMemberCharacterListIsNull() {
        //given
        String username = "testUser";
        String password = "11223344";
        String characterName = "파이썬볼러";
        MemberDto memberDto = MemberDto.builder()
                .username(username)
                .password(password)
                .characterName(characterName)
                .apiKey(apiKey)
                .build();

        String url = "http://localhost:"+port+"/api/auth/signup";

        //when
        ResponseEntity<ErrorResponse> responseEntity = testRestTemplate.postForEntity(url, memberDto, ErrorResponse.class);

        //then
        Assertions.assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        System.out.println("responseEntity.getBody() = " + responseEntity.getBody());
        Assertions.assertThat(responseEntity.getBody().getExceptionName()).isEqualTo(RuntimeException.class.getSimpleName());
        Assertions.assertThat(responseEntity.getBody().getErrorMessage().get(0)).isEqualTo("아이템 레벨 1415 이상 캐릭터가 없습니다.");
    }

    @Test
    @DisplayName("회원가입 실패 - @Valid Error")
    void signupMemberValid() {
        //given
        String password = "11223344";
        MemberDto memberDto = MemberDto.builder()
                .password(password)
                .apiKey(apiKey)
                .build();

        String url = "http://localhost:"+port+"/api/auth/signup";

        //when
        ResponseEntity<ErrorResponse> responseEntity = testRestTemplate.postForEntity(url, memberDto, ErrorResponse.class);

        //then
        Assertions.assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        Assertions.assertThat(responseEntity.getBody().getExceptionName()).isEqualTo(MethodArgumentNotValidException.class.getSimpleName());
        Assertions.assertThat(responseEntity.getBody().getErrorMessage()).contains(
                "[characterName](은)는 비어 있을 수 없습니다 입력된 값: [null]",
                "[username](은)는 비어 있을 수 없습니다 입력된 값: [null]");
    }

    @Test
    @DisplayName("로그인 성공")
    void login() {
        //given
        String username = "test";
        String password = "1234";
        MemberLoginDto memberLoginDto = MemberLoginDto.builder()
                .username(username)
                .password(password)
                .build();

        String url = "http://localhost:"+port+"/api/auth/login";

        //when
        ResponseEntity<MemberResponseDto> responseEntity = testRestTemplate.postForEntity(url, memberLoginDto, MemberResponseDto.class);

        //then
        Assertions.assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        String validToken = tokenProvider.validToken(responseEntity.getBody().getToken()); // 토큰 검증
        Assertions.assertThat(responseEntity.getBody().getUsername()).isEqualTo(validToken);
    }

    @Test
    @DisplayName("로그인 실패 - not equal password")
    void loginNotEqualPassword() {
        //given
        String username = "test";
        String password = "11";
        MemberLoginDto memberLoginDto = MemberLoginDto.builder()
                .username(username)
                .password(password)
                .build();

        String url = "http://localhost:"+port+"/api/auth/login";

        //when
        ResponseEntity<ErrorResponse> responseEntity = testRestTemplate.postForEntity(url, memberLoginDto, ErrorResponse.class);

        //then
        Assertions.assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        Assertions.assertThat(responseEntity.getBody().getExceptionName()).isEqualTo(IllegalArgumentException.class.getSimpleName());
        assertThat(responseEntity.getBody().getErrorMessage().get(0)).isEqualTo("패스워드가 틀립니다.");
    }

    @Test
    @DisplayName("로그인 실패 - @Valid Error")
    void createMemberNotEmpty() {
        //given
        String password = "11";
        MemberLoginDto memberLoginDto = MemberLoginDto.builder()
                .password(password)
                .build();

        String url = "http://localhost:"+port+"/api/auth/login";

        // when
        ResponseEntity<ErrorResponse> responseEntity = testRestTemplate.postForEntity(url, memberLoginDto, ErrorResponse.class);

        // then
        Assertions.assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        Assertions.assertThat(responseEntity.getBody().getExceptionName()).isEqualTo(MethodArgumentNotValidException.class.getSimpleName());
        Assertions.assertThat(responseEntity.getBody().getErrorMessage()).contains(
                "[username](은)는 비어 있을 수 없습니다 입력된 값: [null]");
    }

}