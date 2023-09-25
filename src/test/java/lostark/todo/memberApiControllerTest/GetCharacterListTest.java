package lostark.todo.memberApiControllerTest;

import lostark.todo.controller.dto.characterDto.CharacterResponseDto;
import lostark.todo.controller.dto.memberDto.MemberRequestDto;
import lostark.todo.controller.dto.memberDto.MemberResponseDto;
import lostark.todo.domain.character.Character;
import lostark.todo.domain.member.Member;
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
import org.springframework.core.ParameterizedTypeReference;
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
public class GetCharacterListTest {
//    @LocalServerPort
//    private int port;
//
//    @Autowired
//    private TestRestTemplate testRestTemplate;
//
//    @Autowired
//    CharacterService characterService;
//
//    @Autowired MemberService memberService;
//    @Autowired ContentService contentService;
//
//    @Value("${Lostark-API-Test-Key}")
//    String apiKey;
//
//    String username = "repeat2487@gmail.com";
//
//    String token = "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJyZXBlYXQyNDg3QGdtYWlsLmNvbSIsImlzcyI6Ikxvc3RhcmtUb2RvIiwiaWF0IjoxNjk1MDI3NjQ3fQ.7XIW0ujAigt562bEqtN-K2t_ETcgtYMsxWrRnoT54FWNuUfwLtE-0-GirhAhOqsQN67BOrE4tQtlolB7xQxgZw";
//
//    String url = "";
//    MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
//
//    @BeforeEach
//    void before() {
//        //PATCH 오류 해결
//        testRestTemplate.getRestTemplate().setRequestFactory(new HttpComponentsClientHttpRequestFactory());
//
//        //url
//        url = "http://localhost:"+port+"/member/characterList";
//
//        //headers
//        headers.add("Content-Type", "application/json");
//        headers.set("Authorization", "Bearer " + token);
//    }
//
//    @Test
//    @DisplayName("회원 캐릭터 리스트 조회 성공")
//    void getCharacterList() {
//        //given
//
//        //when
//        ResponseEntity<List<CharacterResponseDto>> responseEntity = new TestRestTemplate().exchange(
//                url, HttpMethod.GET, new HttpEntity<Object>(headers),
//                new ParameterizedTypeReference<List<CharacterResponseDto>>() {});
//
//
//        //then
//        Member member = memberService.findMember(username);
//        Assertions.assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
//        Assertions.assertThat(responseEntity.getBody().size()).isEqualTo(member.getCharacters().size());
//    }
//
//    @Test
//    @DisplayName("회원 캐릭터 리스트 조회 실패 - 등록된 캐릭터 없음")
//    void getCharacterList_CharacterListIsNull() {
//        //given
//        String token = "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJxd2UyNDg3QGFqb3UuYWMua3IiLCJpc3MiOiJMb3N0YXJrVG9kbyIsImlhdCI6MTY5NDc4NTExN30.IT2trQ9cVlQSfb-ouh5pVOnD2Ews_OArzTpDhVDlpkjocS2WWDBfauGDqQTDrPAQJMJSJluR_1Ry1DyBb-2jEA";
//        headers.set("Authorization", "Bearer " + token);
//
//        //when
//        ResponseEntity<ErrorResponse> responseEntity = new TestRestTemplate().exchange(url, HttpMethod.GET,
//                new HttpEntity<Object>(headers), ErrorResponse.class);
//
//        //then
//        Assertions.assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
//        Assertions.assertThat(responseEntity.getBody().getErrorMessage().get(0)).isEqualTo("등록된 캐릭터가 없습니다.");
//    }
    
}
