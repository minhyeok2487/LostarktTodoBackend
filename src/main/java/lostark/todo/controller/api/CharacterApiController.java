package lostark.todo.controller.api;

import io.swagger.annotations.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lostark.todo.controller.dto.characterDto.*;
import lostark.todo.controller.dto.contentDto.WeekContentDto;
import lostark.todo.controller.dto.todoDto.TodoDto;
import lostark.todo.controller.dto.todoDto.TodoResponseDto;
import lostark.todo.domain.character.Character;
import lostark.todo.domain.character.DayTodo;
import lostark.todo.domain.content.DayContent;
import lostark.todo.domain.content.WeekContent;
import lostark.todo.domain.market.Market;
import lostark.todo.domain.member.Member;
import lostark.todo.domain.todo.Todo;
import lostark.todo.domain.todo.TodoContentName;
import lostark.todo.service.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/character")
@Api(tags = {"캐릭터 API"})
public class CharacterApiController {

    private final CharacterService characterService;
    private final TodoService todoService;
    private final ContentService contentService;
    private final MarketService marketService;
    private final MemberService memberService;

    @ApiOperation(value = "캐릭터 일일컨텐츠 체크 업데이트",
            response = CharacterDayTodoDto.class)
    @PatchMapping("/check")
    public ResponseEntity updateDayTodoCheck(@AuthenticationPrincipal String username,
                                      @RequestBody @Valid CharacterDayTodoDto characterDayTodoDto) {
        // 로그인한 아이디에 등록된 캐릭터인지 검증
        // 다른 아이디면 자동으로 Exception 처리
        Character character = characterService.findCharacterWithMember(characterDayTodoDto.getCharacterName(), username);

        // Check 업데이트
        DayTodo updated = characterService.updateCheck(character, characterDayTodoDto);

        CharacterDayTodoDto responseDto = CharacterDayTodoDto.builder()
                .characterName(character.getCharacterName())
                .eponaCheck(updated.isEponaCheck())
                .chaosCheck(updated.getChaosCheck())
                .guardianCheck(updated.getGuardianCheck())
                .build();
        return new ResponseEntity(responseDto, HttpStatus.OK);
    }

    @ApiOperation(value = "캐릭터 일일컨텐츠 휴식게이지 업데이트",
            response = CharacterResponseDto.class)
    @PatchMapping("/gauge")
    public ResponseEntity updateDayTodoGauge(@AuthenticationPrincipal String username,
                                          @RequestBody @Valid CharacterDayTodoDto characterDayTodoDto) {
        // 로그인한 아이디에 등록된 캐릭터인지 검증
        // 다른 아이디면 자동으로 Exception 처리
        Character character = characterService.findCharacterWithMember(characterDayTodoDto.getCharacterName(), username);

        // 휴식게이지 업데이트
        Character updateCharacter = characterService.updateGauge(character, characterDayTodoDto);

        // 재련재료 데이터 리스트로 거래소 데이터 호출
        Map<String, Market> contentResource = marketService.findContentResource();

        // 업데이트된 휴식게이지로 예상 수익 계산
        Character resultCharacter = characterService.calculateDayTodo(updateCharacter, contentResource);

        CharacterResponseDto responseDto = CharacterResponseDto.builder()
                .characterName(resultCharacter.getCharacterName())
                .chaosGauge(resultCharacter.getDayTodo().getChaosGauge())
                .chaosGold(resultCharacter.getDayTodo().getChaosGold())
                .guardianGauge(resultCharacter.getDayTodo().getChaosGauge())
                .guardianGold(resultCharacter.getDayTodo().getGuardianGold())
                .build();
        return new ResponseEntity(responseDto, HttpStatus.OK);
    }

    @ApiOperation(value = "캐릭터 일일컨텐츠 통계보기")
    @GetMapping("/day-todo/{characterName}/{category}")
    public ResponseEntity getDayTodoCheck(@AuthenticationPrincipal String username
            , @PathVariable("characterName") String characterName, @PathVariable("category") String category) {
        Character character = characterService.findCharacterWithMember(characterName, username);
        String name = "";
        if (category.equals("카오스던전")) {
            name = character.getDayTodo().getChaosName();
        }
        if (category.equals("가디언토벌")) {
            name = character.getDayTodo().getGuardianName();
        }
        DayContent content = contentService.findDayContentByName(name);
        return new ResponseEntity(content, HttpStatus.OK);
    }

