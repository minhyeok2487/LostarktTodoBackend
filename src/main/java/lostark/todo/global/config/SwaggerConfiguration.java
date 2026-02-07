package lostark.todo.global.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.ApiKey;
import springfox.documentation.service.AuthorizationScope;
import springfox.documentation.service.SecurityReference;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.contexts.SecurityContext;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger.web.OperationsSorter;
import springfox.documentation.swagger.web.UiConfiguration;
import springfox.documentation.swagger.web.UiConfigurationBuilder;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.*;

/**
 * Swagger를 사용하여 API 문서를 생성하고 노출
 */

@Configuration
@EnableSwagger2
@Profile("!prod")
public class SwaggerConfiguration {

    /**
     * Swagger 문서 정보
     */
    private ApiInfo apiInfo(String description, String version) {
        return new ApiInfoBuilder()
                .title("로아투두 API")
                .description(description)
                .version(version)
                .build();
    }

    private Docket createDocket(String version, String description, String basePackage) {
        return new Docket(DocumentationType.SWAGGER_2)
                .ignoredParameterTypes(AuthenticationPrincipal.class)
                .useDefaultResponseMessages(false)
                .groupName(version)
                .apiInfo(apiInfo(description, version))
                .select()
                .apis(RequestHandlerSelectors.basePackage(basePackage))
                .build()
                .securityContexts(Collections.singletonList(securityContext()))
                .securitySchemes(Collections.singletonList(apiKey()));
    }

    @Bean
    public Docket board() {
        return createDocket("게시판", "게시판 API (공지사항, 모집, 방명록)", "lostark.todo.domain.board");
    }

    @Bean
    public Docket characters() {
        return createDocket("캐릭터", "캐릭터 API", "lostark.todo.domain.character");
    }

    @Bean
    public Docket content() {
        return createDocket("콘텐츠", "로스트아크 콘텐츠 API", "lostark.todo.domain.content");
    }

    @Bean
    public Docket cube() {
        return createDocket("큐브", "큐브 API", "lostark.todo.domain.cube");
    }

    @Bean
    public Docket friend() {
        return createDocket("깐부(친구)", "깐부(친구) API", "lostark.todo.domain.friend");
    }

    @Bean
    public Docket logs() {
        return createDocket("로그(타임라인)", "타임라인(로그) API", "lostark.todo.domain.logs");
    }

    @Bean
    public Docket member() {
        return createDocket("회원", "회원 API", "lostark.todo.domain.member");
    }

    @Bean
    public Docket notification() {
        return createDocket("알림", "알림 API", "lostark.todo.domain.notification");
    }

    @Bean
    public Docket schedule() {
        return createDocket("일정", "일정 API", "lostark.todo.domain.schedule");
    }


    /**
     * Method 순으로 정렬
     */
    @Bean
    public UiConfiguration uiConfig() {
        return UiConfigurationBuilder.builder()
                .operationsSorter(OperationsSorter.METHOD).build();
    }

    /**
     * JWT 보안 스킴 설정
     */
    private ApiKey apiKey() {
        return new ApiKey("JWT", "Authorization", "header");
    }


    /**
     * API 인증에 대한 소코프 설정
     * JWT 보안 스킴과 연결되어 특정 API 엔드포인트에 대한 인증 권한 제어
     */
    private SecurityContext securityContext() {
        return springfox
                .documentation
                .spi.service
                .contexts
                .SecurityContext
                .builder()
                .securityReferences(defaultAuth()).forPaths(PathSelectors.any()).build();
    }

    List<SecurityReference> defaultAuth() {
        AuthorizationScope authorizationScope = new AuthorizationScope("global", "accessEverything");
        AuthorizationScope[] authorizationScopes = new AuthorizationScope[1];
        authorizationScopes[0] = authorizationScope;
        return List.of(new SecurityReference("JWT", authorizationScopes));
    }

}
