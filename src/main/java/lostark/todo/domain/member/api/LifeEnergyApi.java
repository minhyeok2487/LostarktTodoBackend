package lostark.todo.domain.member.api;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lostark.todo.domain.member.dto.*;
import lostark.todo.domain.member.entity.Member;
import lostark.todo.domain.member.service.LifeEnergyService;
import lostark.todo.domain.member.service.MemberService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;


@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/v1/life-energy")
@Api(tags = {"생활의 기운"})
public class LifeEnergyApi {

    private final LifeEnergyService service;
    private final MemberService memberService;

    @ApiOperation(value = "생활의 기운 추가")
    @PostMapping("")
    public ResponseEntity<?> save(@AuthenticationPrincipal String username,
                                    @RequestBody @Valid LifeEnergySaveRequest request) {
        Member member = memberService.get(username);
        service.save(member, request);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @ApiOperation(value = "생활의 기운 수정")
    @PutMapping("")
    public ResponseEntity<?> update(@AuthenticationPrincipal String username,
                                    @RequestBody @Valid LifeEnergyUpdateRequest request) {
        service.update(username, request);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @ApiOperation(value = "생활의 기운 캐릭터 삭제")
    @DeleteMapping("/{characterName}")
    public ResponseEntity<?> deleteCharacterLifeEnergy(@AuthenticationPrincipal String username,
                                                       @PathVariable String characterName) {
        service.delete(username, characterName);
        return new ResponseEntity<>(HttpStatus.OK);
    }

}
