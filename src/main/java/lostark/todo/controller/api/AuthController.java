package lostark.todo.controller.api;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lostark.todo.config.TokenProvider;
import lostark.todo.controller.dto.memberDto.MemberResponseDto;
import lostark.todo.controller.v1.dto.memberDto.MemberResponseDtoV1;
import lostark.todo.controller.dto.memberDto.MemberSignupDto;
import lostark.todo.controller.dto.memberDto.MemberLoginDto;
import lostark.todo.domain.character.Character;
import lostark.todo.domain.member.Member;
import lostark.todo.service.MemberService;
import lostark.todo.service.v2.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 인증 (로그인, 회원가입) API
 */
@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/auth")
@Api(tags = {"인증 API"})
public class AuthController {

    private final MemberService memberService;
    private final LostarkCharacterServiceV2 lostarkCharacterService;
    private final TokenProvider tokenProvider;

    @ApiOperation(value = "회원 가입",
            notes="대표캐릭터 검색을 통한 로스트아크 api 검증 \n 대표캐릭터와 연동된 캐릭터 함께 저장",
            response = MemberResponseDto.class)
    @PostMapping("/signup")
    public ResponseEntity signupMember(@RequestBody @Valid MemberSignupDto memberSignupDto) {
        // 대표캐릭터와 연동된 캐릭터(api 검증)
        List<Character> characterList = lostarkCharacterService.getCharacterList(memberSignupDto);

        // Member 회원가입
        Member signupMember = memberService.createMember(memberSignupDto, characterList);

        // 결과 출력
        MemberResponseDtoV1 responseDto = MemberResponseDtoV1.builder()
                .id(signupMember.getId())
                .username(signupMember.getUsername())
                .characters(signupMember.getCharacters().stream().map(
                                character -> character.getCharacterName()
                                        + " / " + character.getCharacterClassName()
                                        + " / Lv." + character.getItemLevel())
                        .collect(Collectors.toList()))
                .build();
        return new ResponseEntity(responseDto, HttpStatus.CREATED);
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
