package lostark.todo.controller.apiV4.cube;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lostark.todo.controller.dto.characterDto.CharacterDefaultDto;
import lostark.todo.controller.dto.contentDto.CubeContentDto;
import lostark.todo.controller.dtoV2.cube.CubeResponse;
import lostark.todo.controller.dtoV2.cube.CubeUpdateRequest;
import lostark.todo.domain.character.Character;
import lostark.todo.domain.content.CubeContent;
import lostark.todo.domain.cube.Cubes;
import lostark.todo.domain.market.Market;
import lostark.todo.service.CharacterService;
import lostark.todo.service.ContentService;
import lostark.todo.service.CubesService;
import lostark.todo.service.MarketService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/v4/cube")
@Api(tags = {"큐브 API"})
public class CubeControllerV4 {

    private final CubesService cubesService;
    private final CharacterService characterService;
    private final MarketService marketService;
    private final ContentService contentService;

    @ApiOperation(value = "큐브 통계 데이터 출력", response = CubeContentDto.class)
    @GetMapping("/statistics")
    public ResponseEntity<List<CubeContentDto>> getStatistics() {
        Map<String, Market> jewelryMap = marketService.findByNameIn(List.of("3티어 1레벨 보석", "4티어 1레벨 보석"))
                .stream()
                .collect(Collectors.toMap(Market::getName, market -> market));

        List<CubeContent> cubeContentList = contentService.findAllCubeContent();
        List<CubeContentDto> result = cubeContentList.stream()
                .map(content -> {
                    String tierName = content.getLevel() >= 1640 ? "4티어 1레벨 보석" : "3티어 1레벨 보석";
                    Market market = jewelryMap.get(tierName);

                    if (market == null) {
                        throw new IllegalArgumentException("해당하는 보석이 없습니다: " + tierName);
                    }

                    return new CubeContentDto().toDto(content, market);
                })
                .collect(Collectors.toList());

        return new ResponseEntity<>(result, HttpStatus.OK);
    }


    @ApiOperation(value = "큐브 컨텐츠 출력", response = CubeResponse.class)
    @GetMapping()
    public ResponseEntity<?> get(@AuthenticationPrincipal String username) {
        List<CubeResponse> cubeResponseList = cubesService.get(username);
        return new ResponseEntity<>(cubeResponseList, HttpStatus.OK);
    }

    @ApiOperation(value = "큐브 컨텐츠 추가", response = CubeResponse.class)
    @PostMapping()
    public ResponseEntity<?> create(@AuthenticationPrincipal String username, @RequestBody CharacterDefaultDto dto) {
        Character character = characterService.get(dto.getCharacterId(), username);
        Cubes cubes = cubesService.create(character.getId());
        CubeResponse cubeResponse = new CubeResponse(character, cubes);
        return new ResponseEntity<>(cubeResponse, HttpStatus.OK);
    }

    @ApiOperation(value = "큐브 티켓 숫자 변경", response = CubeResponse.class)
    @PutMapping()
    public ResponseEntity<?> update(@AuthenticationPrincipal String username, @RequestBody CubeUpdateRequest request) {
        Character character = characterService.get(request.getCharacterId(), username);
        Cubes cubes = cubesService.update(request);
        CubeResponse cubeResponse = new CubeResponse(character, cubes);
        return new ResponseEntity<>(cubeResponse, HttpStatus.OK);
    }

    @ApiOperation(value = "캐릭터 삭제")
    @DeleteMapping("/{characterId}")
    public ResponseEntity<?> delete(@AuthenticationPrincipal String username, @PathVariable Long characterId) {
        Character character = characterService.get(characterId, username);
        cubesService.delete(character.getId());
        return new ResponseEntity<>(HttpStatus.OK);
    }

}
