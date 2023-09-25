package lostark.todo.service;

import lostark.todo.domain.content.Category;
import lostark.todo.domain.content.ContentRepository;
import lostark.todo.domain.content.DayContent;
import lostark.todo.domain.content.WeekContent;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;


@ExtendWith(MockitoExtension.class)
class ContentServiceTest {

    @InjectMocks
    ContentService contentService;

    @Mock
    ContentRepository contentRepository;

    @Test
    @DisplayName("카테고리별 일일 컨텐츠 출력 테스트 성공")
    void findDayContentTest() {
        // given
        Category category = Category.카오스던전;
        List<DayContent> dayContentList = new ArrayList<>();
        DayContent content1 = DayContent.builder()
                .category(category)
                .name("계몽1")
                .build();
        DayContent content2 = DayContent.builder()
                .category(category)
                .name("천공1")
                .build();
        dayContentList.add(content1);
        dayContentList.add(content2);
        given(contentRepository.findDayContentByCategoryOrderByLevelDesc(category)).willReturn(dayContentList);

        // when
        List<DayContent> result = contentService.findDayContent(category);

        // then
        verify(contentRepository).findDayContentByCategoryOrderByLevelDesc(category);
        assertThat(result.size()).isEqualTo(dayContentList.size());
    }

    @Test
    @DisplayName("카테고리별 일일 컨텐츠 출력 테스트 실패 - 카테고리 오류")
    void findDayContentTestCategoryException() {
        // given
        Category category = Category.군단장레이드;

        // when - then
        assertThatThrownBy(() -> contentService.findDayContent(category))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("카테고리가 일일컨텐츠가 아닙니다.(카오스던전, 가디언토벌)");
    }

    @Test
    @DisplayName("일일 컨텐츠 이름으로 조회 테스트 성공")
    void findDayContentByNameTest() {
        // given
        String name = "계몽1";
        DayContent dayContent = DayContent.builder()
                .category(Category.카오스던전)
                .name(name)
                .build();
        given(contentRepository.findContentByName(name)).willReturn(Optional.ofNullable(dayContent));

        // when
        DayContent result = contentService.findDayContentByName(name);

        // then
        assertThat(result).isEqualTo(dayContent);
    }

    @Test
    @DisplayName("일일 컨텐츠 이름으로 조회 테스트 실패 - 없는 이름")
    void findDayContentByNameTestNameException() {
        // given
        String name = "계몽1";
        given(contentRepository.findContentByName(name)).willThrow(new IllegalArgumentException(name+" - 없는 컨텐츠 입니다."));

        // when - then
        assertThatThrownBy(() -> contentService.findDayContentByName(name))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage(name+" - 없는 컨텐츠 입니다.");
    }

    @Test
    @DisplayName("주간 컨텐츠 전체 조회 테스트 성공")
    void findAllByWeekContentTest() {
        //given
        Category category = Category.군단장레이드;
        List<WeekContent> weekContentList = new ArrayList<>();
        WeekContent content1 = WeekContent.builder()
                .category(category)
                .name("카멘 노말")
                .build();
        WeekContent content2 = WeekContent.builder()
                .category(category)
                .name("카양겔 하드")
                .build();
        weekContentList.add(content1);
        weekContentList.add(content2);
        given(contentRepository.findAllByWeekContent()).willReturn(weekContentList);

        // when
        List<WeekContent> result = contentService.findAllByWeekContent();

        // then
        verify(contentRepository).findAllByWeekContent();
        assertThat(result.size()).isEqualTo(weekContentList.size());
    }

    @Test
    @DisplayName("주간 컨텐츠 추가 테스트 성공")
    void saveWeekContentTest() {
        //given
        Category category = Category.군단장레이드;
        WeekContent content = WeekContent.builder()
                .category(category)
                .name("카멘 노말 테스트")
                .level(1610)
                .build();
        given(contentRepository.save(content)).willReturn(content);

        // when
        WeekContent result = contentService.saveWeekContent(content);

        // then
        verify(contentRepository).save(content);
        assertThat(result).isEqualTo(content);
    }

    @Test
    @DisplayName("주간 컨텐츠 추가 테스트 실패 - 카테고리 오류")
    void saveWeekContentTestCategoryException() {
        //given
        Category category = Category.카오스던전;
        WeekContent content = WeekContent.builder()
                .category(category)
                .name("카멘 노말 테스트")
                .level(1610)
                .build();

        // when-then
        assertThatThrownBy(() -> contentService.saveWeekContent(content))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("카테고리가 주간 컨텐츠가 아닙니다.");
    }

    @Test
    @DisplayName("아이템 레벨보다 작은 주간 컨텐츠 조회 테스트 성공")
    void findAllByWeekContentWithItemLevelTest() {
        // given
        Category category = Category.카오스던전;
        List<WeekContent> weekContentList = new ArrayList<>();
        WeekContent content1 = WeekContent.builder()
                .category(category)
                .name("테스트 1")
                .level(1580)
                .build();
        WeekContent content2 = WeekContent.builder()
                .category(category)
                .name("테스트 2")
                .level(1610)
                .build();
        WeekContent content3 = WeekContent.builder()
                .category(category)
                .name("테스트 3")
                .level(1630)
                .build();
        weekContentList.add(content1);
        weekContentList.add(content2);
        weekContentList.add(content3);
        double itemLevel = 1611.12;
        given(contentRepository.findAllByWeekContentWithItemLevel(itemLevel))
                .willReturn(weekContentList.stream()
                        .filter(weekContent -> weekContent.getLevel()<=itemLevel)
                        .collect(Collectors.toList()));

        // when
        List<WeekContent> result = contentService.findAllByWeekContentWithItemLevel(itemLevel);

        // then
        assertThat(result.size()).isNotEqualTo(weekContentList.size());
        assertThat(result.size()).isEqualTo(2);
    }
}