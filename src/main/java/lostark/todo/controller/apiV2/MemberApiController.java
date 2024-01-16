package lostark.todo.controller.apiV2;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lostark.todo.controller.dto.characterDto.CharacterDto;
import lostark.todo.controller.dto.characterDto.CharacterCheckDto;
import lostark.todo.controller.dto.characterDto.CharacterSettingDto;
import lostark.todo.controller.dto.characterDto.CharacterSortDto;
import lostark.todo.controller.dto.memberDto.MemberRequestDto;
import lostark.todo.controller.dto.memberDto.MemberResponseDto;
import lostark.todo.domain.Role;
import lostark.todo.domain.character.Character;
import lostark.todo.domain.character.DayTodo;
import lostark.todo.domain.content.Category;
import lostark.todo.domain.content.DayContent;
import lostark.todo.domain.logs.Logs;
import lostark.todo.domain.market.Market;
import lostark.todo.domain.member.Member;
import lostark.todo.service.*;
import lostark.todo.service.lostarkApi.LostarkApiService;
import lostark.todo.service.lostarkApi.LostarkCharacterService;
import org.json.simple.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
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
    private final LostarkApiService lostarkApiService;
    private final LogsService logsService;
    private final ConcurrentHashMap<String, Boolean> usernameLocks;

//    @GetMapping()
//    public ResponseEntity findMember(@AuthenticationPrincipal String username) {
//        Member member = memberService.findMember(username);
//        MemberResponseDto memberResponseDto = new MemberResponseDto().toDto(member);
//        if (member.getRole().equals(Role.ADMIN)) {
//            memberResponseDto.setUsername("관리자");
//        }
//        return new ResponseEntity(memberResponseDto, HttpStatus.OK);
//    }


//    @ApiOperation(value = "회원가입시 캐릭터 추가",
//            notes="대표캐릭터 검색을 통한 로스트아크 api 검증 \n 대표캐릭터와 연동된 캐릭터 함께 저장",
//            response = MemberResponseDto.class)
//    @PostMapping("/signup")
//    public ResponseEntity saveCharacter(@AuthenticationPrincipal String username, @RequestBody @Valid MemberRequestDto memberDto) {
//        if (usernameLocks.putIfAbsent(username, true) != null) {
//            throw new IllegalStateException("이미 진행중입니다.");
//        }
//        try {
//            // 일일 컨텐츠 통계(카오스던전, 가디언토벌) 호출
//            List<DayContent> chaos = contentService.findDayContent(Category.카오스던전);
//            List<DayContent> guardian = contentService.findDayContent(Category.가디언토벌);
//
//            // 대표캐릭터와 연동된 캐릭터 호출(api 검증)
//            List<Character> characterList = lostarkCharacterService.findCharacterList(memberDto.getCharacterName(), memberDto.getApiKey(), chaos, guardian);
//
//            // 재련재료 데이터 리스트로 거래소 데이터 호출
//            Map<String, Market> contentResource = marketService.findContentResource();
//
//            // 일일숙제 예상 수익 계산(휴식 게이지 포함)
//            List<Character> calculatedCharacterList = new ArrayList<>();
//            for (Character character : characterList) {
//                Character result = characterService.calculateDayTodo(character, contentResource);
//                calculatedCharacterList.add(result);
//            }
//
//            // Member 회원가입
//            Member signupMember = memberService.createCharacter(username, memberDto.getApiKey(), calculatedCharacterList);
//
//            // 결과 출력
//            MemberResponseDto memberResponseDto = new MemberResponseDto().toDto(signupMember);
//            return new ResponseEntity(memberResponseDto, HttpStatus.OK);
//        } finally {
//            usernameLocks.remove(username);
//        }
//    }

