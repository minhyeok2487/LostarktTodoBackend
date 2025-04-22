package lostark.todo.domain.board.comments.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lostark.todo.domain.board.comments.dto.CommentListDto;
import lostark.todo.domain.board.comments.dto.CommentResponseDto;
import lostark.todo.domain.board.comments.entity.Comments;
import lostark.todo.domain.board.comments.repository.CommentsRepository;
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
}
