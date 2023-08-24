package lostark.todo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
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

import java.util.Arrays;
import java.util.List;

/**
 * Swagger를 사용하여 API 문서를 생성하고 노출
 */

@Configuration
@EnableSwagger2
public class SwaggerConfiguration {
    private String version;
    private String title;

    /**
     * Swagger 문서 정보
     */
    private ApiInfo apiInfo(String title, String version) {
        return new ApiInfoBuilder()
                .title(title)
                .description("로스트아크 숙제 체크 REST API")
                .version(version)
                .build();
    }

    /**
     * 설정 Bean 등록
     */
    @Bean
    public Docket userApi() {
        version = "유저";
        title = "로스트아크 숙제 체크 API";

        return new Docket(DocumentationType.SWAGGER_2)
                .ignoredParameterTypes(AuthenticationPrincipal.class)
                .useDefaultResponseMessages(false)
                .groupName(version)
                .apiInfo(apiInfo(title, version))
                .select()
                .apis(RequestHandlerSelectors.basePackage("lostark.todo.controller.api"))
                .paths(PathSelectors.ant("/**"))
                .build()
                .securityContexts(Arrays.asList(securityContext()))
                .securitySchemes(Arrays.asList(apiKey()));
    }

    @Bean
    public Docket adminApi() {
        version = "관리자";
        title = "로스트아크 숙제 체크 API";

        return new Docket(DocumentationType.SWAGGER_2)
                .ignoredParameterTypes(AuthenticationPrincipal.class)
                .useDefaultResponseMessages(false)
                .groupName(version)
                .apiInfo(apiInfo(title, version))
                .select()
                .apis(RequestHandlerSelectors.basePackage("lostark.todo.controller.admin"))
                .paths(PathSelectors.ant("/**"))
                .build()
                .securityContexts(Arrays.asList(securityContext()))
                .securitySchemes(Arrays.asList(apiKey()));
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
        return Arrays.asList(new SecurityReference("JWT", authorizationScopes));
    }
}
