package lostark.todo.service;

import lostark.todo.domain.content.Category;
import lostark.todo.domain.content.Content;
import lostark.todo.domain.content.ContentRepository;
import lostark.todo.domain.content.DayContent;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class ContentServiceTest {

    @Autowired ContentService contentService;
    @Autowired
    ContentRepository contentRepository;

    @Test
    public void saveDayContentTest() {
        DayContent chaosContent = DayContent.createChaos(72415, 2438,
                4.9, 76.7, 226.4, 7, 21);
        DayContent savedDayContent = contentService.saveDayContent(chaosContent, 1415, "타락1");

        DayContent content = contentService.findDayContentById(savedDayContent.getId());
        Assertions.assertThat(savedDayContent).isEqualTo(content);
    }

    @Test
    public void getDayContentByLevelTest() {
        double level = 1618.5;
        List<DayContent> content = contentRepository.findDayContentByLevel(level, Category.카오스던전);
        System.out.println("content = " + content.toString());
    }

}