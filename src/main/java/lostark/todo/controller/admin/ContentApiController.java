package lostark.todo.controller.admin;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lostark.todo.controller.dto.characterDto.CharacterCheckDto;
import lostark.todo.controller.dto.characterDto.CharacterListResponeDto;
import lostark.todo.controller.dto.characterDto.CharacterResponseDto;
import lostark.todo.controller.dto.contentDto.SortedDayContentProfitDto;
import lostark.todo.controller.dto.contentDto.WeekContentDto;
import lostark.todo.controller.dto.marketDto.MarketContentResourceDto;
import lostark.todo.controller.dto.memberDto.MemberDto;
import lostark.todo.domain.character.Character;
import lostark.todo.domain.content.WeekContent;
import lostark.todo.domain.market.Market;
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
@RequestMapping("/admin/content")
@Api(tags = {"콘텐츠 관리 api"})
public class ContentApiController {

    private final ContentService contentService;

    @GetMapping()
    public ResponseEntity getContent() {
        return new ResponseEntity(contentService.findAll(), HttpStatus.OK);
    }

    @PostMapping("/weekContent")
    public ResponseEntity addContent(@RequestBody @Valid WeekContentDto weekContentDto) {
        WeekContent weekContent = weekContentDto.toEntity();
        return new ResponseEntity(contentService.save(weekContent), HttpStatus.OK);
    }
}
