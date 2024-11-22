package lostark.todo.domainV2.board.comments.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lostark.todo.controller.dto.commentsDto.CommentListDto;
import lostark.todo.controller.dto.commentsDto.CommentResponseDto;
import lostark.todo.controller.dtoV2.admin.SearchAdminCommentsRequest;
import lostark.todo.controller.dtoV2.admin.SearchAdminCommentsResponse;
import lostark.todo.domainV2.board.comments.dto.CommentResponse;
import lostark.todo.domainV2.board.comments.entity.Comments;
import lostark.todo.domainV2.board.comments.repository.CommentsRepository;
import lostark.todo.global.dto.CursorResponse;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class CommentsService {

    private final CommentsRepository commentsRepository;

    public Page<Comments> findAllByParentIdIs0(int page) {
        return commentsRepository.findAllByParentIdIs0(PageRequest.of(page, 5, Sort.by(Sort.Direction.DESC, "createdDate")));
    }

    public List<Comments> findAllByParentId(long id) {
        return commentsRepository.findAllByParentId(id);
    }


    public List<Comments> findAll() {
            return commentsRepository.findAll(Sort.by(Sort.Direction.DESC, "id"));
    }

    public Comments save(Comments comments) {
       return commentsRepository.save(comments);
    }

    public void update(Comments comments) {
        Comments currentComment = commentsRepository.findById(comments.getId())
                .orElseThrow(() -> new IllegalArgumentException("없는 글 입니다."));
        currentComment.setBody(comments.getBody());

    }

    public void delete(Comments comments) {
        commentsRepository.deleteById(comments.getId());
    }

    @Transactional(readOnly = true)
    public Page<SearchAdminCommentsResponse> searchAdmin(SearchAdminCommentsRequest request, PageRequest pageRequest) {
        return commentsRepository.searchAdmin(request, pageRequest);
    }

    @Transactional(readOnly = true)
    public CommentListDto test() {
        List<Comments> test = commentsRepository.findAllByParentId(0);
        List<CommentResponseDto> commentResponseDtoList = new ArrayList<>();
        for (Comments comments : test) {
            commentResponseDtoList.add(new CommentResponseDto().createResponseDto(comments));
        }

        return new CommentListDto(commentResponseDtoList, 10000000);
    }

    @Transactional(readOnly = true)
    public CursorResponse<CommentResponse> searchCursor(Long commentsId, PageRequest pageRequest) {
        return commentsRepository.searchCursor(commentsId, pageRequest);
    }
}
