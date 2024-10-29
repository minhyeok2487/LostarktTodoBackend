package lostark.todo.domainV2.board.community.service;

import lombok.RequiredArgsConstructor;
import lostark.todo.domain.member.Member;
import lostark.todo.domainV2.board.community.dao.CommunityDao;
import lostark.todo.domainV2.board.community.dao.CommunityImagesDao;
import lostark.todo.domainV2.board.community.dto.CommunityResponse;
import lostark.todo.domainV2.board.community.dto.CommunitySaveRequest;
import lostark.todo.domainV2.board.community.dto.CommunitySearchParams;
import lostark.todo.domainV2.board.community.entity.Community;
import lostark.todo.domainV2.member.dao.MemberDao;
import lostark.todo.global.config.TokenProvider;
import lostark.todo.global.customAnnotation.RateLimit;
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
    private final CommunityImagesDao communityImagesDao;
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

    @RateLimit(120)
    @Transactional
    public void save(String username, CommunitySaveRequest request) {
        Member member = memberDao.get(username);
        Community save = communityDao.save(Community.toEntity(member, request));
        if(!request.getImageList().isEmpty()) {
            communityImagesDao.updateAll(save.getId(), request.getImageList());
        }
    }
}
