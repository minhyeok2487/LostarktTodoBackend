package lostark.todo.domain.generaltodo.service;

import lostark.todo.domain.generaltodo.dto.*;
import lostark.todo.domain.generaltodo.entity.GeneralTodoCategory;
import lostark.todo.domain.generaltodo.entity.GeneralTodoFolder;
import lostark.todo.domain.generaltodo.entity.GeneralTodoItem;
import lostark.todo.domain.generaltodo.entity.GeneralTodoStatus;
import lostark.todo.domain.generaltodo.enums.GeneralTodoViewMode;
import lostark.todo.domain.generaltodo.repository.GeneralTodoCategoryRepository;
import lostark.todo.domain.generaltodo.repository.GeneralTodoFolderRepository;
import lostark.todo.domain.generaltodo.repository.GeneralTodoItemRepository;
import lostark.todo.domain.generaltodo.repository.GeneralTodoStatusRepository;
import lostark.todo.domain.member.entity.Member;
import lostark.todo.domain.member.repository.MemberRepository;
import lostark.todo.global.exhandler.exceptions.ConditionNotMetException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
@org.mockito.junit.jupiter.MockitoSettings(strictness = org.mockito.quality.Strictness.LENIENT)
class GeneralTodoServiceTest {

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private GeneralTodoFolderRepository folderRepository;

    @Mock
    private GeneralTodoCategoryRepository categoryRepository;

    @Mock
    private GeneralTodoItemRepository itemRepository;

    @Mock
    private GeneralTodoStatusRepository statusRepository;

    @InjectMocks
    private GeneralTodoService generalTodoService;

    private Member testMember;
    private GeneralTodoFolder testFolder;
    private GeneralTodoCategory testCategory;
    private GeneralTodoStatus testStatus;
    private GeneralTodoItem testItem;

    @BeforeEach
    void setUp() {
        testMember = Member.builder()
                .id(1L)
                .username("test@test.com")
                .build();

        testFolder = GeneralTodoFolder.builder()
                .id(1L)
                .member(testMember)
                .name("테스트 폴더")
                .sortOrder(0)
                .categories(new ArrayList<>())
                .build();

        testCategory = GeneralTodoCategory.builder()
                .id(1L)
                .folder(testFolder)
                .member(testMember)
                .name("테스트 카테고리")
                .sortOrder(0)
                .viewMode(GeneralTodoViewMode.LIST)
                .statuses(new ArrayList<>())
                .build();

        testStatus = GeneralTodoStatus.builder()
                .id(1L)
                .category(testCategory)
                .member(testMember)
                .name("시작전")
                .sortOrder(0)
                .build();

        testItem = GeneralTodoItem.builder()
                .id(1L)
                .folder(testFolder)
                .category(testCategory)
                .member(testMember)
                .title("테스트 할 일")
                .status(testStatus)
                .build();
    }

    @Nested
    @DisplayName("createFolder 메서드")
    class CreateFolderTest {

        @Test
        @DisplayName("성공 - 폴더 생성")
        void success() {
            // given
            given(memberRepository.get("test@test.com")).willReturn(testMember);
            given(folderRepository.findIdsByMemberId(1L)).willReturn(List.of());

            CreateGeneralTodoFolderRequest request = new CreateGeneralTodoFolderRequest();
            request.setName("새 폴더");

            // when
            GeneralTodoFolderResponse response = generalTodoService.createFolder("test@test.com", request);

            // then
            assertThat(response.getName()).isEqualTo("새 폴더");
            verify(folderRepository).save(any(GeneralTodoFolder.class));
        }

        @Test
        @DisplayName("실패 - 음수 정렬 순서")
        void fail_negativeSortOrder() {
            // given
            given(memberRepository.get("test@test.com")).willReturn(testMember);
            given(folderRepository.findIdsByMemberId(1L)).willReturn(List.of());

            CreateGeneralTodoFolderRequest request = new CreateGeneralTodoFolderRequest();
            request.setName("새 폴더");
            request.setSortOrder(-1);

            // when & then
            assertThatThrownBy(() -> generalTodoService.createFolder("test@test.com", request))
                    .isInstanceOf(ConditionNotMetException.class)
                    .hasMessageContaining("정렬 순서는 0 이상이어야 합니다");
        }
    }

    @Nested
    @DisplayName("renameFolder 메서드")
    class RenameFolderTest {

