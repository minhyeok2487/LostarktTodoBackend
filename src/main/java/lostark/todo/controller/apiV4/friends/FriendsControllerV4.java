package lostark.todo.controller.apiV4.friends;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lostark.todo.controller.dto.contentDto.WeekContentDto;
import lostark.todo.controller.dtoV2.firend.FriendsResponse;
import lostark.todo.controller.dtoV2.firend.UpdateFriendWeekRaidRequest;
import lostark.todo.controller.dtoV2.firend.UpdateSortRequest;
import lostark.todo.domain.character.Character;
import lostark.todo.domain.content.WeekContent;
import lostark.todo.domain.friends.Friends;
import lostark.todo.domain.member.Member;
import lostark.todo.domain.todoV2.TodoV2;
import lostark.todo.domainV2.util.content.service.ContentService;
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
@Api(tags = {"깐부 API"})
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

    @ApiOperation(value = "깐부 순서 변경")
    @PutMapping("/sort")
    public ResponseEntity<?> updateSort(@AuthenticationPrincipal String username,
                                        @RequestBody UpdateSortRequest updateSortRequest) {
        Member member = memberService.get(username);
        friendsService.updateSort(member, updateSortRequest.getFriendIdList());

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
                                                  @RequestBody UpdateFriendWeekRaidRequest request) {
        Friends friend = friendsService.findByFriendUsername(request.getFriendUsername(), username);

        if (!friend.getFriendSettings().isSetting()) {
            throw new IllegalArgumentException("권한이 없습니다.");
        }

        Character character = friend.getMember().getCharacters().stream()
                .filter(c -> c.getId() == request.getFriendCharacterId())
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("등록되지 않은 캐릭터 입니다."));

        List<WeekContent> weekContentList = contentService.findAllByIdWeekContent(request.getWeekContentIdList())
                .stream()
                .map(content -> (WeekContent) content)
                .toList();

        if (weekContentList.size() == 1) {
            todoServiceV2.updateWeekRaid(character, weekContentList.get(0));
        } else {
            todoServiceV2.updateWeekRaidAll(character, weekContentList);
        }

        return new ResponseEntity<>(HttpStatus.OK);
    }

}
