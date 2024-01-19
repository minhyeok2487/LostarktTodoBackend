package lostark.todo.controller.apiV3.auth;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lostark.todo.config.TokenProvider;
import lostark.todo.controller.dto.auth.AuthResponseDto;
import lostark.todo.controller.dto.auth.AuthSignupDto;
import lostark.todo.controller.dto.memberDto.MemberLoginDto;
import lostark.todo.controller.dto.memberDto.MemberRequestDto;
import lostark.todo.controller.dto.memberDto.MemberResponseDto;
import lostark.todo.domain.character.Character;
import lostark.todo.domain.content.Category;
import lostark.todo.domain.content.DayContent;
import lostark.todo.domain.market.Market;
import lostark.todo.domain.member.Member;
import lostark.todo.event.entity.MemberEvent;
import lostark.todo.event.entity.EventType;
import lostark.todo.service.*;
import lostark.todo.service.lostarkApi.LostarkCharacterService;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/v3/auth")
@Api(tags = {"인증(회원가입, 로그인, 로그아웃)"})
public class AuthController {

    private final MemberService memberService;
    private final AuthService authService;
    private final MailService mailService;
    private final CharacterService characterService;
    private final MarketService marketService;
    private final ContentService contentService;
    private final LostarkCharacterService lostarkCharacterService;
    private final ConcurrentHashMap<String, Boolean> usernameLocks;
    private final TokenProvider tokenProvider;
    private final ApplicationEventPublisher eventPublisher;

    @ApiOperation(value = "1차 회원 가입",
            notes="이메일, 비밀번호(O), Api-Key, 대표캐릭터(X)", response = AuthResponseDto.class)
    @PostMapping("/signup")
    public ResponseEntity<?> signUp(@RequestBody @Valid AuthSignupDto authSignupDto) {
        boolean auth = mailService.isAuth(authSignupDto);
        if (!auth) {
            throw new IllegalStateException("이메일 인증이 실패하였습니다.");
        }

        if (!authSignupDto.getPassword().equals(authSignupDto.getEqualPassword())) {
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }

        // Member 회원가입
        Member signupMember = memberService.createMember(authSignupDto.getMail(), authSignupDto.getPassword());

        // 회원가입 완료시 Redis에 저장된 인증번호 모두 삭제
        mailService.deleteAll(authSignupDto.getMail());

        // 이벤트 실행
        EventType eventType = EventType.signUp;
        String message = eventType.getMessage() + "/ username : " + authSignupDto.getMail();
        eventPublisher.publishEvent(new MemberEvent(eventPublisher, signupMember, eventType));

        return new ResponseEntity<>(new AuthResponseDto(true, message), HttpStatus.CREATED);
    }

    @ApiOperation(value = "1차 회원가입 이후 캐릭터 추가",
            notes="대표캐릭터 검색을 통한 로스트아크 api 검증 \n 대표캐릭터와 연동된 캐릭터 함께 저장",
            response = MemberResponseDto.class)
    @PostMapping("/character")
    public ResponseEntity<?> saveCharacter(
            @AuthenticationPrincipal String username,
            @RequestBody MemberRequestDto memberDto) {
        if (usernameLocks.putIfAbsent(username, true) != null) {
            throw new IllegalStateException("이미 진행중입니다.");
        }
        try {
            // 일일 컨텐츠 통계(카오스던전, 가디언토벌) 호출
            List<DayContent> chaos = contentService.findDayContent(Category.카오스던전);
            List<DayContent> guardian = contentService.findDayContent(Category.가디언토벌);

            // 대표캐릭터와 연동된 캐릭터 호출(api 검증)
            List<Character> characterList = lostarkCharacterService.findCharacterList(memberDto.getCharacterName(), memberDto.getApiKey(), chaos, guardian);

            // 재련재료 데이터 리스트로 거래소 데이터 호출
            Map<String, Market> contentResource = marketService.findContentResource();

            // 일일숙제 예상 수익 계산(휴식 게이지 포함)
            List<Character> calculatedCharacterList = new ArrayList<>();
            for (Character character : characterList) {
                Character result = characterService.calculateDayTodo(character, contentResource);
                calculatedCharacterList.add(result);
            }

            // Member 회원가입
            Member signupMember = memberService.createCharacter(username, memberDto.getApiKey(), calculatedCharacterList);

            // 이벤트 실행
            EventType eventType = EventType.addCharacters;
            eventPublisher.publishEvent(new MemberEvent(eventPublisher, signupMember, eventType));

            // 결과 출력
            MemberResponseDto memberResponseDto = new MemberResponseDto().toDto(signupMember);
            return new ResponseEntity<>(memberResponseDto, HttpStatus.OK);
        } finally {
            usernameLocks.remove(username);
        }
    }

    @ApiOperation(value = "일반 로그인",
            notes="JWT", response = MemberResponseDto.class)
    @PostMapping("/login")
    public ResponseEntity<?> loginMember(@RequestBody @Valid MemberLoginDto memberloginDto) {
        Member member = memberService.login(memberloginDto);
        String token = tokenProvider.createToken(member);

        MemberResponseDto responseDto = MemberResponseDto.builder()
                .username(member.getUsername())
                .token(token)
                .build();
        return new ResponseEntity<>(responseDto, HttpStatus.OK);
    }

    @ApiOperation(value = "로그아웃",
            notes = "로그인 유형 상관없이 로그아웃",
            response = String.class)
    @GetMapping("/logout")
    public ResponseEntity<?> logout(@AuthenticationPrincipal String username) {
        Member member = memberService.findMember(username);

        AuthResponseDto authResponseDto = null;
        if (member.getAuthProvider().equals("Google")) {
            try {
                authResponseDto = authService.googleLogout(member);
            } catch (Exception e) {
                return new ResponseEntity<>(new AuthResponseDto(false, "구글 로그아웃 실패 : " + e.getMessage()), HttpStatus.BAD_REQUEST);
            }
        }
        if (member.getAuthProvider().equals("none")) {
            authResponseDto = new AuthResponseDto(true, "로그아웃 성공");
        }

        return new ResponseEntity<>(authResponseDto, HttpStatus.OK);
    }
}
