package lostark.todo.controller.apiV3.auth;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lostark.todo.domain.member.service.AuthService;
import lostark.todo.domain.member.service.MemberService;
import lostark.todo.global.config.TokenProvider;
import lostark.todo.global.dto.GlobalResponseDto;
import lostark.todo.controller.dto.memberDto.LoginMemberRequest;
import lostark.todo.controller.dto.memberDto.SaveCharacterRequest;
import lostark.todo.domain.member.dto.MemberResponse;
import lostark.todo.domain.character.entity.Character;
import lostark.todo.domain.util.market.entity.Market;
import lostark.todo.domain.member.entity.Member;
import lostark.todo.domain.character.service.CharacterService;
import lostark.todo.domain.util.market.service.MarketService;
import lostark.todo.domain.lostark.client.LostarkCharacterApiClient;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static lostark.todo.global.Constant.TEST_USERNAME;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/v3/auth")
@Api(tags = {"인증(회원가입, 로그인, 로그아웃)"})
public class AuthController {

    private final MemberService memberService;
    private final AuthService authService;
    private final CharacterService characterService;
    private final MarketService marketService;
    private final LostarkCharacterApiClient lostarkCharacterApiClient;
    private final ConcurrentHashMap<String, Boolean> usernameLocks;
    private final TokenProvider tokenProvider;

    // TODO 추후 삭제
    @ApiOperation(value = "1차 회원가입 이후 캐릭터 추가",
            notes="대표캐릭터 검색을 통한 로스트아크 api 검증 \n 대표캐릭터와 연동된 캐릭터 함께 저장")
    @PostMapping("/character")
    public ResponseEntity<?> saveCharacter(
            @AuthenticationPrincipal String username,
            @RequestBody SaveCharacterRequest request) {
        if (username.equals(TEST_USERNAME)) {
            throw new IllegalStateException("테스트 계정은 캐릭터 등록이 불가능 합니다.");
        }
        if (usernameLocks.putIfAbsent(username, true) != null) {
            throw new IllegalStateException("이미 진행중입니다.");
        }
        try {
            // 대표캐릭터와 연동된 캐릭터 호출(api 검증)
            List<Character> characterList = lostarkCharacterApiClient.createCharacterList(request.getCharacterName(), request.getApiKey());

            // 재련재료 데이터 리스트로 거래소 데이터 호출
            Map<String, Market> contentResource = marketService.findContentResource();

            // 일일숙제 예상 수익 계산(휴식 게이지 포함)
            List<Character> calculatedCharacterList = new ArrayList<>();
            for (Character character : characterList) {
                Character result = characterService.calculateDayTodo(character, contentResource);
                calculatedCharacterList.add(result);
            }

            // Member 회원가입
            memberService.createCharacterOLDER(username, request, calculatedCharacterList);

            return new ResponseEntity<>(HttpStatus.OK);
        } finally {
            usernameLocks.remove(username);
        }
    }

    // TODO 추후 삭제
    @ApiOperation(value = "일반 로그인",
            notes="JWT", response = MemberResponse.class)
    @PostMapping("/login")
    public ResponseEntity<?> loginMember(@RequestBody @Valid LoginMemberRequest request) {
        Member member = memberService.validateLogin(request);
        String token = tokenProvider.createToken(member);

        MemberResponse responseDto = MemberResponse.builder()
                .username(member.getUsername())
                .token(token)
                .build();
        return new ResponseEntity<>(responseDto, HttpStatus.OK);
    }

    // TODO 추후 삭제
    @ApiOperation(value = "로그아웃",
            notes = "로그인 유형 상관없이 로그아웃",
            response = String.class)
    @GetMapping("/logout")
    public ResponseEntity<?> logout(@AuthenticationPrincipal String username) {
        Member member = memberService.get(username);

        GlobalResponseDto globalResponseDto = null;
        if (member.getAuthProvider().equals("Google")) {
            try {
                globalResponseDto = authService.googleLogout(member);
            } catch (Exception e) {
                return new ResponseEntity<>(new GlobalResponseDto(false, "구글 로그아웃 실패 : " + e.getMessage()), HttpStatus.BAD_REQUEST);
            }
        }
        if (member.getAuthProvider().equals("none")) {
            globalResponseDto = new GlobalResponseDto(true, "로그아웃 성공");
        }

        return new ResponseEntity<>(globalResponseDto, HttpStatus.OK);
    }
}
