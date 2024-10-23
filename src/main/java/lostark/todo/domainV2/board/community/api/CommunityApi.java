package lostark.todo.domainV2.board.community.api;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lostark.todo.domainV2.board.community.dto.CommunityResponse;
import lostark.todo.domainV2.board.community.dto.CommunitySearchParams;
import lostark.todo.domainV2.board.community.entity.CommunityCategory;
import lostark.todo.domainV2.board.community.service.CommunityService;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.Arrays;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api/v1/community")
@Api(tags = {"커뮤니티 API"})
public class CommunityApi {

    private final CommunityService service;

    @ApiOperation(value = "커뮤니티 카테고리 목록")
    @GetMapping("/category")
    public ResponseEntity<?> getCommunityCategory() {
        return new ResponseEntity<>(Arrays.stream(CommunityCategory.values())
                .map(CommunityCategory::getCategoryName)
                .collect(Collectors.toList()), HttpStatus.OK);
    }

    @ApiOperation(value = "게시글 불러오기 (커서 기반)", response = CommunityResponse.class)
    @GetMapping()
    public ResponseEntity<?> search(@Valid CommunitySearchParams params,
                                    @RequestParam(required = false, defaultValue = "20") int limit) {
        PageRequest pageRequest = PageRequest.of(0, limit);
        return new ResponseEntity<>(service.search(params, pageRequest), HttpStatus.OK);
    }

}
