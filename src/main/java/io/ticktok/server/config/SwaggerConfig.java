package io.ticktok.server.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.ParameterBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.schema.ModelRef;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Parameter;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.ArrayList;
import java.util.List;

// @Configuration
@EnableSwagger2
public class SwaggerConfig {

    @Bean
    public Docket customImplementation(){
        return new Docket(DocumentationType.SWAGGER_2)
                .select()
                .apis(RequestHandlerSelectors.basePackage("io.ticktok.server"))
                .paths(PathSelectors.any())
                .build()
                .globalOperationParameters(commonParameters())
                .apiInfo(apiInfo()).useDefaultResponseMessages(false);
    }

    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title("Ticktok.io server API")
                .license("Apache 2.0")
                .licenseUrl("http://www.apache.org/licenses/LICENSE-2.0.html")
                .termsOfServiceUrl("")
                .version("1.0.0")
                .build();
    }

    private List<Parameter> commonParameters(){
        List<Parameter> parameters = new ArrayList<>();
        parameters.add(new ParameterBuilder()
                .name("access_token")
                .description("token for authorization")
                .modelRef(new ModelRef("string"))
                .parameterType("query")
                .required(false)
                .build());
        parameters.add(new ParameterBuilder()
                .name("Authorization")
                .description("token for authorization")
                .modelRef(new ModelRef("string"))
                .parameterType("header")
                .required(false)
                .build());

        return parameters;
    }
}