        @Test
        @DisplayName("성공 - 폴더 이름 변경")
        void success() {
            // given
            given(memberRepository.get("test@test.com")).willReturn(testMember);
            given(folderRepository.findByIdAndMemberId(1L, 1L)).willReturn(Optional.of(testFolder));

            UpdateGeneralTodoFolderRequest request = new UpdateGeneralTodoFolderRequest();
            request.setName("변경된 이름");

            // when
            GeneralTodoFolderResponse response = generalTodoService.renameFolder("test@test.com", 1L, request);

            // then
            assertThat(response.getName()).isEqualTo("변경된 이름");
        }

        @Test
        @DisplayName("실패 - 폴더 미존재")
        void fail_folderNotFound() {
            // given
            given(memberRepository.get("test@test.com")).willReturn(testMember);
            given(folderRepository.findByIdAndMemberId(anyLong(), anyLong())).willReturn(Optional.empty());

            UpdateGeneralTodoFolderRequest request = new UpdateGeneralTodoFolderRequest();
            request.setName("변경된 이름");

            // when & then
            assertThatThrownBy(() -> generalTodoService.renameFolder("test@test.com", 999L, request))
                    .isInstanceOf(ConditionNotMetException.class)
                    .hasMessageContaining("폴더를 찾을 수 없습니다");
        }
    }

    @Nested
    @DisplayName("deleteFolder 메서드")
    class DeleteFolderTest {

        @Test
        @DisplayName("성공 - 폴더 삭제")
        void success() {
            // given
            given(memberRepository.get("test@test.com")).willReturn(testMember);
            given(folderRepository.findByIdAndMemberId(1L, 1L)).willReturn(Optional.of(testFolder));

            // when
            generalTodoService.deleteFolder("test@test.com", 1L);

            // then
            verify(folderRepository).deleteByIdSafe(testFolder.getId());
        }
    }

    @Nested
    @DisplayName("createCategory 메서드")
    class CreateCategoryTest {

        @Test
        @DisplayName("성공 - 카테고리 생성")
        void success() {
            // given
            given(memberRepository.get("test@test.com")).willReturn(testMember);
            given(folderRepository.findByIdAndMemberId(1L, 1L)).willReturn(Optional.of(testFolder));
            given(categoryRepository.findIdsByFolder(1L, 1L)).willReturn(List.of());
            given(statusRepository.findIdsByCategory(anyLong(), anyLong())).willReturn(List.of());

            CreateGeneralTodoCategoryRequest request = new CreateGeneralTodoCategoryRequest();
            request.setName("새 카테고리");
            request.setViewMode(GeneralTodoViewMode.LIST);

            // when
            GeneralTodoCategoryResponse response = generalTodoService.createCategory("test@test.com", 1L, request);

            // then
            assertThat(response.getName()).isEqualTo("새 카테고리");
            verify(categoryRepository).save(any(GeneralTodoCategory.class));
        }
    }

    @Nested
    @DisplayName("updateCategory 메서드")
    class UpdateCategoryTest {

        @Test
        @DisplayName("성공 - 카테고리 업데이트")
        void success() {
            // given
            given(memberRepository.get("test@test.com")).willReturn(testMember);
            given(categoryRepository.findByIdAndMemberId(1L, 1L)).willReturn(Optional.of(testCategory));
            given(statusRepository.findIdsByCategory(1L, 1L)).willReturn(List.of(1L));

            UpdateGeneralTodoCategoryRequest request = new UpdateGeneralTodoCategoryRequest();
            request.setName("변경된 카테고리");

            // when
            GeneralTodoCategoryResponse response = generalTodoService.updateCategory("test@test.com", 1L, request);

            // then
            assertThat(response.getName()).isEqualTo("변경된 카테고리");
        }

        @Test
        @DisplayName("실패 - 카테고리 미존재")
        void fail_categoryNotFound() {
            // given
            given(memberRepository.get("test@test.com")).willReturn(testMember);
            given(categoryRepository.findByIdAndMemberId(anyLong(), anyLong())).willReturn(Optional.empty());

            UpdateGeneralTodoCategoryRequest request = new UpdateGeneralTodoCategoryRequest();
            request.setName("변경된 카테고리");

            // when & then
            assertThatThrownBy(() -> generalTodoService.updateCategory("test@test.com", 999L, request))
                    .isInstanceOf(ConditionNotMetException.class)
                    .hasMessageContaining("카테고리를 찾을 수 없습니다");
        }
    }

