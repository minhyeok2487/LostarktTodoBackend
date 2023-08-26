package lostark.todo.controller.api;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lostark.todo.controller.dto.characterDto.CharacterResponseDto;
import lostark.todo.controller.dto.characterDto.CharacterCheckDto;
import lostark.todo.controller.dto.memberDto.MemberDto;
import lostark.todo.controller.dto.todoDto.TodoResponseDto;
import lostark.todo.domain.character.Character;
import lostark.todo.domain.content.DayContent;
import lostark.todo.domain.market.Market;
import lostark.todo.domain.member.Member;
import lostark.todo.domain.todo.Todo;
import lostark.todo.service.CharacterService;
import lostark.todo.service.ContentService;
import lostark.todo.service.MarketService;
import lostark.todo.service.MemberService;
import lostark.todo.service.lostarkApi.LostarkCharacterService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/member")
@Api(tags = {"회원 API"})
public class MemberApiController {

    private final CharacterService characterService;
    private final MarketService marketService;
    private final ContentService contentService;
    private final MemberService memberService;
    private final LostarkCharacterService lostarkCharacterService;


    @ApiOperation(value = "회원 캐릭터 리스트 조회",
            response = CharacterResponseDto.class)
    @GetMapping("/characterList")
    public ResponseEntity getCharacterList(@AuthenticationPrincipal String username) {
        // username -> member 조회
        Member member = memberService.findMember(username);

        // member -> List<Character> 조회
        List<Character> characterList = characterService.findByMember(member);
        if(characterList.isEmpty()) {
            throw new IllegalArgumentException("등록된 캐릭터가 없습니다.");
        }

        // 결과
        List<CharacterResponseDto> characterResponseDtoList = new ArrayList<>();

        for (Character character : characterList) {
            // Character -> CharacterResponseDto 변경
            CharacterResponseDto characterResponseDto = createResponseDto(character);
            characterResponseDtoList.add(characterResponseDto);
        }

        return new ResponseEntity<>(characterResponseDtoList, HttpStatus.OK);
    }

    @ApiOperation(value = "회원 캐릭터 리스트 업데이트",
            notes="전투 레벨, 아이템 레벨, 이미지url 업데이트 \n" +
                    "캐릭터 아이템 레벨이 달라지면 예상 수익골드 다시 계산 \n" +
                    "캐릭터 추가 및 삭제 ",
            response = CharacterResponseDto.class)
    @PatchMapping("/characterList")
    public ResponseEntity updateCharacterList(@AuthenticationPrincipal String username) {
        Member member = memberService.findMember(username);
        MemberDto memberDto = MemberDto.builder()
                .apiKey(member.getApiKey())
                .characterName(member.getCharacters().get(0).getCharacterName())
                .username(username)
                .build();
        // 대표캐릭터와 연동된 캐릭터(api 검증)
        List<Character> characterList = lostarkCharacterService.getCharacterList(memberDto);

        // 변경된 내용 업데이트 및 추가, 삭제
        List<Character> updateCharacterList = memberService.updateCharacterList(member, characterList);

        // 재련재료 데이터 리스트로 거래소 데이터 호출
        Map<String, Market> contentResource = marketService.getContentResource();

        // 일일 숙제 통계 가져오기
        Map<String, DayContent> dayContent = contentService.findDayContent();

        List<CharacterResponseDto> characterResponseDtoList = new ArrayList<>();

        for (Character character : updateCharacterList) {
            // 일일숙제 예상 수익 계산(휴식 게이지 포함)
            characterService.calculateDayTodo(character, contentResource, dayContent);

            // Character -> CharacterResponseDto
            CharacterResponseDto characterResponseDto = createResponseDto(character);
            characterResponseDtoList.add(characterResponseDto);
        }

        return new ResponseEntity<>(characterResponseDtoList, HttpStatus.OK);
    }

