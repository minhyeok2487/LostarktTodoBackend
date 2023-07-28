package lostark.todo.controller.v2.api;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lostark.todo.controller.v1.dto.characterDto.CharacterResponseDto;
import lostark.todo.controller.v1.dto.characterDto.CharacterResponseDtoV2;
import lostark.todo.controller.v1.dto.memberDto.MemberResponseDto;
import lostark.todo.controller.v2.dto.memberDto.MemberSignupDtoV2;
import lostark.todo.domain.Role;
import lostark.todo.domain.character.Character;
import lostark.todo.domain.member.Member;
import lostark.todo.service.v1.lostarkApi.LostarkCharacterService;
import lostark.todo.service.v2.CharacterServiceV2;
import lostark.todo.service.v2.ContentServiceV2;
import lostark.todo.service.v2.LostarkCharacterServiceV2;
import lostark.todo.service.v2.MemberServiceV2;
import org.json.simple.JSONArray;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/v2/member")
@Api(tags = {"회원 REST API"})
public class MemberApiControllerV2 {

    private final CharacterServiceV2 characterService;
    private final ContentServiceV2 contentService;
    private final MemberServiceV2 memberService;
    private final LostarkCharacterServiceV2 lostarkCharacterService;

    @ApiOperation(value = "회원 가입",
            notes="대표캐릭터 검색을 통한 로스트아크 api 검증 \n 대표캐릭터와 연동된 캐릭터 함께 저장",
            response = MemberResponseDto.class)
    @PostMapping("/signup")
    public ResponseEntity signupMember(@RequestBody @Valid MemberSignupDtoV2 memberSignupDto) {
        // 대표캐릭터와 연동된 캐릭터(api 검증)
        List<Character> characterList = lostarkCharacterService.getCharacterList(memberSignupDto);

        // Member 회원가입
        Member signupMember = memberService.signup(memberSignupDto, characterList);

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
        return new ResponseEntity(responseDto, HttpStatus.CREATED);
    }




    @ApiOperation(value = "회원과 등록된 캐릭터 리스트 조회",
            notes="휴식게이지를 참고하여 일일컨텐츠 수익 계산하여 함께 리턴(아직)",
            response = CharacterResponseDto.class)
    @GetMapping("/{username}")
    public ResponseEntity getCharacterList(@PathVariable String username) {
        try {
            // username 으로 연결된 캐릭터리스트 호출
            List<Character> characterList = memberService.findMember(username).getCharacters();
            if(characterList.isEmpty()) {
                throw new RuntimeException("등록된 캐릭터가 없습니다.");
            }

            // 캐릭터 레벨에 맞는 일일 컨텐츠 호출
            List<CharacterResponseDtoV2> characterResponseDtoList = contentService.calculateDayContent(characterList);


            return new ResponseEntity<>(characterResponseDtoList, HttpStatus.OK);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
