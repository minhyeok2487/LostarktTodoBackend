package lostark.todo.domainV2.util.cube.api;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lostark.todo.controller.dto.characterDto.CharacterDefaultDto;
import lostark.todo.controller.dto.contentDto.CubeContentDto;
import lostark.todo.domainV2.util.cube.dto.CubeResponse;
import lostark.todo.domainV2.util.cube.dto.CubeUpdateRequest;
import lostark.todo.domain.character.Character;
import lostark.todo.domainV2.util.cube.entity.Cubes;
import lostark.todo.service.CharacterService;
import lostark.todo.domainV2.util.cube.service.CubesService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api/v1/cube")
@Api(tags = {"큐브 API"})
public class CubeApi {

    private final CubesService cubesService;
    private final CharacterService characterService;

    @ApiOperation(value = "큐브 통계 데이터 출력", response = CubeContentDto.class)
    @GetMapping("/statistics")
    public ResponseEntity<?> getStatistics() {
        return new ResponseEntity<>(cubesService.getStatistics(), HttpStatus.OK);
    }

    @ApiOperation(value = "큐브 컨텐츠 출력", response = CubeResponse.class)
    @GetMapping()
    public ResponseEntity<?> get(@AuthenticationPrincipal String username) {
        return new ResponseEntity<>(cubesService.get(username), HttpStatus.OK);
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