    @Nested
    @DisplayName("deleteCategory 메서드")
    class DeleteCategoryTest {

        @Test
        @DisplayName("성공 - 카테고리 삭제")
        void success() {
            // given
            given(memberRepository.get("test@test.com")).willReturn(testMember);
            given(categoryRepository.findByIdAndMemberId(1L, 1L)).willReturn(Optional.of(testCategory));

            // when
            generalTodoService.deleteCategory("test@test.com", 1L);

            // then
            verify(categoryRepository).deleteByIdSafe(testCategory.getId());
        }
    }

    @Nested
    @DisplayName("createStatus 메서드")
    class CreateStatusTest {

        @Test
        @DisplayName("성공 - 상태 생성")
        void success() {
            // given
            given(memberRepository.get("test@test.com")).willReturn(testMember);
            given(categoryRepository.findByIdAndMemberId(1L, 1L)).willReturn(Optional.of(testCategory));
            given(statusRepository.findIdsByCategory(1L, 1L)).willReturn(List.of(1L));

            CreateGeneralTodoStatusRequest request = new CreateGeneralTodoStatusRequest();
            request.setName("진행중");

            // when
            GeneralTodoStatusResponse response = generalTodoService.createStatus("test@test.com", 1L, request);

            // then
            assertThat(response.getName()).isEqualTo("진행중");
            verify(statusRepository).save(any(GeneralTodoStatus.class));
        }
    }

    @Nested
    @DisplayName("deleteStatus 메서드")
    class DeleteStatusTest {

        @Test
        @DisplayName("성공 - 상태 삭제")
        void success() {
            // given
            given(memberRepository.get("test@test.com")).willReturn(testMember);
            given(statusRepository.findByIdAndMemberId(1L, 1L)).willReturn(Optional.of(testStatus));
            given(statusRepository.findIdsByCategory(1L, 1L)).willReturn(List.of(1L, 2L));
            given(itemRepository.existsByStatusId(1L)).willReturn(false);

            // when
            generalTodoService.deleteStatus("test@test.com", 1L, 1L);

            // then
            verify(statusRepository).deleteByIdSafe(testStatus.getId());
        }

        @Test
        @DisplayName("실패 - 최소 1개 상태 필요")
        void fail_minimumOneStatus() {
            // given
            given(memberRepository.get("test@test.com")).willReturn(testMember);
            given(statusRepository.findByIdAndMemberId(1L, 1L)).willReturn(Optional.of(testStatus));
            given(statusRepository.findIdsByCategory(1L, 1L)).willReturn(List.of(1L));

            // when & then
            assertThatThrownBy(() -> generalTodoService.deleteStatus("test@test.com", 1L, 1L))
                    .isInstanceOf(ConditionNotMetException.class)
                    .hasMessageContaining("최소 한 개 이상의 상태가 필요합니다");
        }

        @Test
        @DisplayName("실패 - 연결된 할 일 존재")
        void fail_itemsExist() {
            // given
            given(memberRepository.get("test@test.com")).willReturn(testMember);
            given(statusRepository.findByIdAndMemberId(1L, 1L)).willReturn(Optional.of(testStatus));
            given(statusRepository.findIdsByCategory(1L, 1L)).willReturn(List.of(1L, 2L));
            given(itemRepository.existsByStatusId(1L)).willReturn(true);

            // when & then
            assertThatThrownBy(() -> generalTodoService.deleteStatus("test@test.com", 1L, 1L))
                    .isInstanceOf(ConditionNotMetException.class)
                    .hasMessageContaining("연결된 할 일이 있어 삭제할 수 없습니다");
        }
    }

    @Nested
    @DisplayName("createItem 메서드")
    class CreateItemTest {

        @Test
        @DisplayName("성공 - 할 일 생성 (LIST 뷰)")
        void success_listView() {
            // given
            given(memberRepository.get("test@test.com")).willReturn(testMember);
            given(folderRepository.findByIdAndMemberId(1L, 1L)).willReturn(Optional.of(testFolder));
            given(categoryRepository.findByIdAndMemberId(1L, 1L)).willReturn(Optional.of(testCategory));
            given(statusRepository.findIdsByCategory(1L, 1L)).willReturn(List.of(1L));
            given(statusRepository.findFirstByCategory(1L, 1L)).willReturn(Optional.of(testStatus));

            testCategory.setFolder(testFolder);

            CreateGeneralTodoItemRequest request = new CreateGeneralTodoItemRequest();
            request.setFolderId(1L);
            request.setCategoryId(1L);
            request.setTitle("새 할 일");

            // when
            GeneralTodoItemResponse response = generalTodoService.createItem("test@test.com", request);

            // then
            assertThat(response.getTitle()).isEqualTo("새 할 일");
            verify(itemRepository).save(any(GeneralTodoItem.class));
        }

