package lostark.todo.service;

import lostark.todo.domain.content.DayContent;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.transaction.Transactional;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@Transactional
@SpringBootTest
class ContentServiceTest {

    @Autowired ContentService contentService;
    
    @Test
    void findDayContent() {
        Map<String, DayContent> dayContent = contentService.findDayContent();
        System.out.println("dayContent.toString() = " + dayContent.toString());
    }
}