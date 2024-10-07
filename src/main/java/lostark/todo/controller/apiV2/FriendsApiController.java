package lostark.todo.controller.apiV2;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lostark.todo.controller.dto.characterDto.*;
import lostark.todo.controller.dto.friendsDto.FindCharacterWithFriendsDto;
import lostark.todo.controller.dto.friendsDto.FriendSettingRequestDto;
import lostark.todo.controller.dto.friendsDto.FriendsReturnDto;
import lostark.todo.controller.dto.todoDto.TodoDto;
import lostark.todo.controller.dtoV2.character.CharacterJsonDto;
import lostark.todo.controller.dtoV2.character.CharacterResponse;
import lostark.todo.domainV2.character.entity.Character;
import lostark.todo.domainV2.character.entity.DayTodo;
import lostark.todo.domain.content.Category;
import lostark.todo.domain.content.DayContent;
import lostark.todo.domain.friends.FriendSettings;
import lostark.todo.domain.market.Market;
import lostark.todo.domain.member.Member;
import lostark.todo.domainV2.character.service.CharacterService;
import lostark.todo.domainV2.util.content.service.ContentService;
import lostark.todo.domainV2.util.market.service.MarketService;
import lostark.todo.service.*;
import lostark.todo.domainV2.lostark.dao.LostarkCharacterDao;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.*;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/v2/friends")
@Api(tags = {"친구, 깐부 API"})
public class FriendsApiController {

    private final CharacterService characterService;
    private final MemberService memberService;
    private final FriendsService friendsService;
    private final TodoServiceV2 todoServiceV2;
    private final MarketService marketService;
    private final ContentService contentService;
    private final NotificationService notificationService;
    private final LostarkCharacterDao lostarkCharacterDao;

    @ApiOperation(value = "친구 리스트")
    @GetMapping()
    public ResponseEntity getFriends(@AuthenticationPrincipal String username) {
        Member member = memberService.get(username);
        if (member.getCharacters().isEmpty()) {
            throw new IllegalArgumentException("등록된 캐릭터가 없습니다.");
        }
        List<FriendsReturnDto> friends = friendsService.isFriend(member);
        return new ResponseEntity<>(friends, HttpStatus.OK);
    }

    @ApiOperation(value = "캐릭터 검색")
    @GetMapping("/character/{characterName}")
    public ResponseEntity getCharacterWithFriend(@AuthenticationPrincipal String username,
                                                 @PathVariable String characterName) {
        if(characterName.isEmpty()) {
            throw new IllegalArgumentException("캐릭터명을 입력하여주십시오.");
        }
        Member toMember = memberService.get(username);
        List<Character> characterList = characterService.findCharacter(characterName);
        if(!characterList.isEmpty()) {
            List<FindCharacterWithFriendsDto> dtoList = new ArrayList<>();
            for (Character character : characterList) {
                if(toMember != character.getMember()) { //본인 제외
                    Member fromMember = memberService.get(character.getMember().getId());
                    String weAreFriend = friendsService.isFriend(toMember,fromMember);
                    FindCharacterWithFriendsDto dto = FindCharacterWithFriendsDto.builder()
                            .id(fromMember.getId())
                            .username(fromMember.getUsername())
                            .characterName(characterName)
                            .characterListSize(fromMember.getCharacters().size())
                            .areWeFriend(weAreFriend)
                            .build();
                    dtoList.add(dto);
                }
            }
            return new ResponseEntity<>(dtoList, HttpStatus.OK);
        } else {
            throw new IllegalArgumentException(characterName + "은(는) 등록되지 않은 캐릭터명입니다.");
        }
    }

