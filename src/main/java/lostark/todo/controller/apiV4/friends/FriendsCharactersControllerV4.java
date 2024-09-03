package lostark.todo.controller.apiV4.friends;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lostark.todo.controller.dto.characterDto.CharacterDto;
import lostark.todo.controller.dtoV2.character.CharacterJsonDto;
import lostark.todo.domain.character.Character;
import lostark.todo.domain.character.DayTodo;
import lostark.todo.domain.content.Category;
import lostark.todo.domain.content.DayContent;
import lostark.todo.domain.friends.Friends;
import lostark.todo.domain.market.Market;
import lostark.todo.domain.member.Member;
import lostark.todo.service.*;
import lostark.todo.service.lostarkApi.LostarkCharacterService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static lostark.todo.constants.ErrorMessages.FRIEND_PERMISSION_DENIED;


@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/v4/friends/characters")
@Api(tags = {"깐부 캐릭터 리스트 API"})
public class FriendsCharactersControllerV4 {

    private final FriendsService friendsService;
    private final CharacterService characterService;
    private final MarketService marketService;
    private final ContentService contentService;
    private final LostarkCharacterService lostarkCharacterService;

    @ApiOperation(value = "깐부 회원 캐릭터 리스트 업데이트",
            notes="전투 레벨, 아이템 레벨, 이미지url 업데이트 \n" +
                    "캐릭터 아이템 레벨이 달라지면 예상 수익골드 다시 계산 \n" +
                    "캐릭터 추가 및 삭제 ",
            response = CharacterDto.class)
    @PutMapping("/{friendUsername}")
    public ResponseEntity<?> updateCharacterList(@AuthenticationPrincipal String username,
                                              @PathVariable String friendUsername) {
        Friends friend = friendsService.findByFriendUsername(friendUsername, username);
        if (!friend.getFriendSettings().isSetting()) {
            throw new IllegalArgumentException(FRIEND_PERMISSION_DENIED);
        }

        Member member = friend.getMember();
        String mainCharacter = member.getMainCharacter() != null ? member.getMainCharacter() :
                member.getCharacters().get(0).getCharacterName();
        Map<String, Market> contentResource = marketService.findContentResource();
        List<DayContent> chaos = contentService.findDayContent(Category.카오스던전);
        List<DayContent> guardian = contentService.findDayContent(Category.가디언토벌);

        List<CharacterJsonDto> characterJsonDtoList = lostarkCharacterService.getCharacterJsonDtoList(mainCharacter, member.getApiKey());
        for (CharacterJsonDto dto : characterJsonDtoList) {
            dto.setCharacterImage(lostarkCharacterService.getCharacterImageUrl(dto.getCharacterName(), member.getApiKey()));

            Optional<Character> find = member.getCharacters().stream()
                    .filter(character -> character.getCharacterName().equals(dto.getCharacterName())).findFirst();

            DayTodo dayContent = new DayTodo().createDayContent(chaos, guardian, dto.getItemMaxLevel());

            if (find.isPresent()) { // 이름 같은게 있으면 업데이트
                Character character = find.get();
                characterService.updateCharacter(character, dto, dayContent, contentResource);
            } else { // 이름 같은게 없으면 추가
                Character character = characterService.addCharacter(dto, dayContent, member);
                characterService.calculateDayTodo(character, contentResource);
                member.getCharacters().add(character);
            }
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