    @ApiOperation(value = "캐릭터 주간 숙제 추가폼",
            response = TodoContentName.class)
    @GetMapping("/week/{characterName}")
    public ResponseEntity todoForm(@AuthenticationPrincipal String username,
                                   @PathVariable("characterName") String characterName) {
        // 로그인한 아이디에 등록된 캐릭터인지 검증
        // 다른 아이디면 자동으로 Exception 처리
        Character character = characterService.findCharacterWithMember(characterName, username);

        // 아이템 레벨보다 작은 컨텐츠 불러옴
        List<WeekContent> allByWeekContent = contentService.findAllByWeekContentWithItemLevel(character.getItemLevel());

        // 임시 id 71이상
        List<WeekContent> collect = allByWeekContent.stream()
                .filter(weekContent -> weekContent.getId() >= 71)
                .collect(Collectors.toList());

        List<WeekContentDto> result = new ArrayList<>();
        for (WeekContent weekContent : collect) {
            WeekContentDto weekContentDto = WeekContentDto.builder()
                    .weekCategory(weekContent.getWeekCategory())
                    .level(weekContent.getLevel())
                    .checked(false)
                    .gate(weekContent.getGate())
                    .gold(weekContent.getGold())
                    .name(weekContent.getName())
                    .build();
            for (Todo todo : character.getTodoList()) {
                if (todo.getName().equals(weekContentDto.getName())) {
                    weekContentDto.setChecked(true);
                }
            }
            result.add(weekContentDto);
        }
        return new ResponseEntity(result, HttpStatus.OK);
    }
    @ApiOperation(value = "캐릭터 주간 숙제 추가/제거")
    @PostMapping("/week/{characterName}")
    public ResponseEntity updateTodo_V2(@AuthenticationPrincipal String username,
                                        @PathVariable("characterName") String characterName,
                                     @RequestBody WeekContentDto weekContentDto) {
        // 로그인한 아이디에 등록된 캐릭터인지 검증
        // 다른 아이디면 자동으로 Exception 처리
        Character character = characterService.findCharacterWithMember(characterName, username);

        List<Todo> todoList = todoService.updateWeek_V2(character, weekContentDto);

        CharacterResponseDto responseDto = new CharacterResponseDto().toDto(character);

        return new ResponseEntity(responseDto, HttpStatus.OK);
    }


    @ApiOperation(value = "캐릭터 주간 숙제 check 수정",
            response = TodoResponseDto.class)
    @PatchMapping("/week/check")
    public ResponseEntity updateWeekCheck(@AuthenticationPrincipal String username,
                                  @RequestBody TodoDto todoDto) {
        // 로그인한 아이디에 등록된 캐릭터인지 검증
        // 다른 아이디면 자동으로 Exception 처리
        Character character = characterService.findCharacterWithMember(todoDto.getCharacterName(), username);

        Todo todo = todoService.updateWeekCheck(todoDto);
        TodoResponseDto todoResponseDto = TodoResponseDto.builder()
                .id(todo.getId())
                .check(todo.isChecked())
                .build();
        return new ResponseEntity(todoResponseDto, HttpStatus.OK);
    }

    @ApiOperation(value = "골드 획득 캐릭터 지정")
    @PostMapping("/gold-character")
    public ResponseEntity updateGoldCharacter(@AuthenticationPrincipal String username, @RequestBody Character character) {
        // 로그인한 아이디에 등록된 캐릭터인지 검증
        // 다른 아이디면 자동으로 Exception 처리
        Character checkedCharacter = characterService.findCharacterWithMember(character.getCharacterName(), username);

        // 골드 획득 지정캐릭터 : 6캐릭 이상인지 확인
        int goldCharacter = characterService.checkGoldCharacter(checkedCharacter);
        //골드획득 지정 캐릭터가 아닌데 6개가 넘으면
        if (!checkedCharacter.isGoldCharacter() && goldCharacter >= 6) {
            throw new IllegalArgumentException("골드 획득 지정 캐릭터는 6캐릭까지 가능합니다.");
        }

        Character resultCharacter = characterService.updateGoldCharacter(checkedCharacter);

        CharacterResponseDto responseDto = CharacterResponseDto.builder()
                .characterName(resultCharacter.getCharacterName())
                .goldCharacter(resultCharacter.isGoldCharacter())
                .build();
        return new ResponseEntity<>(responseDto, HttpStatus.OK);
    }

    @ApiOperation(value = "캐릭터 주간 숙제 message 수정",
            response = TodoResponseDto.class)
    @PatchMapping("/week/message")
    public ResponseEntity updateWeekMessage(@AuthenticationPrincipal String username,
                                          @RequestBody TodoDto todoDto) {
        System.out.println("CharacterApiController.updateWeekMessage");
        System.out.println("todoDto = " + todoDto.toString());
        // 로그인한 아이디에 등록된 캐릭터인지 검증
        // 다른 아이디면 자동으로 Exception 처리
        Character character = characterService.findCharacterWithMember(todoDto.getCharacterName(), username);

        Todo todo = todoService.updateWeekMessage(todoDto);
        TodoResponseDto todoResponseDto = TodoResponseDto.builder()
                .id(todo.getId())
                .message(todo.getMessage())
                .build();
        return new ResponseEntity(todoResponseDto, HttpStatus.OK);
    }

    @ApiOperation(value = "원정대 주간 숙제(도전어비스, 도전가디언) 수정")
    @PatchMapping("/challenge")
    public ResponseEntity updateChallenge(@AuthenticationPrincipal String username,
                                            @RequestBody CharacterChallengeRequestDto dto) {
        // username -> member 조회
        System.out.println("dto = " + dto);
        Member member = memberService.findMember(username);
        List<Character> characterList = characterService.updateChallenge(member, dto.getServerName(), dto.getContent());
        // 결과
        List<CharacterResponseDto> characterResponseDtoList = characterList.stream()
                .map(character -> new CharacterResponseDto().toDto(character))
                .collect(Collectors.toList());

        // characterResponseDtoList를 character.getSortnumber 오름차순으로 정렬
        characterResponseDtoList.sort(Comparator.comparingInt(CharacterResponseDto::getSortNumber));
        return new ResponseEntity<>(characterResponseDtoList, HttpStatus.OK);
    }

    @PatchMapping("/settings")
    public ResponseEntity updateSettings(@AuthenticationPrincipal String username, @RequestBody SettingRequestDto dto) {
        Character character = characterService.findCharacterWithMember(dto.getCharacterName(), username);
        characterService.updateSetting(character, dto.getName(), dto.isValue());
        return new ResponseEntity(CharacterSettingDto.toDto(character), HttpStatus.OK);
    }
}
