package lostark.todo.controller.v2.api;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lostark.todo.controller.v2.dto.characterDto.CharacterListResponeDtoV2;
import lostark.todo.controller.v2.dto.characterDto.CharacterResponseDtoV2;
import lostark.todo.controller.v2.dto.characterDto.CharacterUpdateListDtoV2;
import lostark.todo.controller.v2.dto.contentDto.SortedDayContentProfitDtoV2;
import lostark.todo.controller.v2.dto.marketDto.MarketContentResourceDtoV2;
import lostark.todo.domain.character.Character;
import lostark.todo.domain.member.Member;
import lostark.todo.service.v2.CharacterServiceV2;
import lostark.todo.service.v2.ContentServiceV2;
import lostark.todo.service.v2.MarketServiceV2;
import lostark.todo.service.v2.MemberServiceV2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/v3/member")
@Api(tags = {"회원, 캐릭터 API"})
public class MemberApiControllerV3 {

    private final CharacterServiceV2 characterService;
    private final MarketServiceV2 marketService;
    private final ContentServiceV2 contentService;
    private final MemberServiceV2 memberService;


    @ApiOperation(value = "회원과 등록된 캐릭터 리스트 조회",
            notes="휴식게이지를 참고하여 일일컨텐츠 수익 계산하여 함께 리턴",
            response = CharacterListResponeDtoV2.class)
    @GetMapping("/characterList")
    public ResponseEntity getCharacterList(@AuthenticationPrincipal String username) {
        try {
            // username 으로 연결된 캐릭터리스트 호출
            List<Character> characterList = memberService.findMember(username).getCharacters();
            if(characterList.isEmpty()) {
                throw new IllegalArgumentException("등록된 캐릭터가 없습니다.");
            }

            // 캐릭터 레벨에 맞는 일일 컨텐츠 호출
            List<CharacterResponseDtoV2> characterResponseDtoList = contentService.getCharacterListWithDayContent(characterList);

            // 재련재료 데이터 리스트 호출
            List<String> resource = marketService.dayContentResource();

            // 재련재료 데이터 리스트로 거래소 데이터 호출
            Map<String, MarketContentResourceDtoV2> contentResource = marketService.getContentResource(resource);

            // 캐릭터 리스트와 거래소 데이터를 이용한 계산
            characterService.calculateProfit(characterResponseDtoList, contentResource);

            // Profit 순서대로 정렬하기
            List<SortedDayContentProfitDtoV2> sortedDayContentProfit = contentService.sortDayContentProfit(characterResponseDtoList);

            // Profit 합 구하기
            double sum = 0;
            for (SortedDayContentProfitDtoV2 dto : sortedDayContentProfit) {
                sum += dto.getProfit();
            }
            sum = Math.round(sum * 100.0) / 100.0;

            // 결과 출력
            CharacterListResponeDtoV2 charactersReturnDto = CharacterListResponeDtoV2.builder()
                    .characters(characterResponseDtoList)
                    .sumDayContentProfit(sum)
                    .sortedDayContentProfitDtoList(sortedDayContentProfit)
                    .build();
            return new ResponseEntity<>(charactersReturnDto, HttpStatus.OK);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @ApiOperation(value = "회원과 등록된 캐릭터 리스트 업데이트", response = CharacterUpdateListDtoV2.class)
    @PatchMapping("/{username}")
    public ResponseEntity updateCharacters(@RequestBody @Valid CharacterUpdateListDtoV2 characterUpdateListDtoV2,
                                           @PathVariable String username) {
        return new ResponseEntity<>(memberService.updateCharacterList(username, characterUpdateListDtoV2), HttpStatus.OK);
    }
}
