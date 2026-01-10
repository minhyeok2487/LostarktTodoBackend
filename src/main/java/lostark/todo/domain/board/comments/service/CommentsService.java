package lostark.todo.domain.board.comments.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lostark.todo.domain.admin.dto.AdminCommentResponse;
import lostark.todo.domain.board.comments.dto.CommentListDto;
import lostark.todo.domain.board.comments.dto.CommentResponseDto;
import lostark.todo.domain.board.comments.entity.Comments;
import lostark.todo.domain.board.comments.repository.CommentsRepository;
import lostark.todo.global.exhandler.exceptions.ConditionNotMetException;
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

    @Transactional(readOnly = true)
    public CommentListDto searchComments(int page) {
        Page<Comments> allComments = commentsRepository.findAllByParentIdIs0(PageRequest.of(page, 5, Sort.by(Sort.Direction.DESC, "createdDate")));
        int totalPages = allComments.getTotalPages();

        List<CommentResponseDto> commentResponseDtoList = new ArrayList<>();
        for (Comments comment : allComments.getContent()) {
            List<Comments> commentList = commentsRepository.findAllByParentId(comment.getId());
            commentResponseDtoList.add(new CommentResponseDto().createResponseDto(comment));
            if(!commentList.isEmpty()) {
                for (Comments reply : commentList) {
                    commentResponseDtoList.add(new CommentResponseDto().createResponseDto(reply));
                }
            }
        }

        return new CommentListDto(commentResponseDtoList, totalPages);
    }

    // =============== Admin Methods ===============

    @Transactional(readOnly = true)
    public Page<AdminCommentResponse> getCommentsForAdmin(Pageable pageable) {
        Page<Comments> comments = commentsRepository.findAll(
                PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(),
                        Sort.by(Sort.Direction.DESC, "createdDate")));

        return comments.map(AdminCommentResponse::from);
    }

    @Transactional
    public void deleteByAdmin(Long commentId) {
        Comments comment = commentsRepository.findById(commentId)
                .orElseThrow(() -> new ConditionNotMetException("댓글이 존재하지 않습니다. ID: " + commentId));

        // 답글도 함께 삭제
        List<Comments> replies = commentsRepository.findAllByParentId(commentId);
        commentsRepository.deleteAll(replies);

        commentsRepository.delete(comment);
    }
}
