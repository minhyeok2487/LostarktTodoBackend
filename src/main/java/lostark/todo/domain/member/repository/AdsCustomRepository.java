package lostark.todo.domain.member.repository;

import lostark.todo.domain.admin.dto.AdminAdsSearchParams;
import lostark.todo.domain.admin.dto.AdminAdsSearchResponse;
import lostark.todo.domain.member.entity.Ads;
import lostark.todo.global.dto.CursorResponse;
import org.springframework.data.domain.PageRequest;

import java.util.Optional;

public interface AdsCustomRepository {

    CursorResponse<AdminAdsSearchResponse> search(AdminAdsSearchParams params, PageRequest pageRequest);

    Optional<Ads> get(String proposerEmail);
}
