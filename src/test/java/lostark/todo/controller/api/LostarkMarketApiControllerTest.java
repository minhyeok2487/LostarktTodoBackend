package lostark.todo.controller.api;

import lostark.todo.controller.dto.characterDto.CharacterListResponeDto;
import lostark.todo.controller.dto.marketDto.MarketListDto;
import lostark.todo.controller.dto.memberDto.MemberLoginDto;
import lostark.todo.controller.dto.memberDto.MemberResponseDto;
import lostark.todo.domain.character.Character;
import lostark.todo.domain.market.CategoryCode;
import lostark.todo.exhandler.ErrorResponse;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
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

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Transactional
@Rollback(value = false)
class LostarkMarketApiControllerTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate testRestTemplate;

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
    @DisplayName("로스트아크 거래소 데이터 호출 성공 CategoryCode = 50000")
    void getMarketCategoryCode() {
        //given
        CategoryCode categoryCode = CategoryCode.재련재료;
        String url = "http://localhost:"+port+"/api/lostark/market/" + categoryCode;

        MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
        headers.add("Content-Type", "application/json");
        headers.set("Authorization", "Bearer " + token);

        //when
        ResponseEntity<MarketListDto> responseEntity = new TestRestTemplate().exchange(url, HttpMethod.GET,
                new HttpEntity<Object>(headers), MarketListDto.class);

        //then
        Assertions.assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    @DisplayName("로스트아크 거래소 데이터 호출 실패 - 올바르지 않은 categoryCode")
    void getMarketCategoryCodeCategoryCodeError() {

        //given
        CategoryCode categoryCode = CategoryCode.테스트용;
        String url = "http://localhost:"+port+"/api/lostark/market/" + categoryCode;

        MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
        headers.add("Content-Type", "application/json");
        headers.set("Authorization", "Bearer " + token);

        //when
        ResponseEntity<ErrorResponse> responseEntity = new TestRestTemplate().exchange(url, HttpMethod.GET,
                new HttpEntity<Object>(headers), ErrorResponse.class);

        //then
        Assertions.assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        Assertions.assertThat(responseEntity.getBody().getErrorMessage().get(0)).isEqualTo("올바르지 않은 categoryCode");
    }
}