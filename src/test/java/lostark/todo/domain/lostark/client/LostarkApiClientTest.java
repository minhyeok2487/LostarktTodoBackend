package lostark.todo.domain.lostark.client;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class LostarkApiClientTest {

    private LostarkApiClient apiClient;

    @BeforeEach
    void setUp() {
        apiClient = new LostarkApiClient();
        ReflectionTestUtils.setField(apiClient, "baseUrl", "https://developer-lostark.game.onstove.com");
    }

    @Test
    @DisplayName("RestTemplate을 사용한다 (HttpURLConnection이 아닌)")
    void shouldUseRestTemplate() {
        Object restTemplate = ReflectionTestUtils.getField(apiClient, "restTemplate");
        assertThat(restTemplate).isNotNull();
        assertThat(restTemplate).isInstanceOf(RestTemplate.class);
    }

    @Test
    @DisplayName("lostarkGetApi는 String을 반환한다")
    void lostarkGetApi_returnsString() throws NoSuchMethodException {
        var method = LostarkApiClient.class.getMethod("lostarkGetApi", String.class, String.class);
        assertThat(method.getReturnType()).isEqualTo(String.class);
    }

    @Test
    @DisplayName("lostarkPostApi는 String을 반환한다")
    void lostarkPostApi_returnsString() throws NoSuchMethodException {
        var method = LostarkApiClient.class.getMethod("lostarkPostApi", String.class, String.class, String.class);
        assertThat(method.getReturnType()).isEqualTo(String.class);
    }

    @Test
    @DisplayName("잘못된 URL로 호출 시 예외 발생")
    void invalidUrl_throwsException() {
        assertThatThrownBy(() -> apiClient.lostarkGetApi("http://invalid-host-that-does-not-exist.test/api", "test-key"))
                .isInstanceOf(RuntimeException.class);
    }
}
