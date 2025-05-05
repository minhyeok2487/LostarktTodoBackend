package lostark.todo.domain.admin.api;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lostark.todo.domain.admin.dto.UpdateAdsDateRequest;
import lostark.todo.domain.admin.dto.AdminAdsSearchParams;
import lostark.todo.domain.admin.service.AdsService;
import lostark.todo.domain.member.service.MemberService;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/admin/api/v1/ads")
@Api(tags = {"어드민 - 후원 관련 처리"})
public class AdminAdsApi {

    private final AdsService adsService;
    private final MemberService memberService;

    @ApiOperation(value = "후원 목록 출력")
    @GetMapping("")
    public ResponseEntity<?> search(@Valid AdminAdsSearchParams params,
                                    @RequestParam(required = false, defaultValue = "20") int limit) {
        PageRequest pageRequest = PageRequest.of(0, limit);
        return new ResponseEntity<>(adsService.search(params, pageRequest), HttpStatus.OK);
    }

    @ApiOperation(value = "광고 제거 날짜 변경")
    @PostMapping("/date")
    public ResponseEntity<?> updateAdsDate(@RequestBody UpdateAdsDateRequest request) {
        memberService.updateAdsDate(request);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