    @ApiOperation(value = "친구 요청")
    @PostMapping("/{fromUser}")
    public ResponseEntity addFriendsRequest(@AuthenticationPrincipal String username,
                                            @PathVariable String fromUser) {
        Member toMember = memberService.get(username);
        if (toMember.getCharacters().isEmpty()) {
            throw new IllegalArgumentException("등록된 캐릭터가 존재하지 않아 깐부 요청이 불가능합니다.");
        }
        Member fromMember = memberService.get(fromUser);

        friendsService.addFriendsRequest(toMember, fromMember);

        // 보낸 사랑 알림, 받는 사람 알림
        notificationService.saveAddFriendRequest(toMember, fromMember);

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @ApiOperation(value = "친구 요청 수락/거부/삭제")
    @PatchMapping("/{fromUser}/{category}")
    public ResponseEntity updateFriendsRequest(@AuthenticationPrincipal String username,
                                               @PathVariable("fromUser") String fromUser,
                                               @PathVariable("category") String category) {
        Member toMember = memberService.get(username);
        Member fromMember = memberService.get(fromUser);

        friendsService.updateFriendsRequest(toMember, fromMember, category);
        if (category.equals("ok")) {
            notificationService.saveUpdateFriendRequestOk(toMember, fromMember);
        } else if (category.equals("reject")) {
            notificationService.saveUpdateFriendRequestReject(toMember, fromMember);
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @ApiOperation(value = "깐부 권한 수정")
    @PatchMapping("/settings")
    public ResponseEntity updateSettings(@AuthenticationPrincipal String username,
                                         @RequestBody FriendSettingRequestDto friendSettingRequestDto) {

        FriendSettings friendSettings = friendsService.updateSetting(friendSettingRequestDto.getId(),
                friendSettingRequestDto.getName(), friendSettingRequestDto.isValue());

        return new ResponseEntity(friendSettings, HttpStatus.OK);
    }

    @ApiOperation(value = "깐부 캐릭터 일일컨텐츠 체크 업데이트", response = CharacterDto.class)
    @PatchMapping({"/day-content/check/{category}", "/day-content/check/{category}/{all}"})
    public ResponseEntity updateDayTodoCheck(@AuthenticationPrincipal String username,
                                             @PathVariable("category") String category,
                                             @PathVariable(value = "all", required = false) String all,
                                             @RequestBody @Valid CharacterDefaultDto characterDefaultDto) {
        Character friendCharacter = characterService.findCharacterById(characterDefaultDto.getCharacterId());
        Member fromMember = friendCharacter.getMember();
        Member toMember = memberService.get(username);

        boolean dayContent = friendsService.checkSetting(fromMember, toMember, "dayContent");

        if(dayContent) {
            // Check 업데이트
            if (all == null) {
                friendCharacter = characterService.updateCheck(friendCharacter, category);
            } else {
                friendCharacter = characterService.updateCheckAll(friendCharacter, category);
            }
        }
        return ResponseEntity.ok(new CharacterDto().toDtoV2(friendCharacter));
    }

    @ApiOperation(value = "깐부 캐릭터 일일컨텐츠 휴식게이지 업데이트",
            response = CharacterDto.class)
    @PatchMapping("/day-content/gauge")
    public ResponseEntity updateDayTodoGauge(@AuthenticationPrincipal String username,
                                             @RequestBody @Valid CharacterDayTodoDto characterDayTodoDto) {
        Character friendCharacter = characterService.findCharacterById(characterDayTodoDto.getCharacterId());
        Member fromMember = friendCharacter.getMember();
        Member toMember = memberService.get(username);

        boolean dayContent = friendsService.checkSetting(fromMember, toMember, "dayContent");
        if(dayContent) {
            // 재련재료 데이터 리스트로 거래소 데이터 호출
            Map<String, Market> contentResource = marketService.findContentResource();

            // 휴식게이지 업데이트 후 예상 수익 계산
            friendCharacter = characterService.updateGauge(friendCharacter, characterDayTodoDto, contentResource);
        }

        return new ResponseEntity(new CharacterDto().toDtoV2(friendCharacter), HttpStatus.OK);
    }

    @ApiOperation(value = "깐부 캐릭터 주간 레이드 check 수정")
    @PatchMapping({"/raid/check", "/raid/check/{all}"})
    public ResponseEntity updateWeekRaidCheck(@AuthenticationPrincipal String username,
                                              @PathVariable(value = "all", required = false) String all,
                                              @RequestBody TodoDto todoDto) {
        Character friendCharacter = characterService.findCharacterById(todoDto.getCharacterId());
        Member fromMember = friendCharacter.getMember();
        Member toMember = memberService.get(username);

        boolean raid = friendsService.checkSetting(fromMember, toMember, "raid");

        if(raid) {
            if (all == null) {
                todoServiceV2.updateWeekRaidCheck(friendCharacter, todoDto.getWeekCategory(), todoDto.getCurrentGate(), todoDto.getTotalGate());
            } else {
                todoServiceV2.updateWeekRaidCheckAll(friendCharacter, todoDto.getWeekCategory());
            }
        }
        return new ResponseEntity(new CharacterDto().toDtoV2(friendCharacter), HttpStatus.OK);
    }

    @ApiOperation(value = "깐부 캐릭터 주간 에포나 체크",
            notes = "'all'이 붙으면 전체 체크/해제",
            response = CharacterDto.class)
    @PatchMapping({"/epona/{all}","/epona"})
    public ResponseEntity updateWeekTodoEponaCheck(@AuthenticationPrincipal String username,
                                                   @PathVariable(required = false) String all,
                                                   @RequestBody CharacterDto characterDto) {
        Character friendCharacter = characterService.findCharacterById(characterDto.getId());
        Member fromMember = friendCharacter.getMember();
        Member toMember = memberService.get(username);

        boolean weekTodo = friendsService.checkSetting(fromMember, toMember, "weekTodo");

        // all?
        if (weekTodo) {
            if(all != null) {
                if (friendCharacter.getWeekTodo().getWeekEpona() <3) {
                    friendCharacter.getWeekTodo().setWeekEpona(2);
                }
            }
            // Check 업데이트
            characterService.updateWeekEpona(friendCharacter);
        }

        return new ResponseEntity(new CharacterDto().toDtoV2(friendCharacter), HttpStatus.OK);
    }

    @ApiOperation(value = "깐부 캐릭터 실마엘 교환 업데이트", response = CharacterDto.class)
    @PatchMapping("/silmael")
    public ResponseEntity updateSilmael(@AuthenticationPrincipal String username,
                                        @RequestBody CharacterDto characterDto) {
        Character friendCharacter = characterService.findCharacterById(characterDto.getId());
        Member fromMember = friendCharacter.getMember();
        Member toMember = memberService.get(username);

        boolean weekTodo = friendsService.checkSetting(fromMember, toMember, "weekTodo");

        if(weekTodo) {
            // Check 업데이트
            characterService.updateSilmael(friendCharacter);
        }

        return new ResponseEntity(new CharacterDto().toDtoV2(friendCharacter), HttpStatus.OK);
    }

    @ApiOperation(value = "깐부 캐릭터 큐브 티켓 업데이트", response = CharacterDto.class)
    @PatchMapping("/cube/{state}")
    public ResponseEntity updateCubeTicket(@AuthenticationPrincipal String username,
                                           @RequestBody CharacterDto characterDto,
                                           @PathVariable String state) {
        int num = 0;
        if (state.equals("add")) {
            num = 1;
        } else if (state.equals("substract")) {
            num = -1;
        } else {
            throw new IllegalArgumentException("없는 메소드 입니다.");
        }

        Character friendCharacter = characterService.findCharacterById(characterDto.getId());
        Member fromMember = friendCharacter.getMember();
        Member toMember = memberService.get(username);

        boolean weekTodo = friendsService.checkSetting(fromMember, toMember, "weekTodo");

        // cubeTicket 업데이트
        if(weekTodo) {
            characterService.updateCubeTicket(friendCharacter, num);
        }

        return new ResponseEntity(new CharacterDto().toDtoV2(friendCharacter), HttpStatus.OK);
    }

    @ApiOperation(value = "깐부 원정대 주간 숙제(도전어비스, 도전가디언) 수정")
    @PatchMapping("/challenge")
    public ResponseEntity updateChallenge(@AuthenticationPrincipal String username,
                                          @RequestBody CharacterChallengeRequestDto characterChallengeRequestDto) {

        Character friendCharacter = characterService.findCharacterById(characterChallengeRequestDto.getCharacterId());
        Member fromMember = friendCharacter.getMember();
        Member toMember = memberService.get(username);

        boolean weekTodo = friendsService.checkSetting(fromMember, toMember, "weekTodo");

        // 도전 어비스, 가디언 업데이트
        List<Character> characterList = fromMember.getCharacters();
        if(weekTodo) {
            characterList = characterService.updateChallenge(
                    fromMember, characterChallengeRequestDto.getServerName(), characterChallengeRequestDto.getContent());
        } else {
            throw new IllegalArgumentException("권한이 없습니다.");
        }


        return new ResponseEntity<>(new CharacterDto().toDtoList(characterList), HttpStatus.OK);
    }

    @ApiOperation(value = "깐부의 캐릭터 리스트 순서변경 저장", response = CharacterDto.class)
    @PatchMapping("/characterList/sorting/{fromUser}")
    public ResponseEntity updateSort(@AuthenticationPrincipal String username,
                                     @PathVariable("fromUser") String fromUser,
                                     @RequestBody @Valid List<CharacterSortDto> characterSortDtoList) {
        Member toMember = memberService.get(username);
        Member fromMember = memberService.get(fromUser);
        boolean setting = friendsService.checkSetting(fromMember, toMember, "setting");

        if(setting) {
            List<Character> characterList = memberService.editSort(fromUser, characterSortDtoList);
            List<CharacterResponse> responseList = characterList.stream()
                    .map(CharacterResponse::toDto)
                    .sorted(Comparator
                            .comparingInt(CharacterResponse::getSortNumber)
                            .thenComparing(Comparator.comparingDouble(CharacterResponse::getItemLevel).reversed()))
                    .toList();
            return new ResponseEntity<>(responseList, HttpStatus.OK);
        } else {
            throw new IllegalArgumentException("권한이 없습니다.");
        }
    }

    //TODO : 삭제 예정
    @ApiOperation(value = "깐부 캐릭터 리스트 업데이트")
    @PatchMapping("/characterList")
    public ResponseEntity updateCharacterList(@AuthenticationPrincipal String username,
                                              @RequestBody FriendsReturnDto friendsReturnDto) {
        Member toMember = memberService.get(username);
        Member member = memberService.get(friendsReturnDto.getFriendUsername());
        boolean setting = friendsService.checkSetting(member, toMember, "setting");

        if(setting) {
            String mainCharacter = member.getMainCharacterName() != null ? member.getMainCharacterName() :
                    member.getCharacters().get(0).getCharacterName();
            Map<String, Market> contentResource = marketService.findContentResource();
            List<DayContent> chaos = contentService.findDayContent(Category.카오스던전);
            List<DayContent> guardian = contentService.findDayContent(Category.가디언토벌);

            List<CharacterJsonDto> characterJsonDtoList = lostarkCharacterDao.getSiblings(mainCharacter, member.getApiKey());
            for (CharacterJsonDto dto : characterJsonDtoList) {
                dto.setCharacterImage(lostarkCharacterDao.getCharacterImageUrl(dto.getCharacterName(), member.getApiKey()));

                Optional<Character> find = member.getCharacters().stream()
                        .filter(character -> character.getCharacterName().equals(dto.getCharacterName())).findFirst();

                DayTodo dayContent = new DayTodo().createDayContent(chaos, guardian, dto.getItemMaxLevel());

                if (find.isPresent()) { // 이름 같은게 있으면 업데이트
                    Character character = find.get();
                    characterService.updateCharacter(character, dto, dayContent, contentResource);
                } else { // 이름 같은게 없으면 추가
                    Character character = characterService.addCharacter(dto, dayContent, member);
                    member.getCharacters().add(character);
                }
            }
            return new ResponseEntity<>(HttpStatus.OK);
        } else {
            throw new IllegalArgumentException("권한이 없습니다.");
        }
    }
}
