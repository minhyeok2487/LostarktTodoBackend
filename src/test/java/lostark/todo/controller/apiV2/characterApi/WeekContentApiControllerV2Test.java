package lostark.todo.controller.apiV2.characterApi;

import lombok.extern.slf4j.Slf4j;
import lostark.todo.controller.dto.characterDto.CharacterDto;
import lostark.todo.controller.dto.contentDto.WeekContentDto;
import lostark.todo.domain.character.Character;
import lostark.todo.domain.todoV2.TodoV2;
import lostark.todo.service.CharacterService;
import lostark.todo.service.TodoV2ServiceTestCode;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.test.context.junit4.SpringRunner;
import java.util.List;
import java.util.stream.Collectors;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Slf4j
class WeekContentApiControllerV2Test {

    @LocalServerPort
    private int port;

    private String JWT = "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJyZXBlYXQyNDg3QGdtYWlsLmNvbSIsImlzcyI6Ikxvc3RhcmtUb2RvIiwiaWF0IjoxNjk4MDM4NjcxfQ.WA4dEa8UgOxsezUpN9sYMmX34t4Hcv6rh-qn9hS_lxSm3KgDnanvXhJkQxrJMO-vD_ufP0WBTX3SxI3PeYZOMg";

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    CharacterService characterService;

    @Autowired
    TodoV2ServiceTestCode todoV2ServiceTestCode;

    private long startTime;
    @BeforeEach()
    void setTime() {
        startTime = System.currentTimeMillis(); // 작업 시작 시간 기록
    }

    @AfterEach()
    void executionTime() {
        long endTime = System.currentTimeMillis(); // 작업 종료 시간 기록
        long executionTime = endTime - startTime; // 작업에 걸린 시간 계산
        log.info("time: {} ms", executionTime);
    }

    @DisplayName("캐릭터 주간 숙제 추가폼 테스트")
    @Test
    void getTodoFormTest(){
        //given
        long id = 161L;
        String characterName = "마볼링";
        String username = "repeat2487@gmail.com";

        String url = "http://localhost:"+port+"/v2/character/week/form/"+id+"/"+characterName;

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer "+JWT);
        HttpEntity<Object> entity = new HttpEntity<>(null, headers);

        //when
        ResponseEntity<List<WeekContentDto>> response = restTemplate.exchange(url,HttpMethod.GET, entity, new ParameterizedTypeReference<List<WeekContentDto>>() {});
        List<WeekContentDto> responseBody = response.getBody();

        //then
        Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        List<WeekContentDto> collect = responseBody.stream().filter(weekContentDto -> weekContentDto.isChecked()).collect(Collectors.toList());
        List<TodoV2> todoV2List = todoV2ServiceTestCode.findTodoV2List(characterService.findCharacter(id, characterName, username));
        Assertions.assertThat(collect.size()).isEqualTo(todoV2List.size());
    }
}