package lostark.todo.controller.apiV3.member;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lostark.todo.controller.dto.auth.ResponseDto;
import lostark.todo.domain.member.Member;
import lostark.todo.service.CharacterService;
import lostark.todo.service.MemberService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/v3/member/setting")
@Api(tags = {"회원 설정"})
public class MemberSettingController {

    private final CharacterService characterService;
    private final MemberService memberService;

    @ApiOperation(value = "등록된 캐릭터 삭제", response = ResponseDto.class)
    @DeleteMapping("/characters")
    public ResponseEntity<?> deleteCharacters(@AuthenticationPrincipal String username) {
        Member member = memberService.findMember(username);

        if (characterService.deleteByMember(member)) {
            return new ResponseEntity<>(new ResponseDto(true, "등록된 캐릭터가 정상적으로 삭제되었습니다."), HttpStatus.OK);
        } else {
            throw new IllegalArgumentException("등록된 캐릭터가 없습니다.");
        }
    }
}