    @ApiOperation(value = "회원과 연결된 캐릭터 리스트의 변경된 Todo항목 저장", response = CharacterCheckDto.class)
    @PatchMapping("/characterList/todo")
    public ResponseEntity updateTodo(@AuthenticationPrincipal String username,
                                     @RequestBody @Valid List<CharacterCheckDto> characterCheckDtoList) {
        return new ResponseEntity<>(memberService.updateTodo(username, characterCheckDtoList), HttpStatus.OK);
    }


    // character 엔티티로 CharacterResponseDto 객체 생성
    private CharacterResponseDto createResponseDto(Character character) {
        List<TodoResponseDto> todoResponseDtoList = new ArrayList<>();

        for (Todo todo : character.getTodoList()) {
            TodoResponseDto todoResponseDto = new TodoResponseDto();
            todoResponseDto.setId(todo.getId());
            todoResponseDto.setCheck(todo.isChecked());
            todoResponseDto.setGold(todo.getGold());
            todoResponseDto.setContentName(todo.getContentName().getDisplayName());
            todoResponseDtoList.add(todoResponseDto);
        }


        CharacterResponseDto characterResponseDto = CharacterResponseDto.builder()
                .id(character.getId())
                .characterName(character.getCharacterName())
                .characterImage(character.getCharacterImage())
                .characterClassName(character.getCharacterClassName())
                .itemLevel(character.getItemLevel())
                .chaosCheck(character.getDayTodo().getChaosCheck())
                .chaosGauge(character.getDayTodo().getChaosGauge())
                .chaosName(character.getDayTodo().getChaosName())
                .chaosGold(character.getDayTodo().getChaosGold())
                .guardianCheck(character.getDayTodo().getGuardianCheck())
                .guardianGauge(character.getDayTodo().getGuardianGauge())
                .guardianName(character.getDayTodo().getGuardianName())
                .guardianGold(character.getDayTodo().getGuardianGold())
                .eponaCheck(character.getDayTodo().isEponaCheck())
                .todoList(todoResponseDtoList)
                .build();
        return characterResponseDto;
    }

//    @ApiOperation(value = "회원과 등록된 캐릭터 리스트 조회",
//            notes="휴식게이지를 참고하여 일일컨텐츠 수익 계산하여 함께 리턴",
//            response = CharacterListResponeDto.class)
//    @GetMapping("/characterList")
//    public ResponseEntity getCharacterList(@AuthenticationPrincipal String username) {
//        try {
//            // username 으로 연결된 캐릭터리스트 호출
//            List<Character> characterList = memberService.findMember(username).getCharacters();
//            if(characterList.isEmpty()) {
//                throw new IllegalArgumentException("등록된 캐릭터가 없습니다.");
//            }
//
//            // 캐릭터 레벨에 맞는 일일 컨텐츠 호출
//            List<CharacterResponseDto> characterResponseDtoList = contentService.getCharacterListWithDayContent(characterList);
//
//            // 재련재료 데이터 리스트로 거래소 데이터 호출
//            Map<String, Market> contentResource = marketService.getContentResource();
//
//            // 캐릭터 리스트와 거래소 데이터를 이용한 계산
//            characterService.calculateProfit(characterResponseDtoList, contentResource);
//
//            // Profit 순서대로 정렬하기
//            List<SortedDayContentProfitDto> sortedDayContentProfit = contentService.sortDayContentProfit(characterResponseDtoList);
//
//            // Profit 합 구하기
//            double sum = 0;
//            for (SortedDayContentProfitDto dto : sortedDayContentProfit) {
//                sum += dto.getProfit();
//            }
//            sum = Math.round(sum * 100.0) / 100.0;
//
//            // 결과 출력
//            CharacterListResponeDto charactersReturnDto = CharacterListResponeDto.builder()
//                    .characters(characterResponseDtoList)
//                    .sumDayContentProfit(sum)
//                    .sortedDayContentProfitDtoList(sortedDayContentProfit)
//                    .build();
//            return new ResponseEntity<>(charactersReturnDto, HttpStatus.OK);
//        } catch (Exception e) {
//            throw new RuntimeException(e.getMessage());
//        }
//    }

}
