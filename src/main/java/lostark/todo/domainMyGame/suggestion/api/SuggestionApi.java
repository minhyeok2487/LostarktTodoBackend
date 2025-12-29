package lostark.todo.domainMyGame.suggestion.api;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lostark.todo.domainMyGame.common.dto.ApiResponse;
import lostark.todo.domainMyGame.suggestion.dto.SuggestionRequest;
import lostark.todo.domainMyGame.suggestion.dto.SuggestionResponse;
import lostark.todo.domainMyGame.suggestion.service.SuggestionService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api/v1/suggestions")
@Api(tags = {"유저 제보 API"})
public class SuggestionApi {

    private final SuggestionService suggestionService;

    @ApiOperation(value = "제보 등록", response = ApiResponse.class)
    @PostMapping
    public ResponseEntity<?> createSuggestion(
            @ApiParam(value = "제보 정보", required = true) @Valid @RequestBody SuggestionRequest request) {

        SuggestionResponse suggestion = suggestionService.createSuggestion(request);

        return new ResponseEntity<>(
                ApiResponse.success(suggestion),
                HttpStatus.CREATED
        );
    }
}
