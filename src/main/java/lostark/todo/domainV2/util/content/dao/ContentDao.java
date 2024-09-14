package lostark.todo.domainV2.util.content.dao;

import lombok.RequiredArgsConstructor;
import lostark.todo.domain.content.ContentRepository;
import lostark.todo.domain.content.CubeContent;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RequiredArgsConstructor
@Repository
public class ContentDao {

    private final ContentRepository contentRepository;

    @Transactional(readOnly = true)
    public List<CubeContent> findAllCubeContent() {
        return contentRepository.findAllByCubeContent();
    }
}
