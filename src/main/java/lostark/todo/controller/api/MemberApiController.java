package lostark.todo.controller.api;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lostark.todo.controller.dto.characterDto.CharacterListResponeDto;
import lostark.todo.controller.dto.characterDto.CharacterResponseDto;
import lostark.todo.controller.dto.characterDto.CharacterCheckDto;
import lostark.todo.controller.dto.characterDto.CharacterUpdateListDto;
import lostark.todo.controller.dto.contentDto.SortedDayContentProfitDto;
import lostark.todo.controller.dto.marketDto.MarketContentResourceDto;
import lostark.todo.controller.dto.memberDto.MemberDto;
import lostark.todo.domain.character.Character;
import lostark.todo.domain.member.Member;
import lostark.todo.service.CharacterService;
import lostark.todo.service.ContentService;
import lostark.todo.service.MarketService;
import lostark.todo.service.MemberService;
import lostark.todo.service.lostarkApi.LostarkCharacterService;
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
@RequestMapping("/api/member")
@Api(tags = {"회원 API"})
public class MemberApiController {

    private final CharacterService characterService;
    private final MarketService marketService;
    private final ContentService contentService;
    private final MemberService memberService;
    private final LostarkCharacterService lostarkCharacterService;

    @ApiOperation(value = "회원과 등록된 캐릭터 리스트 조회",
            notes="휴식게이지를 참고하여 일일컨텐츠 수익 계산하여 함께 리턴",
            response = CharacterListResponeDto.class)
    @GetMapping("/characterList")
    public ResponseEntity getCharacterList(@AuthenticationPrincipal String username) {
        try {
            // username 으로 연결된 캐릭터리스트 호출
            List<Character> characterList = memberService.findMember(username).getCharacters();
            if(characterList.isEmpty()) {
                throw new IllegalArgumentException("등록된 캐릭터가 없습니다.");
            }

            // 캐릭터 레벨에 맞는 일일 컨텐츠 호출
            List<CharacterResponseDto> characterResponseDtoList = contentService.getCharacterListWithDayContent(characterList);

            // 재련재료 데이터 리스트 호출
            List<String> resource = marketService.dayContentResource();

            // 재련재료 데이터 리스트로 거래소 데이터 호출
            Map<String, MarketContentResourceDto> contentResource = marketService.getContentResource(resource);

            // 캐릭터 리스트와 거래소 데이터를 이용한 계산
            characterService.calculateProfit(characterResponseDtoList, contentResource);

            // Profit 순서대로 정렬하기
            List<SortedDayContentProfitDto> sortedDayContentProfit = contentService.sortDayContentProfit(characterResponseDtoList);

            // Profit 합 구하기
            double sum = 0;
            for (SortedDayContentProfitDto dto : sortedDayContentProfit) {
                sum += dto.getProfit();
            }
            sum = Math.round(sum * 100.0) / 100.0;

            // 결과 출력
            CharacterListResponeDto charactersReturnDto = CharacterListResponeDto.builder()
                    .characters(characterResponseDtoList)
                    .sumDayContentProfit(sum)
                    .sortedDayContentProfitDtoList(sortedDayContentProfit)
                    .build();
            log.info(charactersReturnDto.getCharacters().get(0).toString());
            return new ResponseEntity<>(charactersReturnDto, HttpStatus.OK);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @PatchMapping("/characterList")
    public ResponseEntity updateCharacterList(@AuthenticationPrincipal String username) {
        Member member = memberService.findMember(username);
        MemberDto memberDto = MemberDto.builder()
                .apiKey(member.getApiKey())
                .characterName(member.getCharacters().get(0).getCharacterName())
                .username(username)
                .build();
        // 대표캐릭터와 연동된 캐릭터(api 검증)
        List<Character> characterList = lostarkCharacterService.getCharacterList(memberDto);
        log.info("characterList.size={}",characterList.size());

        List<CharacterResponseDto> result = memberService.updateCharacterList(username, characterList);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @ApiOperation(value = "회원과 등록된 캐릭터 리스트 업데이트", response = CharacterUpdateListDto.class)
    @PatchMapping("/todo")
    public ResponseEntity updateTodo(@AuthenticationPrincipal String username,
                                           @RequestBody @Valid List<CharacterCheckDto> characterCheckDtoList) {
        return new ResponseEntity<>(memberService.updateTodo(username, characterCheckDtoList), HttpStatus.OK);
    }


}
