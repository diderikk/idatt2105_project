package idatt2105.backend.Configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 * Configures swagger at /v2/api-docs and swagger-ui at /swagger-ui/
 * Inspired by: https://riptutorial.com/swagger/example/25118/setup-springfox-using-swagger-ui-in-spring-boot
 */
@Configuration
@EnableSwagger2
public class SwaggerConfiguration {
    /**
     * Sets up the api to be displayed in the swagger docs
     * @return Docket
     */
    @Bean
    public Docket api(){
        return new Docket(DocumentationType.SWAGGER_2).apiInfo(metadata()).select().apis(RequestHandlerSelectors.any()).paths(PathSelectors.any()).build();
    }

    /**
     * Creates the meta data to be used in the swagger documentation
     * @return ApiInfo
     */
    private static ApiInfo metadata(){
        return new ApiInfoBuilder().title("Book That Room API").version("1.0.0").description("The REST API for Book That Room").build();
    }
}
