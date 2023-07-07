package lostark.todo.service;

import lombok.RequiredArgsConstructor;
import lostark.todo.controller.dto.DayContentDto;
import lostark.todo.domain.content.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Transactional
public class ContentService {

    private final DayContentRepository dayContentRepository;
    private final ContentRepository contentRepository;

    public Content findContentById(Long id) {
        return contentRepository.findById(id).orElseThrow();
    }

    public DayContent saveDayContent(DayContent dayContent, int level, String name) {
        dayContent.setCategory(Category.일일);
        dayContent.setLevel(level);
        dayContent.setName(name);
        DayContent saved = dayContentRepository.save(dayContent);
        return saved;
    }

    public DayContent findDayContentById(Long id) {

        return dayContentRepository.findById(id).orElseThrow();
    }

    public List<DayContent> findDayContents() {
        return dayContentRepository.findAll();
    }


    public DayContent updateDayContent(DayContentDto dayContentDto) {
        DayContent dayContent = findDayContentById(dayContentDto.getId());
        DayContent updated = dayContent.update(dayContentDto);
        return updated;
    }

    public Map<Category, DayContent> getDayContentByLevel(double level) {
        DayContent chaosContent = contentRepository.findDayContentByLevel(level, Category.카오스던전).get(0);
        DayContent guardianContent = contentRepository.findDayContentByLevel(level, Category.가디언토벌).get(0);

        Map<Category, DayContent> dayContentMap = new HashMap<>();
        dayContentMap.put(Category.카오스던전, chaosContent);
        dayContentMap.put(Category.가디언토벌, guardianContent);
        return dayContentMap;
    }
}
