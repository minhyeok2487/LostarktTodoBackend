package lostark.todo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger.web.OperationsSorter;
import springfox.documentation.swagger.web.UiConfiguration;
import springfox.documentation.swagger.web.UiConfigurationBuilder;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@Configuration
@EnableSwagger2
public class SwaggerConfiguration {

    private String version;
    private String title;

    @Bean
    public Docket api() {
        version = "V1";
        title = "로스트아크 숙제 체크 API " + version;
        return new Docket(DocumentationType.SWAGGER_2)
                .useDefaultResponseMessages(false)
                .groupName(version)
                .apiInfo(apiInfo(title, version))
                .select()
                .apis(RequestHandlerSelectors.basePackage("lostark.todo.controller.v1"))
                .paths(PathSelectors.ant("/api/v1/**"))
                .build();
    }

    @Bean
    public Docket apiV2() {
        version = "V2";
        title = "로스트아크 숙제 체크 API " + version;
        return new Docket(DocumentationType.SWAGGER_2)
                .useDefaultResponseMessages(false)
                .groupName(version)
                .apiInfo(apiInfo(title, version))
                .select()
                .apis(RequestHandlerSelectors.basePackage("lostark.todo.controller.v2"))
                .paths(PathSelectors.ant("/api/v2/**"))
                .build();
    }

    @Bean
    public UiConfiguration uiConfig() {
        return UiConfigurationBuilder.builder()
                .operationsSorter(OperationsSorter.METHOD).build();
    }

    private ApiInfo apiInfo(String title, String version) {
        return new ApiInfoBuilder()
                .title(title)
                .description("설명 부분")
                .version(version)
                .build();
    }
}
