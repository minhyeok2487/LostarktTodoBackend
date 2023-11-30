package lostark.todo.controller.apiV2;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lostark.todo.controller.dto.characterDto.CharacterChallengeRequestDto;
import lostark.todo.controller.dto.characterDto.CharacterDefaultDto;
import lostark.todo.controller.dto.characterDto.CharacterDto;
import lostark.todo.controller.dto.friendsDto.FindCharacterWithFriendsDto;
import lostark.todo.controller.dto.friendsDto.FriendSettingRequestDto;
import lostark.todo.controller.dto.friendsDto.FriendsReturnDto;
import lostark.todo.controller.dto.todoDto.TodoDto;
import lostark.todo.domain.character.Character;
import lostark.todo.domain.friends.FriendSettings;
import lostark.todo.domain.friends.Friends;
import lostark.todo.domain.member.Member;
import lostark.todo.service.CharacterService;
import lostark.todo.service.FriendsService;
import lostark.todo.service.MemberService;
import lostark.todo.service.TodoServiceV2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

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

    @ApiOperation(value = "캐릭터 검색")
    @GetMapping("/character/{characterName}")
    public ResponseEntity getCharacterWithFriend(@AuthenticationPrincipal String username,
                                                 @PathVariable String characterName) {
        if(characterName.isEmpty()) {
            throw new IllegalArgumentException("캐릭터명을 입력하여주십시오.");
        }
        Member toMember = memberService.findMember(username);
        List<Character> characterList = characterService.findCharacter(characterName);
        if(!characterList.isEmpty()) {
            List<FindCharacterWithFriendsDto> dtoList = new ArrayList<>();
            for (Character character : characterList) {
                if(toMember != character.getMember()) { //본인 제외
                    Member fromMember = memberService.findMember(character.getMember().getId());
                    String weAreFriend = friendsService.findFriends(toMember,fromMember);
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
            return new ResponseEntity(dtoList, HttpStatus.OK);
        } else {
            throw new IllegalArgumentException(characterName + "은(는) 등록되지 않은 캐릭터명입니다.");
        }
    }

    @ApiOperation(value = "친구 리스트")
    @GetMapping()
    public ResponseEntity getFriends(@AuthenticationPrincipal String username) {
        Member member = memberService.findMember(username);
        List<FriendsReturnDto> friends = friendsService.findFriends(member);
        return new ResponseEntity<>(friends, HttpStatus.OK);
    }

    @ApiOperation(value = "친구 요청")
    @PostMapping("/{fromUser}")
    public ResponseEntity addFriendsRequest(@AuthenticationPrincipal String username,
                                            @PathVariable String fromUser) {
        Member toMember = memberService.findMember(username);
        Member fromMember = memberService.findMember(fromUser);

        friendsService.addFriendsRequest(toMember, fromMember);
        List<FriendsReturnDto> friends = friendsService.findFriends(toMember);
        return new ResponseEntity<>(friends, HttpStatus.OK);
    }

    @ApiOperation(value = "친구 요청 수락/거부/삭제")
    @PatchMapping("/{fromUser}/{category}")
    public ResponseEntity updateFriendsRequest(@AuthenticationPrincipal String username,
                                               @PathVariable("fromUser") String fromUser,
                                               @PathVariable("category") String category) {
        Member toMember = memberService.findMember(username);
        Member fromMember = memberService.findMember(fromUser);

        friendsService.updateFriendsRequest(toMember, fromMember, category);
        List<FriendsReturnDto> friends = friendsService.findFriends(toMember);
        return new ResponseEntity<>(friends, HttpStatus.OK);
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
        Member toMember = memberService.findMember(username);

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

    @ApiOperation(value = "깐부 캐릭터 주간 레이드 check 수정")
    @PatchMapping({"/raid/check", "/raid/check/{all}"})
    public ResponseEntity updateWeekRaidCheck(@AuthenticationPrincipal String username,
                                              @PathVariable(value = "all", required = false) String all,
                                              @RequestBody TodoDto todoDto) {
        Character friendCharacter = characterService.findCharacterById(todoDto.getCharacterId());
        Member fromMember = friendCharacter.getMember();
        Member toMember = memberService.findMember(username);

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
        Member toMember = memberService.findMember(username);

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
        Member toMember = memberService.findMember(username);

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
        Member toMember = memberService.findMember(username);

        boolean weekTodo = friendsService.checkSetting(fromMember, toMember, "weekTodo");

        // cubeTicket 업데이트
        if(weekTodo) {
            characterService.updateCubeTicket(friendCharacter, num);
        }

        return new ResponseEntity(new CharacterDto().toDtoV2(friendCharacter), HttpStatus.OK);
    }

    @ApiOperation(value = "원정대 주간 숙제(도전어비스, 도전가디언) 수정")
    @PatchMapping("/challenge")
    public ResponseEntity updateChallenge(@AuthenticationPrincipal String username,
                                          @RequestBody CharacterChallengeRequestDto characterChallengeRequestDto) {

        Character friendCharacter = characterService.findCharacterById(characterChallengeRequestDto.getCharacterId());
        Member fromMember = friendCharacter.getMember();
        Member toMember = memberService.findMember(username);

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
}
