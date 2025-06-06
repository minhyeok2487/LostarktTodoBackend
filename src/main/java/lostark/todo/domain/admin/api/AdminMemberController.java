package lostark.todo.domain.admin.api;

import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lostark.todo.domain.admin.dto.SearchAdminMemberRequest;
import lostark.todo.domain.admin.dto.SearchAdminMemberResponse;
import lostark.todo.domain.member.service.MemberService;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/members")
@RequiredArgsConstructor
public class AdminMemberController {

    private final MemberService memberService;

    @ApiOperation(value = "어드민 회원 테이블 조회 API",
            response = SearchAdminMemberResponse.class)
    @GetMapping()
    public ResponseEntity<?> search(SearchAdminMemberRequest request,
                                    @RequestParam(required = false, defaultValue = "1") int page,
                                    @RequestParam(required = false, defaultValue = "25") int limit) {
        PageRequest pageRequest = PageRequest.of(page-1, limit);
        PageImpl<SearchAdminMemberResponse> memberList = memberService.searchAdminMember(request, pageRequest);
        return new ResponseEntity<>(memberList, HttpStatus.OK);
    }
}