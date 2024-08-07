package lostark.todo.controller.apiV4.friends;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lostark.todo.controller.dto.contentDto.WeekContentDto;
import lostark.todo.controller.dtoV2.firend.FriendsResponse;
import lostark.todo.controller.dtoV2.firend.UpdateFriendWeekRaidParams;
import lostark.todo.domain.character.Character;
import lostark.todo.domain.content.WeekContent;
import lostark.todo.domain.friends.Friends;
import lostark.todo.domain.member.Member;
import lostark.todo.domain.todoV2.TodoV2;
import lostark.todo.service.ContentService;
import lostark.todo.service.FriendsService;
import lostark.todo.service.MemberService;
import lostark.todo.service.TodoServiceV2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;


@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/v4/friends")
@Api(tags = {"깐부 리스트"})
public class FriendsControllerV4 {

    private final FriendsService friendsService;
    private final MemberService memberService;
    private final ContentService contentService;
    private final TodoServiceV2 todoServiceV2;

    @ApiOperation(value = "깐부 리스트 조회",
            response = FriendsResponse.class)
    @GetMapping()
    public ResponseEntity<?> get(@AuthenticationPrincipal String username) {
        return new ResponseEntity<>(friendsService.getFriendListV2(memberService.get(username).getId()), HttpStatus.OK);
    }

    @ApiOperation(value = "깐부 삭제")
    @DeleteMapping("/{friendId}")
    public ResponseEntity<?> delete(@AuthenticationPrincipal String username, @PathVariable long friendId) {
        Member member = memberService.get(username);
        friendsService.deleteById(member, friendId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @ApiOperation(value = "깐부 캐릭터 주간 숙제 추가폼")
    @GetMapping("/week/form/{friendUsername}/{characterId}")
    public ResponseEntity<?> getTodoForm(@AuthenticationPrincipal String username,
                                         @PathVariable String friendUsername,
                                         @PathVariable long characterId) {
        // 로그인한 아이디에 등록된 캐릭터인지 검증
        // 다른 아이디면 자동으로 Exception 처리
        Friends friend = friendsService.findByFriendUsername(friendUsername, username);
        if (!friend.getFriendSettings().isSetting()) {
            throw new IllegalArgumentException("권한이 없습니다.");
        }

        Character character = friendsService.findFriendCharacter(friendUsername, characterId);

        // 아이템 레벨보다 작은 컨텐츠 불러옴
        List<WeekContent> allByWeekContent = contentService.findAllWeekContent(character.getItemLevel());

        List<WeekContentDto> result = new ArrayList<>();
        for (WeekContent weekContent : allByWeekContent) {
            WeekContentDto weekContentDto = new WeekContentDto().toDto(weekContent);
            if (!character.getTodoV2List().isEmpty()) {
                for (TodoV2 todo : character.getTodoV2List()) {
                    if (todo.getWeekContent().equals(weekContent)) {
                        weekContentDto.setChecked(true); // 이미 등록된 컨텐츠면 true
                        weekContentDto.setGoldCheck(todo.isGoldCheck());
                        break;
                    }
                }
            }
            result.add(weekContentDto);
        }

        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @ApiOperation(value = "깐부 캐릭터 주간 레이드 추가/제거")
    @PostMapping("/raid")
    public ResponseEntity<?> updateFriendWeekRaid(@AuthenticationPrincipal String username,
                                                  @RequestBody UpdateFriendWeekRaidParams params) {
        Friends friend = friendsService.findByFriendUsername(params.getFriendUsername(), username);

        if (!friend.getFriendSettings().isSetting()) {
            throw new IllegalArgumentException("권한이 없습니다.");
        }

        Character character = friend.getMember().getCharacters().stream()
                .filter(c -> c.getId() == params.getFriendCharacterId())
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("등록되지 않은 캐릭터 입니다."));

        WeekContent weekContent = (WeekContent) contentService.findById(params.getWeekContentId());
        todoServiceV2.updateWeekRaid(character, weekContent);

        return new ResponseEntity<>(HttpStatus.OK);
    }


//    @ApiOperation(value = "깐부 캐릭터 주간 레이드 전체 추가/제거 all")
//    @PostMapping("/raid/{characterId}/{characterName}/all")
//    public ResponseEntity updateWeekRaidAll(@AuthenticationPrincipal String username,
//                                            @PathVariable long characterId,
//                                            @PathVariable("characterName") String characterName,
//                                            @RequestBody List<WeekContentDto> weekContentDtoList) {
//        // 로그인한 아이디에 등록된 캐릭터인지 검증
//        // 다른 아이디면 자동으로 Exception 처리
//        Character character = characterService.findCharacter(characterId, characterName, username);
//        List<WeekContent> weekContentList = contentService.findAllByCategoryAndWeekCategory(character.getItemLevel(),
//                weekContentDtoList.get(0).getWeekCategory(), weekContentDtoList.get(0).getWeekContentCategory());
//
//        todoServiceV2.updateWeekRaidAll(character, weekContentList);
//
//        return new ResponseEntity(new CharacterDto().toDtoV2(character), HttpStatus.OK);
//    }
}
