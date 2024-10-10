package lostark.todo.domainV2.util.content.dao;

import lombok.RequiredArgsConstructor;
import lostark.todo.domain.content.*;
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


    // 카테고리(카오스던전, 가디언토벌)별 일일컨텐츠 출력
    @Transactional(readOnly = true)
    public List<DayContent> findDayContent(Category category) {
        if(category.equals(Category.가디언토벌) || category.equals(Category.카오스던전)) {
            return contentRepository.findDayContentByCategoryOrderByLevelDesc(category);
        } else {
            throw new IllegalArgumentException("카테고리가 일일컨텐츠가 아닙니다.(카오스던전, 가디언토벌)");
        }
    }

    @Transactional
    public List<Content> findAllByIdWeekContent(List<Long> idList) {
        return contentRepository.findAllById(idList);
    }
}
