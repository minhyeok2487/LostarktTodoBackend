package lostark.todo.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lostark.todo.domain.comments.Comments;
import lostark.todo.domain.comments.CommentsRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class CommentsService {

    private final CommentsRepository commentsRepository;

    public List<Comments> findAll() {
            return commentsRepository.findAll();
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
}