//    @ApiOperation(value = "캐릭터 서버 리스트 불러오기")
//    @GetMapping("/characterList/server")
//    public ResponseEntity<Map<String, Long>> findGroupServerNameCount(@AuthenticationPrincipal String username) {
//        return new ResponseEntity<>(characterService.findGroupServerNameCount(username), HttpStatus.OK);
//    }
//
//    @ApiOperation(value = "회원 캐릭터 리스트 조회 - 서버별 분리",
//            response = CharacterDto.class)
//    @GetMapping("/characterList-v3/{serverName}")
//    public ResponseEntity findCharacterListServerNameV3(@AuthenticationPrincipal String username, @PathVariable("serverName") String serverName) {
//
//        //username, serverName -> characterList 조회
//        List<Character> characterList = characterService.findCharacterListServerName(username, serverName);
//
//        // 결과
//        List<CharacterDto> characterDtoList = characterList.stream()
//                .filter(character -> character.getSettings().isShowCharacter())
//                .map(character -> new CharacterDto().toDtoV2(character)).sorted(Comparator
//                        .comparingInt(CharacterDto::getSortNumber)
//                        .thenComparing(Comparator.comparingDouble(CharacterDto::getItemLevel).reversed())).collect(Collectors.toList());
//        Logs build = Logs.builder()
//                .memberId(characterList.get(0).getMember().getId())
//                .message(username+" 접속")
//                .build();
//        logsService.save(build);
//        return new ResponseEntity<>(characterDtoList, HttpStatus.OK);
//    }

    @ApiOperation(value = "회원 캐릭터 리스트 업데이트",
        notes="전투 레벨, 아이템 레벨, 이미지url 업데이트 \n" +
                "캐릭터 아이템 레벨이 달라지면 예상 수익골드 다시 계산 \n" +
                "캐릭터 추가 및 삭제 ",
        response = CharacterDto.class)
    @PatchMapping("/characterList")
    public ResponseEntity updateCharacterList(@AuthenticationPrincipal String username) {
        Member member = memberService.findMember(username);
        List<DayContent> chaos = contentService.findDayContent(Category.카오스던전);
        List<DayContent> guardian = contentService.findDayContent(Category.가디언토벌);

        List<Character> removeList = new ArrayList<>();
        // 비교 : 캐릭터 이름, 아이템레벨, 클래스
        for (Character character : member.getCharacters()) {
            JSONObject jsonObject = lostarkCharacterService.findCharacter(character.getCharacterName(), member.getApiKey());
            if (jsonObject == null) {
                log.info("delete character name : {}", character.getCharacterName());
                //삭제 리스트에 추가
                removeList.add(character);
            } else {
                double itemMaxLevel = Double.parseDouble(jsonObject.get("ItemMaxLevel").toString().replace(",", ""));

                // 데이터 변경
                if (itemMaxLevel >= 1415.0) {
                    Character newCharacter = Character.builder()
                            .characterName(jsonObject.get("CharacterName") != null ? jsonObject.get("CharacterName").toString() : null)
                            .characterClassName(jsonObject.get("CharacterClassName") != null ? jsonObject.get("CharacterClassName").toString() : null)
                            .characterImage(jsonObject.get("CharacterImage") != null ? jsonObject.get("CharacterImage").toString() : null)
                            .characterLevel(Integer.parseInt(jsonObject.get("CharacterLevel").toString()))
                            .itemLevel(itemMaxLevel)
                            .dayTodo(new DayTodo().createDayContent(chaos, guardian, itemMaxLevel))
                            .build();
                    characterService.updateCharacter(character, newCharacter);
                } else {
                    //삭제 리스트에 추가
                    removeList.add(character);
                }
            }
        }

        // 삭제
        if (!removeList.isEmpty()) {
            for (Character character : removeList) {
                characterService.deleteCharacter(member.getCharacters(), character);
            }
        }

        // 추가 리스트
        List<Character> addList = new ArrayList<>();
        List<Character> updateCharacterList = lostarkCharacterService.findCharacterList(
                member.getCharacters().get(0).getCharacterName(), member.getApiKey(), chaos, guardian);
        for (Character character : updateCharacterList) {
            boolean contain = false;
            for (Character before : member.getCharacters()) {
                if (before.getCharacterName().equals(character.getCharacterName())) {
                    contain = true;
                    break;
                }
            }
            if (!contain) {
                addList.add(character);
            }
        }

        //추가 하면서 캐릭터 닉네임 변경감지
        if (!addList.isEmpty()) {
            characterService.addCharacterList(addList, removeList, member);
        }

        // 재련재료 데이터 리스트로 거래소 데이터 호출
        Map<String, Market> contentResource = marketService.findContentResource();

        // 일일숙제 예상 수익 계산(휴식 게이지 포함)
        List<Character> calculatedCharacterList = new ArrayList<>();
        for (Character character : member.getCharacters()) {
            Character result = characterService.calculateDayTodo(character, contentResource);
            calculatedCharacterList.add(result);
        }

        // 결과
        List<CharacterDto> characterDtoList = calculatedCharacterList.stream()
                .filter(character -> character.getSettings().isShowCharacter())
                .map(character -> new CharacterDto().toDtoV2(character)).sorted(Comparator
                        .comparingInt(CharacterDto::getSortNumber)
                        .thenComparing(Comparator.comparingDouble(CharacterDto::getItemLevel).reversed())).collect(Collectors.toList());


        return new ResponseEntity<>(characterDtoList, HttpStatus.OK);
    }


    @ApiOperation(value = "회원과 연결된 캐릭터 리스트의 변경된 Todo항목 저장", response = CharacterCheckDto.class)
    @PatchMapping("/characterList/todo")
    public ResponseEntity updateTodo(@AuthenticationPrincipal String username,
                                     @RequestBody @Valid List<CharacterCheckDto> characterCheckDtoList) {
        return new ResponseEntity<>(memberService.updateTodo(username, characterCheckDtoList), HttpStatus.OK);
    }

    @ApiOperation(value = "회원과 연결된 캐릭터 리스트 순서변경 저장", response = CharacterDto.class)
    @PatchMapping("/characterList/sorting")
    public ResponseEntity updateSort(@AuthenticationPrincipal String username,
                                     @RequestBody @Valid List<CharacterSortDto> characterSortDtoList) {
        Member member = memberService.updateSort(username, characterSortDtoList);

        List<CharacterDto> characterDtoList = new ArrayList<>();
        for (Character character : member.getCharacters()) {
            // Character -> CharacterResponseDto 변경
            CharacterDto characterDto = new CharacterDto().toDtoV2(character);
            characterDtoList.add(characterDto);
        }
        return new ResponseEntity<>(characterDtoList, HttpStatus.OK);
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

    @ApiOperation(value = "회원 API KEY 갱신")
    @PatchMapping("/api-key")
    public ResponseEntity updateApiKey(@AuthenticationPrincipal String username,
                                       @RequestBody MemberRequestDto memberRequestDto) {
        // 1. 검증
        Member member = memberService.findMember(username);
        if (memberRequestDto.getApiKey() == null || memberRequestDto.getApiKey().isEmpty()) {
            throw new IllegalArgumentException("API KEY를 입력하여 주십시오");
        }
        if (member.getApiKey() != null && member.getApiKey().equals(memberRequestDto.getApiKey())) {
            throw new IllegalArgumentException("동일한 API KEY입니다.");
        }

        // 2. API KEY 인증 확인
        lostarkApiService.findEvents(memberRequestDto.getApiKey());

        // 3. API KEY 업데이트
        memberService.updateApiKey(member, memberRequestDto.getApiKey());

        return new ResponseEntity(new MemberResponseDto().toDto(member), HttpStatus.OK);
    }

    @ApiOperation(value = "중복 캐릭터 삭제")
    @DeleteMapping("/duplicate")
    public ResponseEntity removeDuplicateCharacters(@AuthenticationPrincipal String username) {
        Member member = memberService.findMember(username);
        memberService.removeDuplicateCharacters(member);
        return new ResponseEntity(new MemberResponseDto().toDto(member), HttpStatus.OK);
    }


    //    @ApiOperation(value = "회원 캐릭터 리스트 업데이트",
//            notes="전투 레벨, 아이템 레벨, 이미지url 업데이트 \n" +
//                    "캐릭터 아이템 레벨이 달라지면 예상 수익골드 다시 계산 \n" +
//                    "캐릭터 추가 및 삭제 ",
//            response = CharacterResponseDto.class)
//    @PatchMapping("/characterList")
//    public ResponseEntity updateCharacterList(@AuthenticationPrincipal String username) {
//        Member member = memberService.findMember(username);
//        List<Character> beforeCharacterList = member.getCharacters();
//        // 대표캐릭터와 연동된 캐릭터(api 검증)
//        // 일일 컨텐츠 통계(카오스던전, 가디언토벌) 호출
//        List<DayContent> chaos = contentService.findDayContent(Category.카오스던전);
//        List<DayContent> guardian = contentService.findDayContent(Category.가디언토벌);
//
//        // 대표캐릭터와 연동된 캐릭터 호출(api 검증)
//        List<Character> updateCharacterList = lostarkCharacterService.findCharacterList(
//                beforeCharacterList.get(0).getCharacterName(), member.getApiKey(), chaos, guardian);
//
//        // 변경된 내용 업데이트 및 추가, 삭제
//        memberService.updateCharacterList(member, updateCharacterList);
//
//        // 재련재료 데이터 리스트로 거래소 데이터 호출
//        Map<String, Market> contentResource = marketService.findContentResource();
//
//        // 일일숙제 예상 수익 계산(휴식 게이지 포함)
//        List<Character> calculatedCharacterList = new ArrayList<>();
//        for (Character character : member.getCharacters()) {
//            Character result = characterService.calculateDayTodo(character, contentResource);
//            calculatedCharacterList.add(result);
//        }
//
//        // 결과
//        List<CharacterResponseDto> characterResponseDtoList = calculatedCharacterList.stream()
//                .map(character -> new CharacterResponseDto().toDto(character))
//                .collect(Collectors.toList());
//
//        // characterResponseDtoList를 character.getSortnumber 오름차순으로 정렬
//        characterResponseDtoList.sort(Comparator
//                .comparingInt(CharacterResponseDto::getSortNumber)
//                .thenComparing(Comparator.comparingDouble(CharacterResponseDto::getItemLevel).reversed())
//        );
//
//        return new ResponseEntity<>(characterResponseDtoList, HttpStatus.OK);
//    }

    //    @ApiOperation(value = "회원 캐릭터 리스트 조회",
//            response = CharacterResponseDto.class)
//    @GetMapping("/characterList")
//    public ResponseEntity getCharacterList(@AuthenticationPrincipal String username) {
//        // username -> member 조회
//        Member member = memberService.findMember(username);
//        if(member.getCharacters().isEmpty()) {
//            throw new IllegalArgumentException("등록된 캐릭터가 없습니다.");
//        }
//        // 결과
//        List<CharacterResponseDto> characterResponseDtoList = member.getCharacters().stream()
//                .filter(character -> character.getSettings().isShowCharacter())
//                .map(character -> new CharacterResponseDto().toDto(character))
//                .collect(Collectors.toList());
//
//        // characterResponseDtoList를 character.getSortnumber 오름차순으로 정렬
//        characterResponseDtoList.sort(Comparator
//                .comparingInt(CharacterResponseDto::getSortNumber)
//                .thenComparing(Comparator.comparingDouble(CharacterResponseDto::getItemLevel).reversed())
//        );
//        return new ResponseEntity<>(characterResponseDtoList, HttpStatus.OK);
//    }

//    @ApiOperation(value = "회원 캐릭터 리스트 조회 - 서버별 분리",
//            response = CharacterDto.class)
//    @GetMapping("/characterList/{serverName}")
//    public ResponseEntity findCharacterListServerName(@AuthenticationPrincipal String username, @PathVariable("serverName") String serverName) {
//        // username -> member 조회
//        Member member = memberService.findMember(username);
//        if(member.getCharacters().isEmpty()) {
//            throw new IllegalArgumentException("등록된 캐릭터가 없습니다.");
//        }
//        List<Character> characterList = characterService.findCharacterListServerName(member, serverName);
//        // 결과
//        List<CharacterDto> characterDtoList = characterList.stream()
//                .filter(character -> character.getSettings().isShowCharacter())
//                .map(character -> new CharacterDto().toDtoV2(character))
//                .collect(Collectors.toList());
//
//        // characterResponseDtoList를 character.getSortnumber 오름차순으로 정렬
//        characterDtoList.sort(Comparator
//                .comparingInt(CharacterDto::getSortNumber)
//                .thenComparing(Comparator.comparingDouble(CharacterDto::getItemLevel).reversed())
//        );
//        return new ResponseEntity<>(characterDtoList, HttpStatus.OK);
//    }
}
