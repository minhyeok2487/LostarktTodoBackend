package lostark.todo.controller.apiV2;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lostark.todo.controller.dto.contentDto.CubeContentDto;
import lostark.todo.controller.dto.friendsDto.FindCharacterWithFriendsDto;
import lostark.todo.controller.dto.friendsDto.FriendsReturnDto;
import lostark.todo.domain.character.Character;
import lostark.todo.domain.content.CubeContent;
import lostark.todo.domain.friends.Friends;
import lostark.todo.domain.market.Market;
import lostark.todo.domain.member.Member;
import lostark.todo.service.CharacterService;
import lostark.todo.service.FriendsService;
import lostark.todo.service.MemberService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/v2/friends")
@Api(tags = {"친구, 깐부 API"})
public class FriendsApiController {

    private final CharacterService characterService;
    private final MemberService memberService;
    private final FriendsService friendsService;

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
    @GetMapping("")
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

    @ApiOperation(value = "친구 요청 수락/거부")
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
}
