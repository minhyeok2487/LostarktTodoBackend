package lostark.todo.controller.apiV2.characterApi;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lostark.todo.controller.dto.characterDto.CharacterDto;
import lostark.todo.controller.dto.contentDto.CubeContentDto;
import lostark.todo.controller.dto.contentDto.WeekContentDto;
import lostark.todo.domain.character.Character;
import lostark.todo.domain.content.CubeContent;
import lostark.todo.domain.content.WeekContent;
import lostark.todo.domain.market.Market;
import lostark.todo.domain.todoV2.TodoV2;
import lostark.todo.service.CharacterService;
import lostark.todo.service.ContentService;
import lostark.todo.service.MarketService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/v2/character/cube")
@Api(tags = {"캐릭터 API V2 - 큐브 컨텐츠"})
public class CubeContentApiController {

    private final ContentService contentService;
    private final MarketService marketService;

    @ApiOperation(value = "큐브 컨텐츠 호출")
    @GetMapping("/{name}")
    public ResponseEntity getCubeContent(@AuthenticationPrincipal String username,
                                         @PathVariable String name) {

        CubeContent cubeContent = contentService.findCubeContent(name);
        Market jewelry = marketService.findByName("1레벨");
        log.info(cubeContent.toString());
        return new ResponseEntity(new CubeContentDto().toDto(cubeContent, jewelry), HttpStatus.OK);
    }

}
