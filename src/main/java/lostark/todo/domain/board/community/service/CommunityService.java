package lostark.todo.domain.board.community.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lostark.todo.controller.dtoV2.image.ImageResponse;
import lostark.todo.domain.member.enums.Role;
import lostark.todo.domain.board.community.entity.CommunityLike;
import lostark.todo.domain.board.community.repository.CommunityImagesRepository;
import lostark.todo.domain.board.community.repository.CommunityLikeRepository;
import lostark.todo.domain.member.entity.Member;
import lostark.todo.domain.member.repository.MemberRepository;
import lostark.todo.domain.notification.entity.Notification;
import lostark.todo.domain.notification.repository.NotificationRepository;
import lostark.todo.domain.board.community.dto.*;
import lostark.todo.domain.board.community.entity.Community;
import lostark.todo.domain.board.community.entity.CommunityCategory;
import lostark.todo.domain.board.community.entity.CommunityImages;
import lostark.todo.domain.board.community.repository.CommunityRepository;
import lostark.todo.global.customAnnotation.RateLimit;
import lostark.todo.global.dto.CursorResponse;
import lostark.todo.global.dto.ImageResponseV2;
import lostark.todo.global.service.ImagesService;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class CommunityService {

    private final CommunityRepository communityRepository;
    private final CommunityLikeRepository communityLikeRepository;
    private final CommunityImagesRepository communityImagesRepository;
    private final MemberRepository memberRepository;
    private final ImagesService imagesService;
    private final NotificationRepository notificationRepository;

    @Transactional(readOnly = true)
    public CursorResponse<CommunitySearchResponse> search(String username, CommunitySearchParams params, PageRequest pageRequest) {
        long memberId = username == null ? 0L : memberRepository.get(username).getId();
        return communityRepository.search(memberId, params, pageRequest);
    }

    @RateLimit()
    @Transactional
    public void save(String username, CommunitySaveRequest request) {
        Member member = memberRepository.get(username);
        validateSaveRequest(member, request);
        Community community = communityRepository.save(Community.toEntity(member, request));

        Optional.of(request.getImageList())
                .filter(list -> !list.isEmpty())
                .ifPresent(images -> updateAll(community.getId(), images));

        Optional.of(request)
                .filter(req -> req.getRootParentId() != 0L)
                .map(req -> {
                    Community rootCommunity = communityRepository.get(req.getRootParentId());
                    Member receiver = req.getCommentParentId() == 0L
                            ? rootCommunity.getMemberId()
                            : communityRepository.get(req.getCommentParentId()).getMemberId();
                    return Notification.createReplyNotification(rootCommunity, receiver);
                })
                .ifPresent(notificationRepository::save);

    }

    private void updateAll(long communityId, List<Long> imageList) {
        AtomicInteger counter = new AtomicInteger(1);
        communityImagesRepository.search(imageList)
                .forEach(image -> image.update(communityId, counter.getAndIncrement()));
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
        memberRepository.get(username); // 단순 회원 검증용
        String folderName = "community-images/";
        ImageResponse imageResponse = imagesService.upload(image, folderName);
        CommunityImages save = communityImagesRepository.save(
                CommunityImages.builder()
                        .fileName(imageResponse.getFileName())
                        .url(imageResponse.getImageUrl())
                        .ordering(0)
                        .build());
        return new ImageResponseV2(imageResponse, save.getId());
    }

    @Transactional
    public void update(String username, CommunityUpdateRequest request) {
        Community community = communityRepository.get(username, request.getCommunityId());
        community.update(request.getBody());
    }

    @Transactional
    public void delete(String username, long communityId) {
        Community community = communityRepository.get(username, communityId);
        community.delete();
    }

    @Transactional(readOnly = true)
    public CommunityGetResponse get(String username, Long communityId) {
        long memberId = username == null ? 0L : memberRepository.get(username).getId();
        CommunitySearchResponse searchResponse = communityRepository.getResponse(memberId, communityId);
        List<CommunityCommentResponse> commentResponseList = communityRepository.getComments(memberId, communityId);
        return new CommunityGetResponse(searchResponse, commentResponseList);
    }

    @Transactional
    public void updateLike(String username, long communityId) {
        Member member = memberRepository.get(username);
        Optional<CommunityLike> communityLike = communityLikeRepository.findByCommunityIdAndMemberId(communityId, member.getId());
        if (communityLike.isPresent()) {
            communityLikeRepository.delete(communityLike.get());
        } else {
            communityLikeRepository.save(CommunityLike.builder().communityId(communityId).memberId(member.getId()).build());
        }
    }
}
