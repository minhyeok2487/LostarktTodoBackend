package lostark.todo.controller.api;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lostark.todo.config.TokenProvider;
import lostark.todo.controller.dto.characterDto.CharacterResponseDto;
import lostark.todo.controller.dto.memberDto.MemberResponseDto;
import lostark.todo.controller.dto.memberDto.MemberDto;
import lostark.todo.controller.dto.memberDto.MemberLoginDto;
import lostark.todo.domain.character.Character;
import lostark.todo.domain.content.DayContent;
import lostark.todo.domain.market.Market;
import lostark.todo.domain.member.Member;
import lostark.todo.service.CharacterService;
import lostark.todo.service.ContentService;
import lostark.todo.service.MarketService;
import lostark.todo.service.MemberService;
import lostark.todo.service.lostarkApi.LostarkCharacterService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 인증 (로그인, 회원가입) API
 */
@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/auth")
@Api(tags = {"인증 API"})
public class AuthApiController {

    private final MemberService memberService;
    private final LostarkCharacterService lostarkCharacterService;
    private final TokenProvider tokenProvider;
    private final MarketService marketService;
    private final ContentService contentService;
    private final CharacterService characterService;

    @ApiOperation(value = "회원 가입",
            notes="대표캐릭터 검색을 통한 로스트아크 api 검증 \n 대표캐릭터와 연동된 캐릭터 함께 저장",
            response = MemberResponseDto.class)
    @PostMapping("/signup")
    public ResponseEntity signupMember(@RequestBody @Valid MemberDto memberDto) {
        // 대표캐릭터와 연동된 캐릭터(api 검증)
        List<Character> characterList = lostarkCharacterService.getCharacterList(memberDto);

        // 재련재료 데이터 리스트로 거래소 데이터 호출
        Map<String, Market> contentResource = marketService.getContentResource();

        // 일일 숙제 통계 가져오기
        Map<String, DayContent> dayContent = contentService.findDayContent();

        for (Character character : characterList) {
            // 일일숙제 예상 수익 계산(휴식 게이지 포함)
            characterService.calculateDayTodo(character, contentResource, dayContent);
        }

        // Member 회원가입
        Member signupMember = memberService.createMember(memberDto, characterList);

        // 결과 출력
        MemberResponseDto responseDto = MemberResponseDto.builder()
                .id(signupMember.getId())
                .username(signupMember.getUsername())
                .characters(signupMember.getCharacters().stream().map(
                                character -> character.getCharacterName()
                                        + " / " + character.getCharacterClassName()
                                        + " / Lv." + character.getItemLevel())
                        .collect(Collectors.toList()))
                .build();
        return new ResponseEntity(responseDto, HttpStatus.OK);
    }

    @ApiOperation(value = "로그인",
            notes="JWT", response = MemberResponseDto.class)
    @PostMapping("/login")
    public ResponseEntity loginMember(@RequestBody @Valid MemberLoginDto memberloginDto) {
        Member member = memberService.login(memberloginDto);
        String token = tokenProvider.createToken(member);

        MemberResponseDto responseDto = MemberResponseDto.builder()
                .username(member.getUsername())
                .token(token)
                .build();
        return new ResponseEntity(responseDto, HttpStatus.OK);
    }
}
