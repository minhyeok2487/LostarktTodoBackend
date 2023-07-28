package lostark.todo.controller.v1.apiController;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lostark.todo.controller.v1.dto.characterDto.CharacterListReturnDto;
import lostark.todo.controller.v1.dto.characterDto.CharacterResponseDto;
import lostark.todo.controller.v1.dto.contentDto.SortedDayContentProfitDto;
import lostark.todo.controller.v1.dto.marketDto.MarketContentResourceDto;
import lostark.todo.controller.v1.dto.memberDto.MemberResponseDto;
import lostark.todo.controller.v1.dto.memberDto.MemberSignupDto;
import lostark.todo.domain.character.Character;
import lostark.todo.domain.member.Member;
import lostark.todo.security.SecurityService;
import lostark.todo.service.v1.CharacterService;
import lostark.todo.service.v1.ContentService;
import lostark.todo.service.v1.MarketService;
import lostark.todo.service.v1.MemberService;
import lostark.todo.service.v1.lostarkApi.LostarkCharacterService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/v1/member")
@Api(tags = {"회원 REST API"})
public class MemberApiController {

    private final MarketService marketService;
    private final ContentService contentService;
    private final MemberService memberService;
    private final SecurityService securityService;
    private final LostarkCharacterService lostarkCharacterService;
    private final CharacterService characterService;


    @ApiOperation(value = "회원 가입",
            notes="대표캐릭터 검색을 통한 로스트아크 api 검증 \n 대표캐릭터와 연동된 캐릭터 함께 저장")
    @PostMapping("/signup")
    public ResponseEntity signupMember(@RequestBody @Valid MemberSignupDto memberSignupDto) {
        // 대표캐릭터 검색을 통한 로스트아크 api 검증
        List<Character> characterList = lostarkCharacterService.getCharacterList(memberSignupDto.getApiKey(), memberSignupDto.getCharacterName());

        // 회원가입
        Member signupMember = memberService.signup(memberSignupDto, characterList);

        // 결과 출력
        MemberResponseDto responseDto = MemberResponseDto.builder()
                .id(signupMember.getId())
                .username(signupMember.getUsername())
                .characters(signupMember.getCharacters()
                        .stream().map(o -> o.getCharacterName()).collect(Collectors.toList()))
                .build();
        return new ResponseEntity(responseDto, HttpStatus.CREATED);
    }




    @ApiOperation(value = "회원과 등록된 캐릭터 리스트 조회",
            notes="휴식게이지를 참고하여 일일컨텐츠 수익 계산하여 함께 리턴",
            response = CharacterListReturnDto.class)
    @GetMapping("/{username}")
    public ResponseEntity getCharacterList(@PathVariable String username) {
        try {
//            // username 으로 연결된 캐릭터리스트 호출
//            List<Character> characterList = memberService.findMember(username).getCharacters();
//            if(characterList.isEmpty()) {
//                throw new RuntimeException("등록된 캐릭터가 없습니다.");
//            }
//
//            // 재련재료 데이터 리스트
//            List<String> resource = marketService.dayContentResource();
//
//            // 거래소 데이터 가져옴(Map)
//            Map<String, MarketContentResourceDto> contentResource = marketService.getContentResource(resource);
//
//            // 캐릭터 레벨에 맞는 일일 컨텐츠 호출
//            List<CharacterResponseDto> characterResponseDtoList = contentService.calculatDayContent(characterList, contentResource);
//
//            // 일일숙제 선택된 캐릭터들
//            // Profit 순서대로 정렬하기
//            List<SortedDayContentProfitDto> sortedDayContentProfit = contentService.sortDayContentProfit(characterResponseDtoList);
//
//            // Profit 합 구하기
//            double sum = 0;
//            for (SortedDayContentProfitDto dto : sortedDayContentProfit) {
//                sum += dto.getProfit();
//            }
//            sum = Math.round(sum * 100.0) / 100.0;
//
//            // 결과 출력
//            CharacterListReturnDto charactersReturnDto = new CharacterListReturnDto();
//            charactersReturnDto.setCharacters(characterResponseDtoList);
//            charactersReturnDto.setSumDayContentProfit(sum);
//            charactersReturnDto.setSortedDayContentProfitDtoList(sortedDayContentProfit);

            return new ResponseEntity<>("null", HttpStatus.OK);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