        @Test
        @DisplayName("실패 - TIMELINE 뷰에서 시작일 미입력")
        void fail_timelineNoStartDate() {
            // given
            testCategory.setViewMode(GeneralTodoViewMode.TIMELINE);
            testCategory.setFolder(testFolder);

            given(memberRepository.get("test@test.com")).willReturn(testMember);
            given(folderRepository.findByIdAndMemberId(1L, 1L)).willReturn(Optional.of(testFolder));
            given(categoryRepository.findByIdAndMemberId(1L, 1L)).willReturn(Optional.of(testCategory));
            given(statusRepository.findIdsByCategory(1L, 1L)).willReturn(List.of(1L));
            given(statusRepository.findFirstByCategory(1L, 1L)).willReturn(Optional.of(testStatus));

            CreateGeneralTodoItemRequest request = new CreateGeneralTodoItemRequest();
            request.setFolderId(1L);
            request.setCategoryId(1L);
            request.setTitle("새 할 일");

            // when & then
            assertThatThrownBy(() -> generalTodoService.createItem("test@test.com", request))
                    .isInstanceOf(ConditionNotMetException.class)
                    .hasMessageContaining("시작일을 입력해야 합니다");
        }
    }

    @Nested
    @DisplayName("updateItem 메서드")
    class UpdateItemTest {

        @Test
        @DisplayName("성공 - 할 일 제목 수정")
        void success_updateTitle() {
            // given
            given(memberRepository.get("test@test.com")).willReturn(testMember);
            given(itemRepository.findByIdAndMemberId(1L, 1L)).willReturn(Optional.of(testItem));
            given(statusRepository.findIdsByCategory(1L, 1L)).willReturn(List.of(1L));

            UpdateGeneralTodoItemRequest request = new UpdateGeneralTodoItemRequest();
            request.setTitle("수정된 제목");

            // when
            GeneralTodoItemResponse response = generalTodoService.updateItem("test@test.com", 1L, request);

            // then
            assertThat(response.getTitle()).isEqualTo("수정된 제목");
        }

        @Test
        @DisplayName("실패 - 카테고리 없이 폴더 변경")
        void fail_folderWithoutCategory() {
            // given
            given(memberRepository.get("test@test.com")).willReturn(testMember);
            given(itemRepository.findByIdAndMemberId(1L, 1L)).willReturn(Optional.of(testItem));
            given(statusRepository.findIdsByCategory(1L, 1L)).willReturn(List.of(1L));

            UpdateGeneralTodoItemRequest request = new UpdateGeneralTodoItemRequest();
            request.setFolderId(2L);

            // when & then
            assertThatThrownBy(() -> generalTodoService.updateItem("test@test.com", 1L, request))
                    .isInstanceOf(ConditionNotMetException.class)
                    .hasMessageContaining("카테고리 없이 폴더를 변경할 수 없습니다");
        }
    }

    @Nested
    @DisplayName("deleteItem 메서드")
    class DeleteItemTest {

        @Test
        @DisplayName("성공 - 할 일 삭제")
        void success() {
            // given
            given(memberRepository.get("test@test.com")).willReturn(testMember);
            given(itemRepository.findByIdAndMemberId(1L, 1L)).willReturn(Optional.of(testItem));

            // when
            generalTodoService.deleteItem("test@test.com", 1L);

            // then
            verify(itemRepository).deleteByIdSafe(testItem.getId());
        }

        @Test
        @DisplayName("실패 - 할 일 미존재")
        void fail_itemNotFound() {
            // given
            given(memberRepository.get("test@test.com")).willReturn(testMember);
            given(itemRepository.findByIdAndMemberId(anyLong(), anyLong())).willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> generalTodoService.deleteItem("test@test.com", 999L))
                    .isInstanceOf(ConditionNotMetException.class)
                    .hasMessageContaining("할 일을 찾을 수 없습니다");
        }
    }
}
