package lostark.todo.controller.apiV3;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lostark.todo.controller.dto.boardsDto.*;
import lostark.todo.controller.dto.characterDto.CharacterDto;
import lostark.todo.controller.dto.homeDto.HomeDto;
import lostark.todo.controller.dto.homeDto.HomeRaidDto;
import lostark.todo.controller.dto.noticesDto.NoticesDto;
import lostark.todo.controller.dto.todoDto.TodoResponseDto;
import lostark.todo.domain.Role;
import lostark.todo.domain.boards.Boards;
import lostark.todo.domain.character.Character;
import lostark.todo.domain.member.Member;
import lostark.todo.domain.notices.Notices;
import lostark.todo.exhandler.exceptions.CustomIllegalArgumentException;
import lostark.todo.service.BoardsService;
import lostark.todo.service.CharacterService;
import lostark.todo.service.MemberService;
import lostark.todo.service.NoticesService;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/v3/home")
@Api(tags = {"메인 화면"})
public class HomeController {

    private final CharacterService characterService;

    @ApiOperation(value = "메인 화면 데이터 호출", notes = "캐릭터 데이터, 공지사항, 숙제 현황")
    @GetMapping()
    public ResponseEntity<?> findAll(@AuthenticationPrincipal String username) {
        //1. 전체 캐릭터 데이터
        List<CharacterDto> characters = characterService.findCharacterListUsername(username).stream()
                .map(character -> new CharacterDto().toDtoV2(character))
                .sorted(Comparator
                        .comparingInt(CharacterDto::getSortNumber)
                        .thenComparing(Comparator.comparingDouble(CharacterDto::getItemLevel).reversed()))
                .collect(Collectors.toList());

        //2. 대표 캐릭터
        CharacterDto mainCharacter = characters.get(0);

        //3. 숙제 현황
        List<String> sortList = Arrays.asList("에키드나", "카멘", "상아탑", "일리아칸", "카양겔", "아브렐슈드", "쿠크세이튼", "비아키스", "발탄", "아르고스");
        Map<String, List<TodoResponseDto>> todoListGroupedByWeekCategory = characters.stream()
                .flatMap(character -> character.getTodoList().stream())
                .collect(Collectors.groupingBy(TodoResponseDto::getWeekCategory));

        List<HomeRaidDto> homeRaidDtos = sortList.stream()
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

        HomeDto build = HomeDto.builder()
                .homeRaidDtoList(homeRaidDtos)
                .characterDtoList(characters)
                .mainCharacter(mainCharacter)
                .build();

        return new ResponseEntity<>(build, HttpStatus.OK);
    }


}
