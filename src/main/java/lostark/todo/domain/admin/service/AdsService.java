package lostark.todo.domain.admin.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lostark.todo.domain.admin.dto.AdminAdsSearchParams;
import lostark.todo.domain.admin.dto.AdminAdsSearchResponse;
import lostark.todo.domain.member.repository.AdsRepository;
import lostark.todo.global.dto.CursorResponse;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class AdsService {

    private final AdsRepository adsRepository;

    public CursorResponse<AdminAdsSearchResponse> search(AdminAdsSearchParams params, PageRequest pageRequest) {
        return adsRepository.search(params, pageRequest);
    }
}
