package lostark.todo.controller.apiV4.characters;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lostark.todo.controller.dto.characterDto.CharacterDto;
import lostark.todo.controller.dto.characterDto.CharacterSortDto;
import lostark.todo.controller.dtoV2.character.CharacterResponse;
import lostark.todo.domain.character.Character;
import lostark.todo.domain.character.DayTodo;
import lostark.todo.domain.content.Category;
import lostark.todo.domain.content.DayContent;
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
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/v4/characters")
@Api(tags = {"회원 캐릭터 리스트"})
public class CharactersControllerV4 {

    private final CharacterService characterService;
    private final MarketService marketService;
    private final ContentService contentService;
    private final MemberService memberService;
    private final LostarkCharacterService lostarkCharacterService;

    @ApiOperation(value = "캐릭터 + 숙제 정보 조회 API",
            response = CharacterResponse.class)
    @GetMapping()
    public ResponseEntity<?> get(@AuthenticationPrincipal String username) {
        List<Character> characterList = characterService.findCharacterListUsername(username);
        List<CharacterResponse> responseList = characterList.stream()
                .map(CharacterResponse::toDto)
                .sorted(Comparator
                        .comparingInt(CharacterResponse::getSortNumber)
                        .thenComparing(Comparator.comparingDouble(CharacterResponse::getItemLevel).reversed()))
                .collect(Collectors.toList());
        return new ResponseEntity<>(responseList, HttpStatus.OK);
    }

    @ApiOperation(value = "캐릭터 리스트 순서변경 저장", response = CharacterDto.class)
    @PatchMapping("/sorting")
    public ResponseEntity updateSort(@AuthenticationPrincipal String username,
                                     @RequestBody @Valid List<CharacterSortDto> characterSortDtoList) {
        memberService.updateSort(username, characterSortDtoList);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @ApiOperation(value = "회원 캐릭터 리스트 업데이트",
            notes="전투 레벨, 아이템 레벨, 이미지url 업데이트 \n" +
                    "캐릭터 아이템 레벨이 달라지면 예상 수익골드 다시 계산 \n" +
                    "캐릭터 추가 및 삭제 ",
            response = CharacterDto.class)
    @PutMapping("")
    public ResponseEntity updateCharacterList(@AuthenticationPrincipal String username) {
//        Member member = memberService.findMember(username);
//        List<DayContent> chaos = contentService.findDayContent(Category.카오스던전);
//        List<DayContent> guardian = contentService.findDayContent(Category.가디언토벌);
//
//        List<Character> removeList = new ArrayList<>();
//        // 비교 : 캐릭터 이름, 아이템레벨, 클래스
//        for (Character character : member.getCharacters()) {
//            JSONObject jsonObject = lostarkCharacterService.findCharacter(character.getCharacterName(), member.getApiKey());
//            if (jsonObject == null) {
//                log.info("delete character name : {}", character.getCharacterName());
//                //삭제 리스트에 추가
//                removeList.add(character);
//            } else {
//                double itemMaxLevel = Double.parseDouble(jsonObject.get("ItemMaxLevel").toString().replace(",", ""));
//
//                // 데이터 변경
//                if (itemMaxLevel >= 1415.0) {
//                    Character newCharacter = Character.builder()
//                            .characterName(jsonObject.get("CharacterName") != null ? jsonObject.get("CharacterName").toString() : null)
//                            .characterClassName(jsonObject.get("CharacterClassName") != null ? jsonObject.get("CharacterClassName").toString() : null)
//                            .characterImage(jsonObject.get("CharacterImage") != null ? jsonObject.get("CharacterImage").toString() : null)
//                            .characterLevel(Integer.parseInt(jsonObject.get("CharacterLevel").toString()))
//                            .itemLevel(itemMaxLevel)
//                            .dayTodo(new DayTodo().createDayContent(chaos, guardian, itemMaxLevel))
//                            .build();
//                    characterService.updateCharacter(character, newCharacter);
//                } else {
//                    //삭제 리스트에 추가
//                    removeList.add(character);
//                }
//            }
//        }
//
//        // 삭제
//        if (!removeList.isEmpty()) {
//            for (Character character : removeList) {
//                characterService.deleteCharacter(member.getCharacters(), character);
//            }
//        }
//
//        // 추가 리스트
//        List<Character> addList = new ArrayList<>();
//        List<Character> updateCharacterList = lostarkCharacterService.findCharacterList(
//                member.getCharacters().get(0).getCharacterName(), member.getApiKey(), chaos, guardian);
//        for (Character character : updateCharacterList) {
//            boolean contain = false;
//            for (Character before : member.getCharacters()) {
//                if (before.getCharacterName().equals(character.getCharacterName())) {
//                    contain = true;
//                    break;
//                }
//            }
//            if (!contain) {
//                addList.add(character);
//            }
//        }
//
//        //추가 하면서 캐릭터 닉네임 변경감지
//        if (!addList.isEmpty()) {
//            characterService.addCharacterList(addList, removeList, member);
//        }
//
//        // 재련재료 데이터 리스트로 거래소 데이터 호출
//        Map<String, Market> contentResource = marketService.findContentResource();
//
//        // 일일숙제 예상 수익 계산(휴식 게이지 포함)
//        for (Character character : member.getCharacters()) {
//            characterService.calculateDayTodo(character, contentResource);
//        }
        throw new IllegalArgumentException("로스트아크 API 쪽이 달라져서 현재 수정 중입니다.");
//        return new ResponseEntity<>(HttpStatus.OK);
    }
}
