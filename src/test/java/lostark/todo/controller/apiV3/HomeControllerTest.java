package lostark.todo.controller.apiV3;

import lombok.Getter;
import lostark.todo.controller.dto.characterDto.CharacterDto;
import lostark.todo.controller.dto.todoDto.TodoResponseDto;
import lostark.todo.service.CharacterService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.transaction.Transactional;

import java.util.*;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class HomeControllerTest {

    @Autowired
    CharacterService characterService;

    @Test
    void asd() {
        String username = "repeat2487@gmail.com";
        //2. 전체 캐릭터 데이터
        List<CharacterDto> characters = characterService.findCharacterListUsername(username).stream()
                .map(character -> new CharacterDto().toDtoV2(character))
                .sorted(Comparator
                        .comparingInt(CharacterDto::getSortNumber)
                        .thenComparing(Comparator.comparingDouble(CharacterDto::getItemLevel).reversed()))
                .collect(Collectors.toList());

        //3. 숙제 현황
        List<String> sortList = Arrays.asList("에키드나", "카멘", "상아탑", "일리아칸", "카양겔", "아브렐슈드", "쿠크세이튼", "비아키스", "발탄", "아르고스");
        Map<String, List<TodoResponseDto>> todoListGroupedByWeekCategory = characters.stream()
                .flatMap(character -> character.getTodoList().stream())
                .collect(Collectors.groupingBy(TodoResponseDto::getWeekCategory));

        List<Dto> dtoList = sortList.stream()
                .map(key -> {
                    List<TodoResponseDto> todoResponseDtos = todoListGroupedByWeekCategory.get(key);
                    int count = 0;
                    int totalCount = 0;
                    if (todoResponseDtos != null) {
                        count = (int) todoResponseDtos.stream().filter(TodoResponseDto::isCheck).count();
                        totalCount = todoResponseDtos.size();
                    }
                    return new Dto(key, count, totalCount);
                })
                .filter(dto -> dto.getTotalCount() > 0)
                .collect(Collectors.toList());

        System.out.println("dtoList = " + dtoList);

    }
}

@Getter
class Dto {

    private String name;

    private int count;

    private int totalCount;

    public Dto(String name, int count, int totalCount) {
        this.name = name;
        this.count = count;
        this.totalCount = totalCount;
    }

    @Override
    public String toString() {
        return "Dto{" +
                "name='" + name + '\'' +
                ", count=" + count +
                ", totalCount=" + totalCount +
                '}';
    }
}