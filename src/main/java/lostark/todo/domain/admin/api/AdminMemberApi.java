package lostark.todo.domain.admin.api;

import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lostark.todo.domain.admin.dto.AdminMemberDetailResponse;
import lostark.todo.domain.admin.dto.SearchAdminMemberRequest;
import lostark.todo.domain.admin.dto.SearchAdminMemberResponse;
import lostark.todo.domain.member.entity.Member;
import lostark.todo.domain.member.service.MemberService;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/api/v1/members")
@RequiredArgsConstructor
public class AdminMemberApi {

    private final MemberService memberService;

    @ApiOperation(value = "어드민 회원 목록 조회 API",
            response = SearchAdminMemberResponse.class)
    @GetMapping
    public ResponseEntity<?> search(SearchAdminMemberRequest request,
                                    @RequestParam(required = false, defaultValue = "1") int page,
                                    @RequestParam(required = false, defaultValue = "25") int limit) {
        PageRequest pageRequest = PageRequest.of(page - 1, limit);
        PageImpl<SearchAdminMemberResponse> memberList = memberService.searchAdminMember(request, pageRequest);
        return new ResponseEntity<>(memberList, HttpStatus.OK);
    }

    @ApiOperation(value = "어드민 회원 상세 조회 API",
            response = AdminMemberDetailResponse.class)
    @GetMapping("/{memberId}")
    public ResponseEntity<?> getDetail(@PathVariable Long memberId) {
        Member member = memberService.get(memberId);
        return new ResponseEntity<>(AdminMemberDetailResponse.from(member), HttpStatus.OK);
    }
}