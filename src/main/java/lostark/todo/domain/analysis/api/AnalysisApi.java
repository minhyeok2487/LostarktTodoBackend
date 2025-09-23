package lostark.todo.domain.analysis.api;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lostark.todo.domain.analysis.dto.UpdateAnalysisRequest;
import lostark.todo.domain.analysis.service.AnalysisService;
import lostark.todo.domain.character.entity.Character;
import lostark.todo.domain.character.service.CharacterService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/v1/analysis")
@Api(tags = {"전투분석 콘텐츠 API"})
public class AnalysisApi {

    private final AnalysisService analysisService;
    private final CharacterService characterService;

    @ApiOperation(value = "전투 분석 기록하기 API")
    @PostMapping()
    public ResponseEntity<?> updateAnalysis(@AuthenticationPrincipal String username,
                                            @RequestBody UpdateAnalysisRequest request) {
        Character character = characterService.get(request.getCharacterId(), username);
        analysisService.updateAnalysis(character, request);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
