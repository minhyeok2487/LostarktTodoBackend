package lostark.todo.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lostark.todo.domain.comments.Comments;
import lostark.todo.domain.comments.CommentsRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    public Comments findById(long id) {
        return commentsRepository.findById(id).orElse(null);
    }
}
