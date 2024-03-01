package lostark.todo.controller.apiV3;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lostark.todo.controller.dto.characterDto.CharacterDto;
import lostark.todo.controller.dto.homeDto.HomeDto;
import lostark.todo.controller.dto.homeDto.HomeRaidDto;
import lostark.todo.controller.dto.todoDto.TodoResponseDto;
import lostark.todo.domain.character.Character;
import lostark.todo.domain.todoV2.TodoV2;
import lostark.todo.service.CharacterService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/v3/home")
@Api(tags = {"메인 화면"})
public class HomeController {

    private static final List<String> RAID_SORT_ORDER = Arrays.asList(
            "에키드나", "카멘", "상아탑", "일리아칸", "카양겔", "아브렐슈드", "쿠크세이튼", "비아키스", "발탄", "아르고스");

    private final CharacterService characterService;

    @ApiOperation(value = "메인 화면 데이터 호출", notes = "캐릭터 데이터, 숙제 현황")
    @GetMapping()
    public ResponseEntity<?> findAll(@AuthenticationPrincipal String username) {
        //1. 전체 캐릭터 데이터
        List<Character> characterList = characterService.findCharacterListUsername(username);

        //2. 전체 캐릭터 데이터 -> DtoSortedList
        List<CharacterDto> characterDtoList = characterService.updateDtoSortedList(characterList);

        //3. 대표 캐릭터
        CharacterDto mainCharacter = characterDtoList.get(0);

        //4. 주간 총 수익
        double weekTotalGold = characterService.calculateWeekTotalGold(characterList);
        double dayTotalGold = characterService.calculateDayTotalGold(characterList);

        //5. 숙제 현황
        List<HomeRaidDto> homeRaidDtos = calculateRaidStatus(characterDtoList);

        HomeDto build = HomeDto.builder()
                .homeRaidDtoList(homeRaidDtos)
                .characterDtoList(characterDtoList)
                .mainCharacter(mainCharacter)
                .weekTotalGold(weekTotalGold)
                .dayTotalGold(dayTotalGold)
                .build();

        return new ResponseEntity<>(build, HttpStatus.OK);
    }

    // 캐릭터 리스트의 숙제 현황을 계산하는 메서드
    private List<HomeRaidDto> calculateRaidStatus(List<CharacterDto> characterDtoList) {
        Map<String, List<TodoResponseDto>> todoListGroupedByWeekCategory = characterDtoList.stream()
                .flatMap(character -> character.getTodoList().stream())
                .collect(Collectors.groupingBy(TodoResponseDto::getWeekCategory));

        return RAID_SORT_ORDER.stream()
                .map(key -> {
                    List<TodoResponseDto> todoResponseDtos = todoListGroupedByWeekCategory.get(key);
                    int count = 0;
                    int totalCount = 0;
                    if (todoResponseDtos != null) {
                        count = (int) todoResponseDtos.stream().filter(TodoResponseDto::isCheck).count();
                        totalCount = todoResponseDtos.size();
                    }
                    return new HomeRaidDto(key, count, totalCount);
                })
                .filter(dto -> dto.getTotalCount() > 0)
                .collect(Collectors.toList());
    }

}
