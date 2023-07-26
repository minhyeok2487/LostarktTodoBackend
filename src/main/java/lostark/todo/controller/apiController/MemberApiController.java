package lostark.todo.controller.apiController;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lostark.todo.controller.dto.characterDto.CharacterListReturnDto;
import lostark.todo.controller.dto.characterDto.CharacterResponseDto;
import lostark.todo.controller.dto.contentDto.SortedDayContentProfitDto;
import lostark.todo.controller.dto.marketDto.MarketContentResourceDto;
import lostark.todo.controller.dto.memberDto.MemberSignupDto;
import lostark.todo.domain.character.Character;
import lostark.todo.domain.member.Member;
import lostark.todo.service.CharacterService;
import lostark.todo.service.ContentService;
import lostark.todo.service.MarketService;
import lostark.todo.service.MemberService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/member")
@Api(tags = {"회원 REST API"})
public class MemberApiController {

    private final MarketService marketService;
    private final ContentService contentService;
    private final MemberService memberService;

    @ApiOperation(value = "회원 가입", notes="미완성")
    @PostMapping("/signup")
    public ResponseEntity signup(@RequestBody MemberSignupDto signupDto) {
        Member signupMember = memberService.signup(signupDto);
        return new ResponseEntity(signupMember, HttpStatus.CREATED);
    }



    @ApiOperation(value = "회원과 등록된 캐릭터 리스트 조회",
            notes="휴식게이지를 참고하여 일일컨텐츠 수익 계산하여 함께 리턴",
            response = CharacterListReturnDto.class)
    @GetMapping("/{username}")
    public ResponseEntity characterList(@ApiParam(value = "유저 네임", required = true) @PathVariable String username) {
        try {
            // header : username으로 연결된 캐릭터리스트 중 선택할 리스트 가져옴
            List<Character> characterList = memberService.findMemberAndCharacter(username);

            // 거래소 데이터 가져옴(Map)
            Map<String, MarketContentResourceDto> contentResource = marketService.getContentResource(marketService.dayContentResource());

            // ItemLevel이 1415이상인 캐릭터는 레벨에 맞는 일일 컨텐츠 가져온후 계산
            List<CharacterResponseDto> characterResponseDtoList = contentService.calculateDayContent(characterList, contentResource);

            // 일일숙제 선택된 캐릭터들
            // Profit 순서대로 정렬하기
            List<SortedDayContentProfitDto> sortedDayContentProfit = contentService.sortDayContentProfit(characterResponseDtoList);

            // Profit 합 구하기
            double sum = 0;
            for (SortedDayContentProfitDto dto : sortedDayContentProfit) {
                sum += dto.getProfit();
            }
            sum = Math.round(sum * 100.0) / 100.0;

            // 결과 출력
            CharacterListReturnDto charactersReturnDto = new CharacterListReturnDto();
            charactersReturnDto.setCharacters(characterResponseDtoList);
            charactersReturnDto.setSumDayContentProfit(sum);
            charactersReturnDto.setSortedDayContentProfitDtoList(sortedDayContentProfit);

            return new ResponseEntity<>(charactersReturnDto, HttpStatus.OK);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
