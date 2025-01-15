package lostark.todo.domain.admin.api;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lostark.todo.domain.admin.dto.AdminAdsSearchParams;
import lostark.todo.domain.admin.service.AdsService;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/admin/api/v1/ads")
@Api(tags = {"어드민 - 후원 관련 처리"})
public class AdminAdsApi {

    private final AdsService adsService;

    @ApiOperation(value = "후원 목록 출력")
    @GetMapping("")
    public ResponseEntity<?> search(@AuthenticationPrincipal String username,
                                    @Valid AdminAdsSearchParams params,
                                    @RequestParam(required = false, defaultValue = "20") int limit) {
        PageRequest pageRequest = PageRequest.of(0, limit);
        return new ResponseEntity<>(adsService.search(params, pageRequest), HttpStatus.OK);
    }
}
