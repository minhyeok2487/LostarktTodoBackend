package lostark.todo.controller.apiV2.characterApi;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lostark.todo.controller.dto.contentDto.CubeContentDto;
import lostark.todo.domain.content.CubeContent;
import lostark.todo.domain.market.Market;
import lostark.todo.domainV2.util.content.service.ContentService;
import lostark.todo.domainV2.util.market.service.MarketService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;


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
    public ResponseEntity<?> getCubeContent(@AuthenticationPrincipal String username,
                                         @PathVariable String name) {
        CubeContent cubeContent = contentService.findCubeContent(name);
        Market jewelry;
        if (cubeContent.getLevel() >= 1640) {
            jewelry = marketService.findByName("4티어 1레벨 보석");
        } else {
            jewelry = marketService.findByName("3티어 1레벨 보석");
        }
        return new ResponseEntity<>(new CubeContentDto().toDto(cubeContent, jewelry), HttpStatus.OK);
    }

}
