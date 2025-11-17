package lostark.todo.domain.generaltodo.service;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
import lostark.todo.global.utils.GlobalMethod;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class GeneralTodoService {

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    private final MemberRepository memberRepository;
    private final GeneralTodoFolderRepository folderRepository;
    private final GeneralTodoCategoryRepository categoryRepository;
    private final GeneralTodoItemRepository itemRepository;
    private final GeneralTodoStatusRepository statusRepository;

    @Transactional(readOnly = true)
    public GeneralTodoOverviewResponse getOverview(String username) {
        Member member = memberRepository.get(username);
        Long memberId = member.getId();
        String memberUsername = member.getUsername();

        GeneralTodoOverviewResponse response = new GeneralTodoOverviewResponse();
        response.setFolders(folderRepository.fetchResponses(memberId, memberUsername));
        response.setCategories(categoryRepository.fetchResponses(memberId, memberUsername));
        response.setTodos(itemRepository.fetchResponses(memberId, memberUsername));
        response.setStatuses(statusRepository.fetchResponses(memberId, memberUsername));
        return response;
    }

    public GeneralTodoFolderResponse createFolder(String username, CreateGeneralTodoFolderRequest request) {
        Member member = memberRepository.get(username);
        Long memberId = member.getId();

        List<Long> existingFolderIds = folderRepository.findIdsByMemberId(memberId);
        int nextSortOrder = existingFolderIds.size();

        int sortOrder = Optional.ofNullable(request.getSortOrder())
                .map(order -> {
                    if (order < 0) {
                        throw new ConditionNotMetException("정렬 순서는 0 이상이어야 합니다.");
                    }
                    if (order >= nextSortOrder) {
                        return nextSortOrder;
                    }
                    folderRepository.shiftSortOrders(memberId, order);
                    return order;
                })
                .orElse(nextSortOrder);

        GeneralTodoFolder folder = GeneralTodoFolder.builder()
                .member(member)
                .name(request.getName())
                .sortOrder(sortOrder)
                .build();
        folderRepository.save(folder);
        return GeneralTodoFolderResponse.fromEntity(folder, member.getUsername());
    }

    public GeneralTodoFolderResponse renameFolder(String username, Long folderId, UpdateGeneralTodoFolderRequest request) {
        Member member = memberRepository.get(username);
        GeneralTodoFolder folder = getFolder(folderId, member.getId());
        folder.rename(request.getName());
        return GeneralTodoFolderResponse.fromEntity(folder, member.getUsername());
    }

    public void reorderFolders(String username, ReorderGeneralTodoFoldersRequest request) {
        Member member = memberRepository.get(username);
        Long memberId = member.getId();

        List<Long> existingFolderIds = folderRepository.findIdsByMemberId(memberId);
        GlobalMethod.compareLists(existingFolderIds, request.getFolderIds(), "정렬 대상 폴더 목록이 일치하지 않습니다.");

        folderRepository.updateSortOrders(memberId, request.getFolderIds());
    }

    public void deleteFolder(String username, Long folderId) {
        Member member = memberRepository.get(username);
        GeneralTodoFolder folder = getFolder(folderId, member.getId());
        folderRepository.delete(folder);
    }

    public GeneralTodoCategoryResponse createCategory(String username, Long folderId, CreateGeneralTodoCategoryRequest request) {
        Member member = memberRepository.get(username);
        GeneralTodoFolder folder = getFolder(folderId, member.getId());

        List<Long> existingCategoryIds = categoryRepository.findIdsByFolder(folderId, member.getId());
        int nextSortOrder = existingCategoryIds.size();

        int sortOrder = Optional.ofNullable(request.getSortOrder())
                .map(order -> {
                    if (order < 0) {
                        throw new ConditionNotMetException("정렬 순서는 0 이상이어야 합니다.");
                    }
                    if (order >= nextSortOrder) {
                        return nextSortOrder;
                    }
                    categoryRepository.shiftSortOrders(folderId, member.getId(), order);
                    return order;
                })
                .orElse(nextSortOrder);

        GeneralTodoCategory category = GeneralTodoCategory.builder()
                .folder(folder)
                .member(member)
                .name(request.getName())
                .color(normalizeColor(request.getColor()))
                .sortOrder(sortOrder)
                .viewMode(Optional.ofNullable(request.getViewMode()).orElse(GeneralTodoViewMode.LIST))
                .build();
        categoryRepository.save(category);
        ensureCategoryHasStatuses(category);
        return GeneralTodoCategoryResponse.fromEntity(category, member.getUsername());
    }

    public GeneralTodoCategoryResponse updateCategory(String username, Long categoryId, UpdateGeneralTodoCategoryRequest request) {
        Member member = memberRepository.get(username);
        GeneralTodoCategory category = getCategory(categoryId, member.getId());
        ensureCategoryHasStatuses(category);

        if (StringUtils.hasText(request.getName())) {
            category.updateName(request.getName());
        }
        if (request.isColorProvided()) {
            category.updateColor(normalizeColor(request.getNormalizedColor()));
        }
        if (request.getViewMode() != null) {
            category.updateViewMode(request.getViewMode());
        }
        return GeneralTodoCategoryResponse.fromEntity(category, member.getUsername());
    }

    public void reorderCategories(String username, Long folderId, ReorderGeneralTodoCategoriesRequest request) {
        Member member = memberRepository.get(username);
        Long memberId = member.getId();
        getFolder(folderId, memberId);
        List<Long> existingCategoryIds = categoryRepository.findIdsByFolder(folderId, memberId);
        GlobalMethod.compareLists(existingCategoryIds, request.getCategoryIds(), "정렬 대상 카테고리 목록이 일치하지 않습니다.");
        categoryRepository.updateSortOrders(folderId, memberId, request.getCategoryIds());
    }

    public void deleteCategory(String username, Long categoryId) {
        Member member = memberRepository.get(username);
        GeneralTodoCategory category = getCategory(categoryId, member.getId());
        categoryRepository.delete(category);
    }

    public GeneralTodoStatusResponse createStatus(String username, Long categoryId, CreateGeneralTodoStatusRequest request) {
        Member member = memberRepository.get(username);
        Long memberId = member.getId();
        GeneralTodoCategory category = getCategory(categoryId, memberId);
        ensureCategoryHasStatuses(category);

        List<Long> existingStatusIds = statusRepository.findIdsByCategory(categoryId, memberId);
        int nextSortOrder = existingStatusIds.size();

        int sortOrder = Optional.ofNullable(request.getSortOrder())
                .map(order -> {
                    if (order < 0) {
                        throw new ConditionNotMetException("정렬 순서는 0 이상이어야 합니다.");
                    }
                    if (order >= nextSortOrder) {
                        return nextSortOrder;
                    }
                    statusRepository.shiftSortOrders(categoryId, memberId, order);
                    return order;
                })
                .orElse(nextSortOrder);

        GeneralTodoStatus status = GeneralTodoStatus.builder()
                .category(category)
                .member(member)
                .name(request.getName())
                .sortOrder(sortOrder)
                .build();
        statusRepository.save(status);
        return GeneralTodoStatusResponse.fromEntity(status, member.getUsername());
    }

    public GeneralTodoStatusResponse renameStatus(String username, Long categoryId, Long statusId, UpdateGeneralTodoStatusRequest request) {
        Member member = memberRepository.get(username);
        GeneralTodoStatus status = getStatus(statusId, member.getId());
        validateStatusBelongsToCategory(status, categoryId);
        status.rename(request.getName());
        return GeneralTodoStatusResponse.fromEntity(status, member.getUsername());
    }

    public void reorderStatuses(String username, Long categoryId, ReorderGeneralTodoStatusesRequest request) {
        Member member = memberRepository.get(username);
        Long memberId = member.getId();
        getCategory(categoryId, memberId);
        List<Long> existingStatusIds = statusRepository.findIdsByCategory(categoryId, memberId);
        GlobalMethod.compareLists(existingStatusIds, request.getStatusIds(), "정렬 대상 상태 목록이 일치하지 않습니다.");
        statusRepository.updateSortOrders(categoryId, memberId, request.getStatusIds());
    }

    public void deleteStatus(String username, Long categoryId, Long statusId) {
        Member member = memberRepository.get(username);
        Long memberId = member.getId();
        GeneralTodoStatus status = getStatus(statusId, memberId);
        validateStatusBelongsToCategory(status, categoryId);

        List<Long> existingStatusIds = statusRepository.findIdsByCategory(categoryId, memberId);
        if (existingStatusIds.size() <= 1) {
            throw new ConditionNotMetException("카테고리에는 최소 한 개 이상의 상태가 필요합니다.");
        }
        if (itemRepository.existsByStatusId(statusId)) {
            throw new ConditionNotMetException("상태에 연결된 할 일이 있어 삭제할 수 없습니다.");
        }
        statusRepository.delete(status);
    }

    public GeneralTodoItemResponse createItem(String username, CreateGeneralTodoItemRequest request) {
        Member member = memberRepository.get(username);
        Long memberId = member.getId();
        GeneralTodoFolder folder = getFolder(request.getFolderId(), memberId);
        GeneralTodoCategory category = getCategory(request.getCategoryId(), memberId);
        validateCategoryBelongsToFolder(category, folder);
        ensureCategoryHasStatuses(category);
        GeneralTodoStatus status = resolveStatus(category, memberId, request.getStatusId());

        ScheduleValues schedule = resolveSchedule(
                category.getViewMode(),
                parseDateTime(request.getStartDate()),
                parseDateTime(request.getDueDate()),
                request.getIsAllDay()
        );

        GeneralTodoItem item = GeneralTodoItem.builder()
                .folder(folder)
                .category(category)
                .member(member)
                .title(request.getTitle())
                .description(request.getDescription())
                .startDate(schedule.getStartDate())
                .dueDate(schedule.getDueDate())
                .allDay(schedule.isAllDay())
                .status(status)
                .build();
        itemRepository.save(item);
        return GeneralTodoItemResponse.fromEntity(item, member.getUsername());
    }

    public GeneralTodoItemResponse updateItem(String username, Long itemId, UpdateGeneralTodoItemRequest request) {
        Member member = memberRepository.get(username);
        Long memberId = member.getId();
        GeneralTodoItem item = getItem(itemId, memberId);

        if (StringUtils.hasText(request.getTitle())) {
            item.updateTitle(request.getTitle());
        }
        if (request.getDescription() != null) {
            item.updateDescription(request.getDescription());
        }

        boolean categoryChanged = false;
        if (request.getCategoryId() != null) {
            GeneralTodoCategory category = getCategory(request.getCategoryId(), memberId);
            GeneralTodoFolder targetFolder = Optional.ofNullable(request.getFolderId())
                    .map(id -> {
                        GeneralTodoFolder folder = getFolder(id, memberId);
                        validateCategoryBelongsToFolder(category, folder);
                        return folder;
                    })
                    .orElse(category.getFolder());
            item.moveTo(targetFolder, category);
            ensureCategoryHasStatuses(category);
            categoryChanged = true;
        } else if (request.getFolderId() != null) {
            throw new ConditionNotMetException("카테고리 없이 폴더를 변경할 수 없습니다.");
        } else {
            ensureCategoryHasStatuses(item.getCategory());
        }

        boolean scheduleFieldsProvided = request.scheduleFieldsProvided();
        GeneralTodoCategory activeCategory = item.getCategory();
        boolean requireScheduleUpdate = scheduleFieldsProvided
                || categoryChanged
                || activeCategory.getViewMode() == GeneralTodoViewMode.TIMELINE;
        if (requireScheduleUpdate) {
            LocalDateTime startDate = Optional.ofNullable(request.getStartDate())
                    .map(this::parseDateTime)
                    .orElse(item.getStartDate());
            LocalDateTime dueDate = Optional.ofNullable(request.getDueDate())
                    .map(this::parseDateTime)
                    .orElse(item.getDueDate());
            boolean isAllDay = Optional.ofNullable(request.getIsAllDay()).orElse(item.isAllDay());
            ScheduleValues schedule = resolveSchedule(activeCategory.getViewMode(), startDate, dueDate, isAllDay);
            item.setStartDate(schedule.getStartDate());
            item.setDueDate(schedule.getDueDate());
            item.setAllDay(schedule.isAllDay());
        }
        if (request.getStatusId() != null) {
            GeneralTodoStatus status = resolveStatus(item.getCategory(), memberId, request.getStatusId());
            item.updateStatus(status);
        } else if (categoryChanged) {
            item.updateStatus(getDefaultStatus(item.getCategory()));
        }
        return GeneralTodoItemResponse.fromEntity(item, member.getUsername());
    }

    public void updateItemStatus(String username, Long itemId, UpdateGeneralTodoItemStatusRequest request) {
        Member member = memberRepository.get(username);
        GeneralTodoItem item = getItem(itemId, member.getId());
        GeneralTodoStatus status = resolveStatus(item.getCategory(), member.getId(), request.getStatusId());
        item.updateStatus(status);
    }

    public void deleteItem(String username, Long itemId) {
        Member member = memberRepository.get(username);
        GeneralTodoItem item = getItem(itemId, member.getId());
        itemRepository.delete(item);
    }

    private GeneralTodoFolder getFolder(Long folderId, Long memberId) {
        return folderRepository.findByIdAndMemberId(folderId, memberId)
                .orElseThrow(() -> new ConditionNotMetException("폴더를 찾을 수 없습니다."));
    }

    private GeneralTodoCategory getCategory(Long categoryId, Long memberId) {
        return categoryRepository.findByIdAndMemberId(categoryId, memberId)
                .orElseThrow(() -> new ConditionNotMetException("카테고리를 찾을 수 없습니다."));
    }

    private GeneralTodoItem getItem(Long itemId, Long memberId) {
        return itemRepository.findByIdAndMemberId(itemId, memberId)
                .orElseThrow(() -> new ConditionNotMetException("할 일을 찾을 수 없습니다."));
    }

    private void validateCategoryBelongsToFolder(GeneralTodoCategory category, GeneralTodoFolder folder) {
        if (!category.getFolder().getId().equals(folder.getId())) {
            throw new ConditionNotMetException("카테고리가 해당 폴더에 속해 있지 않습니다.");
        }
    }

    private GeneralTodoStatus getStatus(Long statusId, Long memberId) {
        return statusRepository.findByIdAndMemberId(statusId, memberId)
                .orElseThrow(() -> new ConditionNotMetException("상태를 찾을 수 없습니다."));
    }

    private GeneralTodoStatus resolveStatus(GeneralTodoCategory category, Long memberId, Long statusId) {
        if (statusId == null) {
            return getDefaultStatus(category);
        }
        GeneralTodoStatus status = getStatus(statusId, memberId);
        validateStatusBelongsToCategory(status, category);
        return status;
    }

    private GeneralTodoStatus getDefaultStatus(GeneralTodoCategory category) {
        ensureCategoryHasStatuses(category);
        return statusRepository.findFirstByCategory(category.getId(), category.getMember().getId())
                .orElseThrow(() -> new ConditionNotMetException("상태를 찾을 수 없습니다."));
    }

    private void ensureCategoryHasStatuses(GeneralTodoCategory category) {
        Long categoryId = category.getId();
        Long memberId = category.getMember().getId();
        if (!statusRepository.findIdsByCategory(categoryId, memberId).isEmpty()) {
            return;
        }
        List<GeneralTodoStatus> defaults = new ArrayList<>();
        defaults.add(buildDefaultStatus(category, "시작전", 0));
        defaults.add(buildDefaultStatus(category, "완료", 1));
        statusRepository.saveAll(defaults);
        category.getStatuses().addAll(defaults);
    }

    private GeneralTodoStatus buildDefaultStatus(GeneralTodoCategory category, String name, int sortOrder) {
        return GeneralTodoStatus.builder()
                .category(category)
                .member(category.getMember())
                .name(name)
                .sortOrder(sortOrder)
                .build();
    }

    private void validateStatusBelongsToCategory(GeneralTodoStatus status, GeneralTodoCategory category) {
        validateStatusBelongsToCategory(status, category.getId());
    }

    private void validateStatusBelongsToCategory(GeneralTodoStatus status, Long categoryId) {
        if (!status.getCategory().getId().equals(categoryId)) {
            throw new ConditionNotMetException("상태가 해당 카테고리에 속해 있지 않습니다.");
        }
    }

    private String normalizeColor(String color) {
        return StringUtils.hasText(color) ? color.trim().toUpperCase() : null;
    }

    private LocalDateTime parseDateTime(String value) {
        if (!StringUtils.hasText(value)) {
            return null;
        }
        try {
            return LocalDateTime.parse(value, DATE_TIME_FORMATTER);
        } catch (DateTimeParseException ex) {
            throw new ConditionNotMetException("날짜 형식이 올바르지 않습니다. 예: YYYY-MM-DDTHH:MM");
        }
    }

    private ScheduleValues resolveSchedule(GeneralTodoViewMode viewMode,
                                           LocalDateTime startDate,
                                           LocalDateTime dueDate,
                                           Boolean isAllDay) {
        boolean allDay = Boolean.TRUE.equals(isAllDay);
        if (viewMode == GeneralTodoViewMode.TIMELINE) {
            if (startDate == null) {
                throw new ConditionNotMetException("타임라인 뷰에서는 시작일을 입력해야 합니다.");
            }
            if (dueDate == null) {
                throw new ConditionNotMetException("타임라인 뷰에서는 마감일을 입력해야 합니다.");
            }
            if (allDay) {
                throw new ConditionNotMetException("타임라인 뷰에서는 하루종일 설정을 사용할 수 없습니다.");
            }
            if (startDate.isAfter(dueDate)) {
                throw new ConditionNotMetException("시작일은 마감일보다 늦을 수 없습니다.");
            }
            return new ScheduleValues(startDate, dueDate, false);
        }

        LocalDateTime normalizedDueDate = allDay ? dueDate.toLocalDate().atStartOfDay() : dueDate;
        return new ScheduleValues(null, normalizedDueDate, allDay);
    }

    @Getter
    private static final class ScheduleValues {
        private final LocalDateTime startDate;
        private final LocalDateTime dueDate;
        private final boolean allDay;

        private ScheduleValues(LocalDateTime startDate, LocalDateTime dueDate, boolean allDay) {
            this.startDate = startDate;
            this.dueDate = dueDate;
            this.allDay = allDay;
        }
    }
}
