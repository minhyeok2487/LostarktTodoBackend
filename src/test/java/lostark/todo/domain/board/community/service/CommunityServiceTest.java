package lostark.todo.domain.board.community.service;

import lostark.todo.domain.board.community.dto.*;
import lostark.todo.domain.board.community.entity.Community;
import lostark.todo.domain.board.community.entity.CommunityCategory;
import lostark.todo.domain.board.community.entity.CommunityImages;
import lostark.todo.domain.board.community.entity.CommunityLike;
import lostark.todo.domain.board.community.repository.CommunityImagesRepository;
import lostark.todo.domain.board.community.repository.CommunityLikeRepository;
import lostark.todo.domain.board.community.repository.CommunityRepository;
import lostark.todo.domain.character.entity.Character;
import lostark.todo.domain.member.entity.Member;
import lostark.todo.domain.member.enums.Role;
import lostark.todo.domain.member.repository.MemberRepository;
import lostark.todo.domain.notification.entity.Notification;
import lostark.todo.domain.notification.repository.NotificationRepository;
import lostark.todo.global.dto.CursorResponse;
import lostark.todo.global.dto.ImageResponse;
import lostark.todo.global.dto.ImageResponseV2;
import lostark.todo.global.exhandler.exceptions.ConditionNotMetException;
import lostark.todo.global.service.ImagesService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CommunityServiceTest {

    @Mock
    private CommunityRepository communityRepository;

    @Mock
    private CommunityLikeRepository communityLikeRepository;

    @Mock
    private CommunityImagesRepository communityImagesRepository;

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private ImagesService imagesService;

    @Mock
    private NotificationRepository notificationRepository;

    @InjectMocks
    private CommunityService communityService;

    private Member testMember;
    private Member adminMember;

    @BeforeEach
    void setUp() {
        Character mainCharacter = Character.builder()
                .characterName("테스트캐릭터")
                .characterClassName("버서커")
                .characterImage("image.png")
                .build();

        testMember = Member.builder()
                .id(1L)
                .username("test@test.com")
                .mainCharacter("테스트캐릭터")
                .role(Role.USER)
                .characters(new ArrayList<>(List.of(mainCharacter)))
                .build();

        adminMember = Member.builder()
                .id(2L)
                .username("admin@test.com")
                .mainCharacter("관리자캐릭터")
                .role(Role.ADMIN)
                .characters(new ArrayList<>(List.of(
                        Character.builder()
                                .characterName("관리자캐릭터")
                                .characterClassName("바드")
                                .build()
                )))
                .build();
    }

    private CommunitySaveRequest createSaveRequest(CommunityCategory category, long rootParentId, long commentParentId) {
        CommunitySaveRequest request = new CommunitySaveRequest();
        request.setBody("테스트 내용");
        request.setCategory(category);
        request.setShowName(true);
        request.setImageList(Collections.emptyList());
        request.setRootParentId(rootParentId);
        request.setCommentParentId(commentParentId);
        return request;
    }

    @Nested
    @DisplayName("search 메서드")
    class SearchTest {

        @Test
        @DisplayName("성공 - 로그인 사용자 조회")
        void success_loggedInUser() {
            // given
            given(memberRepository.get("test@test.com")).willReturn(testMember);
            CursorResponse<CommunitySearchResponse> expectedResponse = new CursorResponse<>(Collections.emptyList(), false);
            given(communityRepository.search(eq(1L), any(CommunitySearchParams.class), any(PageRequest.class)))
                    .willReturn(expectedResponse);

            CommunitySearchParams params = new CommunitySearchParams();
            PageRequest pageRequest = PageRequest.of(0, 10);

            // when
            CursorResponse<CommunitySearchResponse> result = communityService.search("test@test.com", params, pageRequest);

            // then
            assertThat(result).isNotNull();
            verify(memberRepository).get("test@test.com");
        }

        @Test
        @DisplayName("성공 - 비로그인 사용자 조회")
        void success_anonymousUser() {
            // given
            CursorResponse<CommunitySearchResponse> expectedResponse = new CursorResponse<>(Collections.emptyList(), false);
            given(communityRepository.search(eq(0L), any(CommunitySearchParams.class), any(PageRequest.class)))
                    .willReturn(expectedResponse);

            CommunitySearchParams params = new CommunitySearchParams();
            PageRequest pageRequest = PageRequest.of(0, 10);

            // when
            CursorResponse<CommunitySearchResponse> result = communityService.search(null, params, pageRequest);

            // then
            assertThat(result).isNotNull();
            verify(memberRepository, never()).get(anyString());
        }
    }

    @Nested
    @DisplayName("save 메서드")
    class SaveTest {

        @Test
        @DisplayName("성공 - 일반 게시글 저장")
        void success_savePost() {
            // given
            given(memberRepository.get("test@test.com")).willReturn(testMember);
            CommunitySaveRequest request = createSaveRequest(CommunityCategory.LIFE, 0, 0);

            Community savedCommunity = Community.builder()
                    .id(1L)
                    .memberId(testMember)
                    .body("테스트 내용")
                    .category(CommunityCategory.LIFE)
                    .build();
            given(communityRepository.save(any(Community.class))).willReturn(savedCommunity);

            // when
            communityService.save("test@test.com", request);

            // then
            verify(communityRepository).save(any(Community.class));
        }

        @Test
        @DisplayName("성공 - 이미지 포함 게시글 저장")
        void success_savePostWithImages() {
            // given
            given(memberRepository.get("test@test.com")).willReturn(testMember);
            CommunitySaveRequest request = createSaveRequest(CommunityCategory.LIFE, 0, 0);
            request.setImageList(List.of(1L, 2L));

            Community savedCommunity = Community.builder()
                    .id(1L)
                    .memberId(testMember)
                    .body("테스트 내용")
                    .category(CommunityCategory.LIFE)
                    .build();
            given(communityRepository.save(any(Community.class))).willReturn(savedCommunity);

            CommunityImages image1 = CommunityImages.builder().id(1L).build();
            CommunityImages image2 = CommunityImages.builder().id(2L).build();
            given(communityImagesRepository.search(List.of(1L, 2L))).willReturn(List.of(image1, image2));

            // when
            communityService.save("test@test.com", request);

            // then
            verify(communityImagesRepository).search(List.of(1L, 2L));
        }

        @Test
        @DisplayName("성공 - 댓글 저장 및 알림 생성")
        void success_saveCommentWithNotification() {
            // given
            given(memberRepository.get("test@test.com")).willReturn(testMember);
            CommunitySaveRequest request = createSaveRequest(CommunityCategory.LIFE, 1L, 0);

            Community savedCommunity = Community.builder()
                    .id(2L)
                    .memberId(testMember)
                    .body("댓글 내용")
                    .category(CommunityCategory.LIFE)
                    .rootParentId(1L)
                    .build();
            given(communityRepository.save(any(Community.class))).willReturn(savedCommunity);

            Community rootCommunity = Community.builder()
                    .id(1L)
                    .memberId(adminMember)
                    .body("원글 내용")
                    .build();
            given(communityRepository.get(1L)).willReturn(rootCommunity);

            // when
            communityService.save("test@test.com", request);

            // then
            verify(notificationRepository).save(any(Notification.class));
        }

        @Test
        @DisplayName("성공 - 관리자의 공지사항 저장")
        void success_adminSaveBoards() {
            // given
            given(memberRepository.get("admin@test.com")).willReturn(adminMember);
            CommunitySaveRequest request = createSaveRequest(CommunityCategory.BOARDS, 0, 0);

            Community savedCommunity = Community.builder()
                    .id(1L)
                    .memberId(adminMember)
                    .body("공지사항 내용")
                    .category(CommunityCategory.BOARDS)
                    .build();
            given(communityRepository.save(any(Community.class))).willReturn(savedCommunity);

            // when
            communityService.save("admin@test.com", request);

            // then
            verify(communityRepository).save(any(Community.class));
        }

        @Test
        @DisplayName("실패 - 일반 사용자의 공지사항 저장 시도")
        void fail_userSaveBoards() {
            // given
            given(memberRepository.get("test@test.com")).willReturn(testMember);
            CommunitySaveRequest request = createSaveRequest(CommunityCategory.BOARDS, 0, 0);

            // when & then
            assertThatThrownBy(() -> communityService.save("test@test.com", request))
                    .isInstanceOf(ConditionNotMetException.class)
                    .hasMessageContaining("공지사항은 관리자만");
        }

        @Test
        @DisplayName("실패 - 댓글 답글인데 rootParentId가 없음")
        void fail_commentReplyWithoutRootParent() {
            // given
            given(memberRepository.get("test@test.com")).willReturn(testMember);
            CommunitySaveRequest request = createSaveRequest(CommunityCategory.LIFE, 0, 1L);

            // when & then
            assertThatThrownBy(() -> communityService.save("test@test.com", request))
                    .isInstanceOf(ConditionNotMetException.class)
                    .hasMessageContaining("댓글 ID가 필요합니다");
        }
    }

    @Nested
    @DisplayName("uploadImage 메서드")
    class UploadImageTest {

        @Test
        @DisplayName("성공 - 이미지 업로드")
        void success() {
            // given
            given(memberRepository.get("test@test.com")).willReturn(testMember);
            MultipartFile mockFile = mock(MultipartFile.class);

            ImageResponse imageResponse = new ImageResponse("test.png", "http://example.com/test.png");
            given(imagesService.upload(mockFile, "community-images/")).willReturn(imageResponse);

            CommunityImages savedImage = CommunityImages.builder()
                    .id(1L)
                    .fileName("test.png")
                    .url("http://example.com/test.png")
                    .build();
            given(communityImagesRepository.save(any(CommunityImages.class))).willReturn(savedImage);

            // when
            ImageResponseV2 result = communityService.uploadImage("test@test.com", mockFile);

            // then
            assertThat(result).isNotNull();
            assertThat(result.getImageId()).isEqualTo(1L);
        }
    }

    @Nested
    @DisplayName("update 메서드")
    class UpdateTest {

        @Test
        @DisplayName("성공 - 게시글 수정 (15분 이내)")
        void success_updateWithin15Minutes() {
            // given
            Community community = Community.builder()
                    .id(1L)
                    .memberId(testMember)
                    .body("원래 내용")
                    .build();
            // CreatedDate를 현재 시간으로 설정하기 위해 spy 사용
            Community spyCommunity = spy(community);
            given(spyCommunity.getCreatedDate()).willReturn(LocalDateTime.now().minusMinutes(5));

            given(communityRepository.get("test@test.com", 1L)).willReturn(spyCommunity);

            CommunityUpdateRequest request = new CommunityUpdateRequest();
            request.setCommunityId(1L);
            request.setBody("수정된 내용");

            // when
            communityService.update("test@test.com", request);

            // then
            verify(spyCommunity).update("수정된 내용");
        }

        @Test
        @DisplayName("실패 - 15분 경과 후 수정 시도")
        void fail_updateAfter15Minutes() {
            // given
            Community community = Community.builder()
                    .id(1L)
                    .memberId(testMember)
                    .body("원래 내용")
                    .build();
            Community spyCommunity = spy(community);
            given(spyCommunity.getCreatedDate()).willReturn(LocalDateTime.now().minusMinutes(20));

            given(communityRepository.get("test@test.com", 1L)).willReturn(spyCommunity);

            CommunityUpdateRequest request = new CommunityUpdateRequest();
            request.setCommunityId(1L);
            request.setBody("수정된 내용");

            // when & then
            assertThatThrownBy(() -> communityService.update("test@test.com", request))
                    .isInstanceOf(ConditionNotMetException.class)
                    .hasMessageContaining("15분이 지나");
        }
    }

    @Nested
    @DisplayName("delete 메서드")
    class DeleteTest {

        @Test
        @DisplayName("성공 - 게시글 삭제")
        void success() {
            // given
            Community community = Community.builder()
                    .id(1L)
                    .memberId(testMember)
                    .body("삭제할 내용")
                    .deleted(false)
                    .build();
            given(communityRepository.get("test@test.com", 1L)).willReturn(community);

            // when
            communityService.delete("test@test.com", 1L);

            // then
            assertThat(community.isDeleted()).isTrue();
        }
    }

    @Nested
    @DisplayName("get 메서드")
    class GetTest {

        @Test
        @DisplayName("성공 - 로그인 사용자가 게시글 상세 조회")
        void success_loggedInUser() {
            // given
            given(memberRepository.get("test@test.com")).willReturn(testMember);

            CommunitySearchResponse searchResponse = mock(CommunitySearchResponse.class);
            given(communityRepository.getResponse(1L, 1L)).willReturn(searchResponse);

            List<CommunityCommentResponse> commentResponses = Collections.emptyList();
            given(communityRepository.getComments(1L, 1L)).willReturn(commentResponses);

            // when
            CommunityGetResponse result = communityService.get("test@test.com", 1L);

            // then
            assertThat(result).isNotNull();
        }

        @Test
        @DisplayName("성공 - 비로그인 사용자가 게시글 상세 조회")
        void success_anonymousUser() {
            // given
            CommunitySearchResponse searchResponse = mock(CommunitySearchResponse.class);
            given(communityRepository.getResponse(0L, 1L)).willReturn(searchResponse);

            List<CommunityCommentResponse> commentResponses = Collections.emptyList();
            given(communityRepository.getComments(0L, 1L)).willReturn(commentResponses);

            // when
            CommunityGetResponse result = communityService.get(null, 1L);

            // then
            assertThat(result).isNotNull();
            verify(memberRepository, never()).get(anyString());
        }
    }

    @Nested
    @DisplayName("updateLike 메서드")
    class UpdateLikeTest {

        @Test
        @DisplayName("성공 - 좋아요 추가")
        void success_addLike() {
            // given
            given(memberRepository.get("test@test.com")).willReturn(testMember);
            given(communityLikeRepository.findByCommunityIdAndMemberId(1L, 1L)).willReturn(Optional.empty());

            // when
            communityService.updateLike("test@test.com", 1L);

            // then
            verify(communityLikeRepository).save(any(CommunityLike.class));
            verify(communityLikeRepository, never()).delete(any(CommunityLike.class));
        }

        @Test
        @DisplayName("성공 - 좋아요 취소")
        void success_removeLike() {
            // given
            given(memberRepository.get("test@test.com")).willReturn(testMember);
            CommunityLike existingLike = CommunityLike.builder()
                    .id(1L)
                    .communityId(1L)
                    .memberId(1L)
                    .build();
            given(communityLikeRepository.findByCommunityIdAndMemberId(1L, 1L)).willReturn(Optional.of(existingLike));

            // when
            communityService.updateLike("test@test.com", 1L);

            // then
            verify(communityLikeRepository).delete(existingLike);
            verify(communityLikeRepository, never()).save(any(CommunityLike.class));
        }
    }

    @Nested
    @DisplayName("searchBoards 메서드")
    class SearchBoardsTest {

        @Test
        @DisplayName("성공 - 공지사항 목록 조회")
        void success() {
            // given
            List<Community> boards = List.of(
                    Community.builder().id(1L).category(CommunityCategory.BOARDS).build(),
                    Community.builder().id(2L).category(CommunityCategory.BOARDS).build()
            );
            given(communityRepository.searchBoards()).willReturn(boards);

            // when
            List<Community> result = communityService.searchBoards();

            // then
            assertThat(result).hasSize(2);
        }
    }
}
