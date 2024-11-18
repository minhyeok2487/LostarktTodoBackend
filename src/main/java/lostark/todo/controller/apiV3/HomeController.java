package lostark.todo.controller.apiV3;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lostark.todo.controller.dto.characterDto.CharacterDto;
import lostark.todo.controller.dto.homeDto.HomeDto;
import lostark.todo.controller.dto.homeDto.HomeFriendsDto;
import lostark.todo.controller.dto.homeDto.HomeRaidDto;
import lostark.todo.controller.dto.todoDto.TodoResponseDto;
import lostark.todo.domainV2.character.entity.Character;
import lostark.todo.domainV2.friend.entity.Friends;
import lostark.todo.domain.member.Member;
import lostark.todo.domainV2.character.service.CharacterService;
import lostark.todo.domainV2.friend.service.FriendsService;
import lostark.todo.domainV2.member.service.MemberService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/v3/home")
@Api(tags = {"메인 화면"})
public class HomeController {

    private static final List<String> RAID_SORT_ORDER = Arrays.asList(
            "베히모스", "에키드나", "카멘", "상아탑", "일리아칸", "카양겔", "아브렐슈드", "쿠크세이튼", "비아키스", "발탄", "아르고스");

    private final CharacterService characterService;
    private final MemberService memberService;
    private final FriendsService friendsService;

    @ApiOperation(value = "메인 화면 데이터 호출", notes = "캐릭터 데이터, 숙제 현황")
    @GetMapping()
    public ResponseEntity<?> findAll(@AuthenticationPrincipal String username) {
        //1. 전체 캐릭터 데이터
        Member member = memberService.get(username);
        List<Character> characterList = member.getCharacters();

        //2. 전체 캐릭터 데이터 -> DtoSortedList
        List<CharacterDto> characterDtoList = characterService.updateDtoSortedList(characterList);

        //3. 대표 캐릭터
        CharacterDto mainCharacter = characterDtoList.get(0);

        //4. 주간 총 수익
        double weekTotalGold = characterService.calculateWeekTotalGold(characterList);
        double dayTotalGold = characterService.calculateDayTotalGold(characterList);

        //5. 숙제 현황
        List<HomeRaidDto> homeRaidDtoList = calculateRaidStatus(characterDtoList);
        
        //6. 깐부 현황
        List<Friends> byFromMember = friendsService.findAllByFromMember(member);

        HomeFriendsDto build1 = HomeFriendsDto.builder()
                .characterName(mainCharacter.getCharacterName())
                .gold(dayTotalGold)
                .build();
        List<HomeFriendsDto> friendsDayList = calculateFriendsDayTotalGold(byFromMember);
        friendsDayList.add(build1);
        friendsDayList.sort(Comparator.comparingDouble(HomeFriendsDto::getGold).reversed());

        HomeFriendsDto build2 = HomeFriendsDto.builder()
                .characterName(mainCharacter.getCharacterName())
                .gold(weekTotalGold)
                .build();
        List<HomeFriendsDto> friendsWeekList = calculateFriendsWeekTotalGold(byFromMember);
        friendsWeekList.add(build2);
        friendsWeekList.sort(Comparator.comparingDouble(HomeFriendsDto::getGold).reversed());

        HomeFriendsDto build3 = HomeFriendsDto.builder()
                .characterName(mainCharacter.getCharacterName())
                .gold(dayTotalGold + weekTotalGold)
                .build();
        List<HomeFriendsDto> friendsTotalList = calculateFriendsTotalGold(byFromMember);
        friendsTotalList.add(build3);
        friendsTotalList.sort(Comparator.comparingDouble(HomeFriendsDto::getGold).reversed());


        HomeDto build = HomeDto.builder()
                .homeRaidDtoList(homeRaidDtoList)
                .characterDtoList(characterDtoList)
                .mainCharacter(mainCharacter)
                .weekTotalGold(weekTotalGold)
                .dayTotalGold(dayTotalGold)
                .friendsDayList(friendsDayList)
                .friendsWeekList(friendsWeekList)
                .friendsTotalList(friendsTotalList)
                .build();

        return new ResponseEntity<>(build, HttpStatus.OK);
    }

    // 캐릭터 리스트의 숙제 현황을 계산하는 메서드
    private List<HomeRaidDto> calculateRaidStatus(List<CharacterDto> characterDtoList) {
        Map<String, List<TodoResponseDto>> todoListGroupedByWeekCategory = characterDtoList.stream()
                .flatMap(character -> character.getTodoList().stream())
                .collect(Collectors.groupingBy(TodoResponseDto::getWeekCategory));

        return RAID_SORT_ORDER.stream()
                .map(key -> {
                    List<TodoResponseDto> todoResponseDtos = todoListGroupedByWeekCategory.get(key);
                    if (todoResponseDtos == null || todoResponseDtos.isEmpty()) {
                        return null;
                    }
                    long count = todoResponseDtos.stream().filter(TodoResponseDto::isCheck).count();
                    long totalCount = todoResponseDtos.size();
                    long dealerCount = todoResponseDtos.stream()
                            .filter(dto -> isDealer(dto.getCharacterClassName()))
                            .count();
                    long supportCount = totalCount - dealerCount;

                    return HomeRaidDto.builder()
                            .name(key)
                            .count((int) count)
                            .dealerCount((int) dealerCount)
                            .supportCount((int) supportCount)
                            .totalCount((int) totalCount)
                            .build();
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    // 딜러 서폿 구분하는 메서드
    private boolean isDealer(String characterClassName) {
        switch (characterClassName) {
            case "도화가":
            case "홀리나이트":
            case "바드":
                return false;
            default:
                return true;
        }
    }


    // 깐부 일일 숙제 주간 골드 합
    public List<HomeFriendsDto> calculateFriendsDayTotalGold(List<Friends> friendsList) {
        List<HomeFriendsDto> result = new ArrayList<>();
        for (Friends friends : friendsList) {
            double gold = characterService.calculateDayTotalGold(friends.getMember().getCharacters());
            result.add(HomeFriendsDto.builder()
                    .characterName(friends.getMember().getCharacters().get(0).getCharacterName())
                    .gold(gold)
                    .build());
        }
        return result;
    }

    // 깐부 주간 숙제 주간 골드 합
    public List<HomeFriendsDto> calculateFriendsWeekTotalGold(List<Friends> friendsList) {
        List<HomeFriendsDto> result = new ArrayList<>();
        for (Friends friends : friendsList) {
            double gold = characterService.calculateWeekTotalGold(friends.getMember().getCharacters());
            result.add(HomeFriendsDto.builder()
                    .characterName(friends.getMember().getCharacters().get(0).getCharacterName())
                    .gold(gold)
                    .build());
        }
        return result;
    }

    // 깐부 총 주간 골드 합
    public List<HomeFriendsDto> calculateFriendsTotalGold(List<Friends> friendsList) {
        List<HomeFriendsDto> result = new ArrayList<>();
        for (Friends friends : friendsList) {
            double dayTotalGold = characterService.calculateDayTotalGold(friends.getMember().getCharacters());
            double weekTotalGold = characterService.calculateWeekTotalGold(friends.getMember().getCharacters());
            result.add(HomeFriendsDto.builder()
                    .characterName(friends.getMember().getCharacters().get(0).getCharacterName())
                    .gold(dayTotalGold+weekTotalGold)
                    .build());
        }
        return result;
    }
}
