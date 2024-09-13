package lostark.todo.domainV2.board.recrutingBoard.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lostark.todo.config.TokenProvider;
import lostark.todo.controller.dto.boardsDto.ImageUrlDto;
import lostark.todo.controller.dtoV2.image.ImageResponse;
import lostark.todo.domain.member.Member;
import lostark.todo.domainV2.board.recrutingBoard.dao.RecruitingBoardDao;
import lostark.todo.domainV2.board.recrutingBoard.dao.RecruitingBoardImagesDao;
import lostark.todo.domainV2.board.recrutingBoard.dto.*;
import lostark.todo.domainV2.board.recrutingBoard.entity.RecruitingBoard;
import lostark.todo.domainV2.board.recrutingBoard.enums.RecruitingCategoryEnum;
import lostark.todo.domainV2.board.recrutingBoard.enums.TimeCategoryEnum;
import lostark.todo.domainV2.member.dao.MemberDao;
import lostark.todo.service.ImagesService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static lostark.todo.constants.ErrorMessages.MEMBER_NOT_MATCH;
import static lostark.todo.constants.ErrorMessages.TIME_CATEGORY_INVALID_SELECTION;

@Service
@RequiredArgsConstructor
@Slf4j
public class RecruitingBoardService {

    private final RecruitingBoardDao recruitingBoardDao;
    private final MemberDao memberDao;
    private final RecruitingBoardImagesDao recruitingBoardImagesDao;
    private final TokenProvider tokenProvider;
    private final ImagesService imagesService;


    @Transactional(readOnly = true)
    public Page<SearchRecruitingBoardResponse> search(SearchRecruitingBoardRequest request, PageRequest pageRequest) {
        return recruitingBoardDao.search(request, pageRequest).map(SearchRecruitingBoardResponse::new);
    }

    @Transactional(readOnly = true)
    public Map<String, List<SearchRecruitingBoardResponse>> searchMain() {
        List<RecruitingBoard> recruitingBoards = recruitingBoardDao.searchMain();

        // 카테고리 그룹 정의
        Map<RecruitingCategoryEnum, String> categoryMap = Map.of(
                RecruitingCategoryEnum.FRIENDS, "FRIENDS",
                RecruitingCategoryEnum.RECRUITING_GUILD, "GUILD",
                RecruitingCategoryEnum.LOOKING_GUILD, "GUILD",
                RecruitingCategoryEnum.RECRUITING_PARTY, "PARTY",
                RecruitingCategoryEnum.LOOKING_PARTY, "PARTY"
        );

        return recruitingBoards.stream()
                .collect(Collectors.groupingBy(
                        recruitingBoard -> categoryMap.getOrDefault(recruitingBoard.getRecruitingCategory(), "ETC"),
                        Collectors.mapping(
                                SearchRecruitingBoardResponse::new,
                                Collectors.toList()
                        )
                ));
    }


    @Transactional
    public GetRecruitingBoardResponse get(Long recruitingBoardId, String token) {
        RecruitingBoard recruitingBoard = recruitingBoardDao.get(recruitingBoardId);
        recruitingBoard.upShowCount(); //조회수 증가

        Member member = null;
        if (StringUtils.hasText(token)) {
            String username = tokenProvider.validToken(token);
            member = memberDao.get(username);
        }
        return new GetRecruitingBoardResponse(recruitingBoard, member);
    }

    @Transactional
    public CreateRecruitingBoardResponse create(String username, CreateRecruitingBoardRequest request) {
        validateCreate(request);
        Member member = memberDao.get(username);
        RecruitingBoard recruitingBoard = RecruitingBoard.toEntity(member, request);
        recruitingBoardImagesDao.saveByfileNames(request.getFileNames(), recruitingBoard);
        RecruitingBoard save = recruitingBoardDao.save(recruitingBoard);
        return new CreateRecruitingBoardResponse(save.getId());
    }

    private void validateCreate(CreateRecruitingBoardRequest request) {
        if (request.getWeekdaysPlay().contains(TimeCategoryEnum.NONE) && request.getWeekdaysPlay().size() > 1) {
            throw new IllegalArgumentException(TIME_CATEGORY_INVALID_SELECTION);
        }

        if (request.getWeekendsPlay().contains(TimeCategoryEnum.NONE) && request.getWeekendsPlay().size() > 1) {
            throw new IllegalArgumentException(TIME_CATEGORY_INVALID_SELECTION);
        }
    }

    @Transactional
    public void update(String username, UpdateRecruitingBoardRequest request, Long recruitingBoardId) {
        Member member = memberDao.get(username);
        RecruitingBoard recruitingBoard = validateOwnership(member, recruitingBoardId);
        recruitingBoard.update(request);
    }

    private RecruitingBoard validateOwnership(Member member, Long recruitingBoardId) {
        RecruitingBoard recruitingBoard = recruitingBoardDao.get(recruitingBoardId);
        if (member.getId() != recruitingBoard.getMember().getId()) {
            throw new IllegalArgumentException(MEMBER_NOT_MATCH);
        }
        return recruitingBoard;
    }

    @Transactional
    public void delete(String username, Long recruitingBoardId) {
        Member member = memberDao.get(username);
        RecruitingBoard recruitingBoard = validateOwnership(member, recruitingBoardId);
        recruitingBoardDao.delete(recruitingBoard);
    }

    @Transactional
    public ImageUrlDto uploadImage(String username, MultipartFile image) {
        memberDao.get(username); //체크만
        String folderName = "recruiting/";
        ImageResponse imageResponse = imagesService.upload(image, folderName);
        recruitingBoardImagesDao.uploadImage(imageResponse);
        return new ImageUrlDto(imageResponse);
    }
}
