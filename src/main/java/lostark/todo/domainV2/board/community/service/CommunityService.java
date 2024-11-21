package lostark.todo.domainV2.board.community.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lostark.todo.controller.dtoV2.image.ImageResponse;
import lostark.todo.domain.Role;
import lostark.todo.domain.member.Member;
import lostark.todo.domain.notification.Notification;
import lostark.todo.domain.notification.NotificationRepository;
import lostark.todo.domainV2.board.community.dao.CommunityDao;
import lostark.todo.domainV2.board.community.dao.CommunityImagesDao;
import lostark.todo.domainV2.board.community.dao.CommunityLikeDao;
import lostark.todo.domainV2.board.community.dto.*;
import lostark.todo.domainV2.board.community.entity.Community;
import lostark.todo.domainV2.board.community.entity.CommunityCategory;
import lostark.todo.domainV2.board.community.entity.CommunityImages;
import lostark.todo.domainV2.board.community.repository.CommunityRepository;
import lostark.todo.domainV2.member.dao.MemberDao;
import lostark.todo.global.customAnnotation.RateLimit;
import lostark.todo.global.dto.CursorResponse;
import lostark.todo.global.dto.ImageResponseV2;
import lostark.todo.service.ImagesService;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class CommunityService {

    private final CommunityDao communityDao;
    private final CommunityLikeDao communityLikeDao;
    private final CommunityImagesDao communityImagesDao;
    private final MemberDao memberDao;
    private final ImagesService imagesService;
    private final NotificationRepository notificationRepository;
    private final CommunityRepository communityRepository;

    @Transactional(readOnly = true)
    public CursorResponse<CommunitySearchResponse> search(String username, CommunitySearchParams params, PageRequest pageRequest) {
        long memberId = username == null ? 0L : memberDao.get(username).getId();
        return communityDao.search(memberId, params, pageRequest);
    }

    @RateLimit()
    @Transactional
    public void save(String username, CommunitySaveRequest request) {
        Member member = memberDao.get(username);
        validateSaveRequest(member, request);
        Community community = communityRepository.save(Community.toEntity(member, request));

        Optional.of(request.getImageList())
                .filter(list -> !list.isEmpty())
                .ifPresent(images -> communityImagesDao.updateAll(community.getId(), images));

        Optional.of(request.getRootParentId())
                .filter(id -> id != 0L)
                .map(communityRepository::get)
                .map(Notification::createReplyNotification)
                .ifPresent(notificationRepository::save);
    }


    private void validateSaveRequest(Member member, CommunitySaveRequest request) {
        if ((request.getCategory().equals(CommunityCategory.BOARDS)) &&
                (!member.getRole().equals(Role.ADMIN)) && (request.getRootParentId() == 0)) {
            throw new IllegalArgumentException("공지사항은 관리자만 올릴 수 있습니다.");
        }
        if (request.getCommentParentId() != 0 && request.getRootParentId() == 0) {
            throw new IllegalArgumentException("댓글 답글은 댓글 ID가 필요합니다");
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

    @Transactional
    public void delete(String username, long communityId) {
        Community community = communityDao.get(username, communityId);
        community.delete();
    }

    @Transactional(readOnly = true)
    public CommunityGetResponse get(String username, Long communityId) {
        long memberId = username == null ? 0L : memberDao.get(username).getId();
        CommunitySearchResponse searchResponse = communityDao.getResponse(memberId, communityId);
        List<CommunityCommentResponse> commentResponseList = communityDao.getComments(memberId, communityId);
        return new CommunityGetResponse(searchResponse, commentResponseList);
    }

    @Transactional
    public void updateLike(String username, long communityId) {
        Member member = memberDao.get(username);
        communityLikeDao.updateLike(member.getId(), communityId);
    }
}
