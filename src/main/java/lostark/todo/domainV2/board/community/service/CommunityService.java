package lostark.todo.domainV2.board.community.service;

import lombok.RequiredArgsConstructor;
import lostark.todo.domainV2.board.community.dao.CommunityDao;
import lostark.todo.domainV2.board.community.dto.CommunityResponse;
import lostark.todo.domainV2.board.community.dto.CommunitySearchParams;
import lostark.todo.domainV2.member.dao.MemberDao;
import lostark.todo.global.config.TokenProvider;
import lostark.todo.global.dto.CursorResponse;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
@RequiredArgsConstructor
@Transactional
public class CommunityService {

    private final CommunityDao communityDao;
    private final MemberDao memberDao;
    private final TokenProvider tokenProvider;

    @Transactional(readOnly = true)
    public CursorResponse<CommunityResponse> search(CommunitySearchParams params, PageRequest pageRequest) {
        Long memberId = getMemberIdFromToken(params.getToken());
        return communityDao.search(memberId, params, pageRequest);
    }

    private Long getMemberIdFromToken(String token) {
        if (!StringUtils.hasText(token)) {
            return 0L;
        }
        String username = tokenProvider.validToken(token);
        return memberDao.get(username).getId();
    }
}
