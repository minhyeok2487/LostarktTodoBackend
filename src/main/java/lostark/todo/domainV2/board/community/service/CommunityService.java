package lostark.todo.domainV2.board.community.service;

import lombok.RequiredArgsConstructor;
import lostark.todo.controller.dtoV2.image.ImageResponse;
import lostark.todo.domain.Role;
import lostark.todo.domain.member.Member;
import lostark.todo.domainV2.board.community.dao.CommunityDao;
import lostark.todo.domainV2.board.community.dao.CommunityImagesDao;
import lostark.todo.domainV2.board.community.dto.CommunityResponse;
import lostark.todo.domainV2.board.community.dto.CommunitySaveRequest;
import lostark.todo.domainV2.board.community.dto.CommunitySearchParams;
import lostark.todo.domainV2.board.community.dto.CommunityUpdateRequest;
import lostark.todo.domainV2.board.community.entity.Community;
import lostark.todo.domainV2.board.community.entity.CommunityCategory;
import lostark.todo.domainV2.board.community.entity.CommunityImages;
import lostark.todo.domainV2.member.dao.MemberDao;
import lostark.todo.global.config.TokenProvider;
import lostark.todo.global.dto.CursorResponse;
import lostark.todo.global.dto.ImageResponseV2;
import lostark.todo.service.ImagesService;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
@Transactional
public class CommunityService {

    private final CommunityDao communityDao;
    private final CommunityImagesDao communityImagesDao;
    private final MemberDao memberDao;
    private final TokenProvider tokenProvider;
    private final ImagesService imagesService;

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

//    @RateLimit(120)
    @Transactional
    public void save(String username, CommunitySaveRequest request) {
        Member member = memberDao.get(username);
        validateSaveRequest(member, request);
        Community save = communityDao.save(Community.toEntity(member, request));
        if (!request.getImageList().isEmpty()) {
            communityImagesDao.updateAll(save.getId(), request.getImageList());
        }
    }

    private void validateSaveRequest(Member member, CommunitySaveRequest request) {
        if ((request.getCategory().equals(CommunityCategory.BOARDS)) &&
                (!member.getRole().equals(Role.ADMIN))) {
            throw new IllegalArgumentException("공지사항은 관리자만 올릴 수 있습니다.");
        }
    }

    @Transactional
    public ImageResponseV2 uploadImage(String username, MultipartFile image) {
        memberDao.get(username); // 단순 회원 검증용
        String folderName = "community-images/";
        ImageResponse imageResponse = imagesService.upload(image, folderName);
        CommunityImages images = communityImagesDao.uploadImage(imageResponse);
        return new ImageResponseV2(imageResponse, images.getId());
    }

    @Transactional
    public void update(String username, CommunityUpdateRequest request) {
        Community community = communityDao.get(username, request.getCommunityId());
        community.update(request.getBody());
    }
}
