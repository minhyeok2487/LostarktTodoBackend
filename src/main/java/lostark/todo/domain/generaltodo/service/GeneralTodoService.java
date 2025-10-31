package lostark.todo.domain.generaltodo.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lostark.todo.domain.generaltodo.dto.*;
import lostark.todo.domain.generaltodo.entity.GeneralTodoCategory;
import lostark.todo.domain.generaltodo.entity.GeneralTodoFolder;
import lostark.todo.domain.generaltodo.entity.GeneralTodoItem;
import lostark.todo.domain.generaltodo.entity.GeneralTodoStatus;
import lostark.todo.domain.generaltodo.enums.GeneralTodoStatusType;
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
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class GeneralTodoService {

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
    private static final String DEFAULT_DONE_STATUS_NAME = "완료";

    private final MemberRepository memberRepository;
    private final GeneralTodoFolderRepository folderRepository;
    private final GeneralTodoCategoryRepository categoryRepository;
    private final GeneralTodoStatusRepository statusRepository;
    private final GeneralTodoItemRepository itemRepository;

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
        ensureDoneStatusExists(member, category);
        return GeneralTodoCategoryResponse.fromEntity(category, member.getUsername());
    }

    public GeneralTodoCategoryResponse updateCategory(String username, Long categoryId, UpdateGeneralTodoCategoryRequest request) {
        Member member = memberRepository.get(username);
        GeneralTodoCategory category = getCategory(categoryId, member.getId());

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

        GeneralTodoStatus doneStatus = ensureDoneStatusExists(member, category);
        int insertOrder = doneStatus.getSortOrder();
        statusRepository.shiftSortOrders(categoryId, memberId, insertOrder);
        doneStatus.updateSortOrder(insertOrder + 1);

        GeneralTodoStatus status = GeneralTodoStatus.builder()
                .category(category)
                .member(member)
                .name(request.getName())
                .sortOrder(insertOrder)
                .type(GeneralTodoStatusType.PROGRESS)
                .build();
        statusRepository.save(status);
        category.getStatuses().add(status);
        return GeneralTodoStatusResponse.fromEntity(status, member.getUsername());
    }

    public GeneralTodoStatusResponse updateStatus(String username, Long statusId, UpdateGeneralTodoStatusRequest request) {
        Member member = memberRepository.get(username);
        Long memberId = member.getId();
        GeneralTodoStatus status = getStatus(statusId, memberId);
        if (status.isDoneType()) {
            throw new ConditionNotMetException("\"DONE\" 상태는 수정할 수 없습니다.");
        }
        status.updateName(request.getName());
        return GeneralTodoStatusResponse.fromEntity(status, member.getUsername());
    }

    public void deleteStatus(String username, Long statusId) {
        Member member = memberRepository.get(username);
        Long memberId = member.getId();
        GeneralTodoStatus status = getStatus(statusId, memberId);
        if (status.isDoneType()) {
            throw new ConditionNotMetException("\"DONE\" 상태는 삭제할 수 없습니다.");
        }
        GeneralTodoCategory category = status.getCategory();

        List<GeneralTodoStatus> remainingStatuses = category.getStatuses().stream()
                .filter(s -> !s.getId().equals(status.getId()))
                .sorted(statusOrdering())
                .collect(Collectors.toList());

        GeneralTodoStatus replacement = remainingStatuses.stream()
                .filter(s -> !s.isDoneType())
                .findFirst()
                .orElse(remainingStatuses.stream()
                        .filter(GeneralTodoStatus::isDoneType)
                        .findFirst()
                        .orElse(null));

        status.getItems().forEach(item -> {
            if (replacement != null) {
                applyStatus(item, replacement);
            } else {
                item.updateStatus(null);
                item.updateCompleted(false);
            }
        });

        category.getStatuses().removeIf(s -> s.getId().equals(status.getId()));
        statusRepository.delete(status);

        if (!remainingStatuses.isEmpty()) {
            List<Long> orderedIds = remainingStatuses.stream()
                    .map(GeneralTodoStatus::getId)
                    .collect(Collectors.toList());
            statusRepository.updateSortOrders(category.getId(), memberId, orderedIds);
        }
    }

    public void reorderStatuses(String username, Long categoryId, ReorderGeneralTodoStatusesRequest request) {
        Member member = memberRepository.get(username);
        Long memberId = member.getId();
        getCategory(categoryId, memberId);

        List<Long> existingStatusIds = statusRepository.findIdsByCategory(categoryId, memberId);
        GlobalMethod.compareLists(existingStatusIds, request.getStatusIds(), "정렬 대상 상태 목록이 일치하지 않습니다.");

        GeneralTodoStatus doneStatus = statusRepository.findByCategoryAndType(categoryId, memberId, GeneralTodoStatusType.DONE)
                .orElseThrow(() -> new ConditionNotMetException("\"DONE\" 상태를 찾을 수 없습니다."));
        if (!request.getStatusIds().isEmpty()) {
            Long lastId = request.getStatusIds().get(request.getStatusIds().size() - 1);
            if (!doneStatus.getId().equals(lastId)) {
                throw new ConditionNotMetException("\"DONE\" 상태는 항상 마지막에 위치해야 합니다.");
            }
        }

        statusRepository.updateSortOrders(categoryId, memberId, request.getStatusIds());
    }

    public GeneralTodoItemResponse createItem(String username, CreateGeneralTodoItemRequest request) {
        Member member = memberRepository.get(username);
        Long memberId = member.getId();
        GeneralTodoFolder folder = getFolder(request.getFolderId(), memberId);
        GeneralTodoCategory category = getCategory(request.getCategoryId(), memberId);
        validateCategoryBelongsToFolder(category, folder);

        GeneralTodoStatus status = resolveStatus(category, memberId, request.getStatusId(), request.getCompleted());
        boolean completed = determineCompletion(status, request.getCompleted());

        GeneralTodoItem item = GeneralTodoItem.builder()
                .folder(folder)
                .category(category)
                .member(member)
                .title(request.getTitle())
                .description(request.getDescription())
                .dueDate(parseDateTime(request.getDueDate()))
                .completed(completed)
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

        GeneralTodoCategory currentCategory = item.getCategory();
        boolean categoryChanged = false;

        if (request.getCategoryId() != null) {
            GeneralTodoCategory targetCategory = getCategory(request.getCategoryId(), memberId);
            GeneralTodoFolder targetFolder = Optional.ofNullable(request.getFolderId())
                    .map(id -> {
                        GeneralTodoFolder folder = getFolder(id, memberId);
                        validateCategoryBelongsToFolder(targetCategory, folder);
                        return folder;
                    })
                    .orElse(targetCategory.getFolder());
            validateCategoryBelongsToFolder(targetCategory, targetFolder);
            item.moveTo(targetFolder, targetCategory);
            currentCategory = targetCategory;
            categoryChanged = true;
        } else if (request.getFolderId() != null) {
            throw new ConditionNotMetException("카테고리 없이 폴더를 변경할 수 없습니다.");
        }

        if (request.getDueDate() != null) {
            item.updateDueDate(parseDateTime(request.getDueDate()));
        }

        if (request.getStatusId() != null) {
            GeneralTodoStatus status = resolveStatusById(currentCategory, memberId, request.getStatusId());
            applyStatus(item, status);
        } else if (request.getCompleted() != null) {
            applyCompletion(item, currentCategory, memberId, request.getCompleted());
        } else if (categoryChanged) {
            alignStatusWithCategory(item, currentCategory, memberId);
        }

        return GeneralTodoItemResponse.fromEntity(item, member.getUsername());
    }

    public void updateItemCompletion(String username, Long itemId, UpdateGeneralTodoItemCompletionRequest request) {
        Member member = memberRepository.get(username);
        GeneralTodoItem item = getItem(itemId, member.getId());
        applyCompletion(item, item.getCategory(), member.getId(), request.getCompleted());
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

    private GeneralTodoStatus getStatus(Long statusId, Long memberId) {
        return statusRepository.findByIdAndMemberId(statusId, memberId)
                .orElseThrow(() -> new ConditionNotMetException("상태를 찾을 수 없습니다."));
    }

    private GeneralTodoStatus resolveStatus(GeneralTodoCategory category, Long memberId, Long statusId, Boolean completed) {
        if (statusId != null) {
            return resolveStatusById(category, memberId, statusId);
        }
        boolean requestedCompleted = Boolean.TRUE.equals(completed);
        GeneralTodoStatus progressStatus = statusRepository.findFirstProgressByCategory(category.getId(), memberId).orElse(null);
        if (requestedCompleted) {
            return statusRepository.findByCategoryAndType(category.getId(), memberId, GeneralTodoStatusType.DONE)
                    .orElse(progressStatus);
        }
        return progressStatus;
    }

    private GeneralTodoStatus resolveStatusById(GeneralTodoCategory category, Long memberId, Long statusId) {
        GeneralTodoStatus status = getStatus(statusId, memberId);
        validateStatusBelongsToCategory(status, category);
        return status;
    }

    private boolean determineCompletion(GeneralTodoStatus status, Boolean requestedCompleted) {
        if (status != null) {
            return status.isDoneType();
        }
        return Boolean.TRUE.equals(requestedCompleted);
    }

    private void applyStatus(GeneralTodoItem item, GeneralTodoStatus status) {
        item.updateStatus(status);
        item.updateCompleted(status != null && status.isDoneType());
    }

    private void applyCompletion(GeneralTodoItem item, GeneralTodoCategory category, Long memberId, boolean completed) {
        GeneralTodoStatus status = resolveStatusForCompletion(category, memberId, completed);
        if (status != null) {
            applyStatus(item, status);
        } else {
            item.updateStatus(null);
            item.updateCompleted(completed);
        }
    }

    private void alignStatusWithCategory(GeneralTodoItem item, GeneralTodoCategory category, Long memberId) {
        applyCompletion(item, category, memberId, item.isCompleted());
    }

    private GeneralTodoStatus resolveStatusForCompletion(GeneralTodoCategory category, Long memberId, boolean completed) {
        if (completed) {
            return statusRepository.findByCategoryAndType(category.getId(), memberId, GeneralTodoStatusType.DONE)
                    .orElse(null);
        }
        return statusRepository.findFirstProgressByCategory(category.getId(), memberId)
                .orElse(null);
    }

    private void validateStatusBelongsToCategory(GeneralTodoStatus status, GeneralTodoCategory category) {
        if (!status.getCategory().getId().equals(category.getId())) {
            throw new ConditionNotMetException("상태가 해당 카테고리에 속해 있지 않습니다.");
        }
    }

    private GeneralTodoStatus ensureDoneStatusExists(Member member, GeneralTodoCategory category) {
        return statusRepository.findByCategoryAndType(category.getId(), member.getId(), GeneralTodoStatusType.DONE)
                .orElseGet(() -> {
                    GeneralTodoStatus status = GeneralTodoStatus.builder()
                            .category(category)
                            .member(member)
                            .name(DEFAULT_DONE_STATUS_NAME)
                            .sortOrder(statusRepository.getNextSortOrder(category.getId(), member.getId()))
                            .type(GeneralTodoStatusType.DONE)
                            .build();
                    statusRepository.save(status);
                    category.getStatuses().add(status);
                    return status;
                });
    }

    private Comparator<GeneralTodoStatus> statusOrdering() {
        return Comparator.comparing(GeneralTodoStatus::isDoneType)
                .thenComparingInt(GeneralTodoStatus::getSortOrder)
                .thenComparingLong(GeneralTodoStatus::getId);
    }

    private void validateCategoryBelongsToFolder(GeneralTodoCategory category, GeneralTodoFolder folder) {
        if (!category.getFolder().getId().equals(folder.getId())) {
            throw new ConditionNotMetException("카테고리가 해당 폴더에 속해 있지 않습니다.");
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
}
