package lostark.todo.controller.apiV3.friends;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lostark.todo.controller.dto.characterDto.*;
import lostark.todo.controller.dto.friendsDto.FindCharacterWithFriendsDto;
import lostark.todo.controller.dto.friendsDto.FriendCharacterResponse;
import lostark.todo.controller.dto.friendsDto.FriendSettingRequestDto;
import lostark.todo.controller.dto.friendsDto.FriendsReturnDto;
import lostark.todo.controller.dto.todoDto.TodoDto;
import lostark.todo.domain.character.Character;
import lostark.todo.domain.character.DayTodo;
import lostark.todo.domain.content.Category;
import lostark.todo.domain.content.DayContent;
import lostark.todo.domain.friends.FriendSettings;
import lostark.todo.domain.friends.Friends;
import lostark.todo.domain.market.Market;
import lostark.todo.domain.member.Member;
import lostark.todo.service.*;
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
import java.util.stream.Collectors;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/v3/friends")
@Api(tags = {"친구, 깐부 API V3"})
public class FriendsTodoApi {

    private final MemberService memberService;
    private final FriendsService friendsService;

    @ApiOperation(value = "깐부 캐릭터 리스트 조회 - 서버별 분리(Map)",
            notes = "key = 서버 이름, value = 캐릭터 리스트",
            response = FriendCharacterResponse.class)
    @GetMapping()
    public ResponseEntity<?> findFriendCharacterList(@AuthenticationPrincipal String username,
                                                     @RequestParam(value = "friendId") Long friendId) {
        Member member = memberService.findMember(username);
        Friends friend = friendsService.findFriend(member, friendId);
        String isFriend = friendsService.isFriend(member, friend.getMember());

        if (isFriend.equals("깐부")) {
            Map<String, List<CharacterDto>> characterDtoMap = friend.getMember().getCharacters().stream()
                    .filter(character -> character.getSettings().isShowCharacter())
                    .map(character -> new CharacterDto().toDtoV2(character))
                    .sorted(Comparator
                            .comparingInt(CharacterDto::getSortNumber)
                            .thenComparing(Comparator.comparingDouble(CharacterDto::getItemLevel).reversed()))
                    .collect(Collectors.groupingBy(CharacterDto::getServerName));
            FriendCharacterResponse friendCharacterResponse = FriendCharacterResponse.builder()
                    .characterDtoMap(characterDtoMap)
                    .friendSettings(friend.getFriendSettings())
                    .build();
            return new ResponseEntity<>(friendCharacterResponse, HttpStatus.OK);
        } else {
            throw new IllegalStateException("깐부가 아니여서 조회할 수 없습니다.");
        }

    }
}
