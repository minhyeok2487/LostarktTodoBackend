package lostark.todo.controller.apiV4.characters;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lostark.todo.controller.dto.characterDto.CharacterDto;
import lostark.todo.controller.dto.characterDto.CharacterSortDto;
import lostark.todo.controller.dtoV2.character.CharacterJsonDto;
import lostark.todo.controller.dtoV2.character.CharacterResponse;
import lostark.todo.domainV2.character.entity.Character;
import lostark.todo.domainV2.character.entity.DayTodo;
import lostark.todo.domain.content.Category;
import lostark.todo.domain.content.DayContent;
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
import java.util.stream.Collectors;


@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/v4/characters")
@Api(tags = {"회원 캐릭터 리스트"})
public class CharactersControllerV4 {

    private final CharacterService characterService;
    private final MarketService marketService;
    private final ContentService contentService;
    private final MemberService memberService;
    private final LostarkCharacterDao lostarkCharacterDao;

    // TODO 추후 삭제
    @ApiOperation(value = "캐릭터 + 숙제 정보 조회 API",
            response = CharacterResponse.class)
    @GetMapping()
    public ResponseEntity<?> get(@AuthenticationPrincipal String username) {
        List<Character> characterList = memberService.get(username).getCharacters();
        List<CharacterResponse> responseList = characterList.stream()
                .map(CharacterResponse::toDto)
                .sorted(Comparator
                        .comparingInt(CharacterResponse::getSortNumber)
                        .thenComparing(Comparator.comparingDouble(CharacterResponse::getItemLevel).reversed()))
                .collect(Collectors.toList());
        return new ResponseEntity<>(responseList, HttpStatus.OK);
    }

    // TODO 추후 삭제
    @ApiOperation(value = "캐릭터 리스트 순서변경 저장", response = CharacterResponse.class)
    @PatchMapping("/sorting")
    public ResponseEntity<?> updateSort(@AuthenticationPrincipal String username,
                                     @RequestBody @Valid List<CharacterSortDto> characterSortDtoList) {
        List<Character> characterList = memberService.editSort(username, characterSortDtoList);
        List<CharacterResponse> responseList = characterList.stream()
                .map(CharacterResponse::toDto)
                .sorted(Comparator
                        .comparingInt(CharacterResponse::getSortNumber)
                        .thenComparing(Comparator.comparingDouble(CharacterResponse::getItemLevel).reversed()))
                .toList();
        return new ResponseEntity<>(responseList, HttpStatus.OK);
    }

    // TODO 삭제 예정
    @ApiOperation(value = "회원 캐릭터 리스트 업데이트",
            notes="전투 레벨, 아이템 레벨, 이미지url 업데이트 \n" +
                    "캐릭터 아이템 레벨이 달라지면 예상 수익골드 다시 계산 \n" +
                    "캐릭터 추가 및 삭제 ",
            response = CharacterDto.class)
    @PutMapping("")
    public ResponseEntity updateCharacterList(@AuthenticationPrincipal String username) {
        Member member = memberService.get(username);
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
                characterService.calculateDayTodo(character, contentResource);
                member.getCharacters().add(character);
            }
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
