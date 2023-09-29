package lostark.todo.controller.api;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lostark.todo.controller.dto.characterDto.CharacterResponseDto;
import lostark.todo.controller.dto.characterDto.CharacterCheckDto;
import lostark.todo.controller.dto.characterDto.CharacterSettingDto;
import lostark.todo.controller.dto.characterDto.CharacterSortDto;
import lostark.todo.controller.dto.memberDto.MemberRequestDto;
import lostark.todo.controller.dto.memberDto.MemberResponseDto;
import lostark.todo.domain.Role;
import lostark.todo.domain.character.Character;
import lostark.todo.domain.character.Settings;
import lostark.todo.domain.content.Category;
import lostark.todo.domain.content.DayContent;
import lostark.todo.domain.market.Market;
import lostark.todo.domain.member.Member;
import lostark.todo.service.CharacterService;
import lostark.todo.service.ContentService;
import lostark.todo.service.MarketService;
import lostark.todo.service.MemberService;
import lostark.todo.service.lostarkApi.LostarkCharacterService;
import org.springframework.data.repository.query.Param;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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

    @GetMapping()
    public ResponseEntity findMember(@AuthenticationPrincipal String username) {
        Member member = memberService.findMember(username);
        MemberResponseDto memberResponseDto = MemberResponseDto.builder()
                .username(username)
                .role(member.getRole())
                .build();
        if (member.getRole().equals(Role.ADMIN)) {
            memberResponseDto.setUsername("관리자");
        }

        return new ResponseEntity(memberResponseDto, HttpStatus.OK);
    }

    @ApiOperation(value = "회원가입시 캐릭터 추가",
            notes="대표캐릭터 검색을 통한 로스트아크 api 검증 \n 대표캐릭터와 연동된 캐릭터 함께 저장",
            response = MemberResponseDto.class)
    @PostMapping("/signup")
    public ResponseEntity signupCharacterV2(@AuthenticationPrincipal String username, @RequestBody @Valid MemberRequestDto memberDto) {
        // 일일 컨텐츠 통계(카오스던전, 가디언토벌) 호출
        List<DayContent> chaos = contentService.findDayContent(Category.카오스던전);
        List<DayContent> guardian = contentService.findDayContent(Category.가디언토벌);

        // 대표캐릭터와 연동된 캐릭터 호출(api 검증)
        List<Character> characterList = lostarkCharacterService.findCharacterList(memberDto.getCharacterName(), memberDto.getApiKey(), chaos, guardian);

        // 재련재료 데이터 리스트로 거래소 데이터 호출
        Map<String, Market> contentResource = marketService.findContentResource();

        // 일일숙제 예상 수익 계산(휴식 게이지 포함)
        List<Character> calculatedCharacterList = new ArrayList<>();
        for (Character character : characterList) {
            Character result = characterService.calculateDayTodo(character, contentResource);
            calculatedCharacterList.add(result);
        }

        // Member 회원가입
        Member signupMember = memberService.createCharacter(username, memberDto.getApiKey(), calculatedCharacterList);

        // 결과 출력
        MemberResponseDto responseDto = MemberResponseDto.builder()
                .id(signupMember.getId())
                .username(signupMember.getUsername())
                .characters(signupMember.getCharacters())
                .build();
        return new ResponseEntity(responseDto, HttpStatus.OK);
    }

    @ApiOperation(value = "회원 캐릭터 리스트 조회",
            response = CharacterResponseDto.class)
    @GetMapping("/characterList")
    public ResponseEntity getCharacterList(@AuthenticationPrincipal String username) {
        // username -> member 조회
        Member member = memberService.findMember(username);
        if(member.getCharacters().isEmpty()) {
            throw new IllegalArgumentException("등록된 캐릭터가 없습니다.");
        }
        // 결과
        List<CharacterResponseDto> characterResponseDtoList = member.getCharacters().stream()
                .filter(character -> character.getSettings().isShowCharacter())
                .map(character -> new CharacterResponseDto().createResponseDto(character))
                .collect(Collectors.toList());

        // characterResponseDtoList를 character.getSortnumber 오름차순으로 정렬
        characterResponseDtoList.sort(Comparator.comparingInt(CharacterResponseDto::getSortNumber));
        return new ResponseEntity<>(characterResponseDtoList, HttpStatus.OK);
    }

    @GetMapping("/characterList/{serverName}")
    public ResponseEntity findCharacterListServerName(@AuthenticationPrincipal String username, @PathVariable("serverName") String serverName) {
        // username -> member 조회
        Member member = memberService.findMember(username);
        if(member.getCharacters().isEmpty()) {
            throw new IllegalArgumentException("등록된 캐릭터가 없습니다.");
        }
        List<Character> characterList = characterService.findCharacterListServerName(member, serverName);
        // 결과
        List<CharacterResponseDto> characterResponseDtoList = characterList.stream()
                .filter(character -> character.getSettings().isShowCharacter())
                .map(character -> new CharacterResponseDto().createResponseDto(character))
                .collect(Collectors.toList());

        // characterResponseDtoList를 character.getSortnumber 오름차순으로 정렬
        characterResponseDtoList.sort(Comparator.comparingInt(CharacterResponseDto::getSortNumber));
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
        List<Character> beforeCharacterList = member.getCharacters();
        // 대표캐릭터와 연동된 캐릭터(api 검증)
        // 일일 컨텐츠 통계(카오스던전, 가디언토벌) 호출
        List<DayContent> chaos = contentService.findDayContent(Category.카오스던전);
        List<DayContent> guardian = contentService.findDayContent(Category.가디언토벌);

        // 대표캐릭터와 연동된 캐릭터 호출(api 검증)
        List<Character> updateCharacterList = lostarkCharacterService.findCharacterList(
                beforeCharacterList.get(0).getCharacterName(), member.getApiKey(), chaos, guardian);

        // 변경된 내용 업데이트 및 추가, 삭제
        List<Character> updatedCharacterList = memberService.updateCharacterList(beforeCharacterList, updateCharacterList);

        // 재련재료 데이터 리스트로 거래소 데이터 호출
        Map<String, Market> contentResource = marketService.findContentResource();

        // 일일숙제 예상 수익 계산(휴식 게이지 포함)
        List<Character> calculatedCharacterList = new ArrayList<>();
        for (Character character : updatedCharacterList) {
            Character result = characterService.calculateDayTodo(character, contentResource);
            calculatedCharacterList.add(result);
        }

        // 결과
        List<CharacterResponseDto> characterResponseDtoList = calculatedCharacterList.stream()
                .map(character -> new CharacterResponseDto().createResponseDto(character))
                .collect(Collectors.toList());

        // characterResponseDtoList를 character.getSortnumber 오름차순으로 정렬
        characterResponseDtoList.sort(Comparator.comparingInt(CharacterResponseDto::getSortNumber));

        return new ResponseEntity<>(characterResponseDtoList, HttpStatus.OK);
    }

    @ApiOperation(value = "회원과 연결된 캐릭터 리스트의 변경된 Todo항목 저장", response = CharacterCheckDto.class)
    @PatchMapping("/characterList/todo")
    public ResponseEntity updateTodo(@AuthenticationPrincipal String username,
                                     @RequestBody @Valid List<CharacterCheckDto> characterCheckDtoList) {
        return new ResponseEntity<>(memberService.updateTodo(username, characterCheckDtoList), HttpStatus.OK);
    }

    @ApiOperation(value = "회원과 연결된 캐릭터 리스트 순서변경 저장", response = CharacterResponseDto.class)
    @PatchMapping("/characterList/sorting")
    public ResponseEntity updateSort(@AuthenticationPrincipal String username,
                                     @RequestBody @Valid List<CharacterSortDto> characterSortDtoList) {
        Member member = memberService.updateSort(username, characterSortDtoList);

        List<CharacterResponseDto> characterResponseDtoList = new ArrayList<>();
        for (Character character : member.getCharacters()) {
            // Character -> CharacterResponseDto 변경
            CharacterResponseDto characterResponseDto = new CharacterResponseDto().createResponseDto(character);
            characterResponseDtoList.add(characterResponseDto);
        }
        return new ResponseEntity<>(characterResponseDtoList, HttpStatus.OK);
    }


    @GetMapping("/characterList/server")
    public ResponseEntity findGroupServerNameCount(@AuthenticationPrincipal String username) {
        Member member = memberService.findMember(username);
        if(member.getCharacters().isEmpty()) {
            throw new IllegalArgumentException("등록된 캐릭터가 없습니다.");
        }
        Map<String, Long> groupServerNameCount = characterService.findGroupServerNameCount(member);
        return new ResponseEntity(groupServerNameCount, HttpStatus.OK);
    }

    @GetMapping("/settings")
    public ResponseEntity findSettings(@AuthenticationPrincipal String username) {
        Member member = memberService.findMember(username);
        member.getCharacters().sort(Comparator.comparingInt(Character::getSortNumber));

        List<CharacterSettingDto> settingsList = new ArrayList<>();
        for (Character character : member.getCharacters()) {
            settingsList.add(CharacterSettingDto.toDto(character));
        }
        return new ResponseEntity(settingsList, HttpStatus.OK);
    }
}
