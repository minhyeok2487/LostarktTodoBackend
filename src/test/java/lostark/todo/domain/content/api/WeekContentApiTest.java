package lostark.todo.domain.content.api;

import lostark.todo.config.DataSourceProxyConfig;
import lostark.todo.config.MeasurePerformance;
import lostark.todo.domain.content.enums.WeekContentCategory;
import lostark.todo.global.config.TokenProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@Import(DataSourceProxyConfig.class)
class WeekContentApiTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TokenProvider tokenProvider;

    private static final String TEST_USERNAME = "repeat2487@gmail.com";

    private String token;

    @BeforeEach
    void setUp() {
        token = tokenProvider.createToken(TEST_USERNAME);
    }

    @Test
    @DisplayName("레이드 카테고리 조회")
    @MeasurePerformance(maxQueries = 5)
    void getScheduleRaidCategory() throws Exception {
        mockMvc.perform(get("/api/v1/content/week/raid/category")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("주간 콘텐츠 난이도 카테고리 목록 조회")
    void getWeekContentCategories() throws Exception {
        mockMvc.perform(get("/api/v1/content/week/categories")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(WeekContentCategory.values().length)))
                .andExpect(jsonPath("$[0].name").exists())
                .andExpect(jsonPath("$[0].displayName").exists())
                .andExpect(jsonPath("$[0].sortOrder").exists())
                .andExpect(jsonPath("$[0].color").exists());
    }

    @Test
    @DisplayName("주간 콘텐츠 카테고리 API 인증 없이 접근 가능")
    void getWeekContentCategoriesWithoutAuth() throws Exception {
        mockMvc.perform(get("/api/v1/content/week/categories"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(WeekContentCategory.values().length)));
    }

    @Test
    @DisplayName("WeekContentCategory enum에 sortOrder, displayName, color가 있는지 확인")
    void weekContentCategoryHasMetadata() {
        for (WeekContentCategory category : WeekContentCategory.values()) {
            assertNotNull(category.getDisplayName(), category.name() + " displayName이 null");
            assertNotNull(category.getColor(), category.name() + " color가 null");
            assertTrue(category.getSortOrder() > 0, category.name() + " sortOrder가 0 이하");
        }
    }

    @Test
    @DisplayName("WeekContentCategory sortOrder가 고유한지 확인")
    void weekContentCategorySortOrderUnique() {
        WeekContentCategory[] values = WeekContentCategory.values();
        long distinctCount = java.util.Arrays.stream(values)
                .mapToInt(WeekContentCategory::getSortOrder)
                .distinct()
                .count();
        assertEquals(values.length, distinctCount, "sortOrder에 중복이 있습니다");
    }
}
