package lostark.todo.domainMyGame.mygame.api;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lostark.todo.domainMyGame.common.dto.ApiResponse;
import lostark.todo.domainMyGame.common.dto.PaginationResponse;
import lostark.todo.domainMyGame.mygame.dto.MyGameResponse;
import lostark.todo.domainMyGame.mygame.service.MyGameService;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api/v1/games")
@Api(tags = {"게임 API"})
public class MyGameApi {

    private final MyGameService myGameService;

    @ApiOperation(value = "게임 목록 조회", response = ApiResponse.class)
    @GetMapping
    public ResponseEntity<?> getGames(
            @ApiParam(value = "검색 키워드") @RequestParam(required = false) String search,
            @ApiParam(value = "페이지 번호 (기본값: 1)") @RequestParam(defaultValue = "1") int page,
            @ApiParam(value = "페이지당 항목 수 (기본값: 20)") @RequestParam(defaultValue = "20") int limit) {

        Page<MyGameResponse> gamesPage = myGameService.searchGames(search, page, limit);
        PaginationResponse pagination = PaginationResponse.from(gamesPage);

        return new ResponseEntity<>(
                ApiResponse.success(gamesPage.getContent(), pagination),
                HttpStatus.OK
        );
    }

    @ApiOperation(value = "게임 상세 조회", response = ApiResponse.class)
    @GetMapping("/{id}")
    public ResponseEntity<?> getGameById(
            @ApiParam(value = "게임 ID", required = true) @PathVariable String id) {

        MyGameResponse game = myGameService.getGameById(id);

        return new ResponseEntity<>(
                ApiResponse.success(game),
                HttpStatus.OK
        );
    }

    @ApiOperation(value = "전체 게임 목록 조회", response = ApiResponse.class)
    @GetMapping("/all")
    public ResponseEntity<?> getAllGames() {
        List<MyGameResponse> games = myGameService.getAllGames();

        return new ResponseEntity<>(
                ApiResponse.success(games),
                HttpStatus.OK
        );
    }
}
