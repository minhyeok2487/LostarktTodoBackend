package lostark.todo.domain.member.repository;

import lostark.todo.domain.admin.dto.AdminAdsSearchParams;
import lostark.todo.domain.admin.dto.AdminAdsSearchResponse;
import lostark.todo.global.dto.CursorResponse;
import org.springframework.data.domain.PageRequest;

public interface AdsCustomRepository {

    CursorResponse<AdminAdsSearchResponse> search(AdminAdsSearchParams params, PageRequest pageRequest);
}
