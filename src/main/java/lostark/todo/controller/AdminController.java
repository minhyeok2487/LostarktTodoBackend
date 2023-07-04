package lostark.todo.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lostark.todo.controller.dtos.DayContentDto;
import lostark.todo.domain.content.Category;
import lostark.todo.domain.content.Content;
import lostark.todo.domain.content.DayContent;
import lostark.todo.service.ContentService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/admin")
public class AdminController {

    private final ContentService contentService;

    @GetMapping("/contents")
    public String content(Model model) {
        List<DayContent> dayContents = contentService.findDayContents();
        model.addAttribute("dayContents", dayContents);
        return "admin/contents";
    }

    @GetMapping("/contents/update/{id}")
    public String updateForm(@PathVariable Long id, Model model) {
        Content content = contentService.findContentById(id);
        if (content.getCategory().equals(Category.일일)) {
            DayContent dayContent = contentService.findDayContentById(content.getId());
            model.addAttribute("dayContent", dayContent);
            return "admin/dayContent";
        }
        return "admin/contents";
    }

    @PostMapping("/contents/update/dayContent")
    public String update(DayContentDto dayContentDto, Model model) {
        DayContent dayContent = contentService.updateDayContent(dayContentDto);
        model.addAttribute("dayContent", dayContent);
        return "redirect:/admin/contents";
    }
}
