package io.ticktok.server.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

@Configuration
public class WebConfig extends WebMvcConfigurerAdapter {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        if (!registry.hasMappingForPattern("/webjars/**")) {
            registry.addResourceHandler("/webjars/**").addResourceLocations(
                    "classpath:/META-INF/resources/webjars/");
        }
        if (!registry.hasMappingForPattern("/resources/swagger-ui/**")) {
            registry.addResourceHandler("src/main/webapp/resources/swagger-ui/**").addResourceLocations(
                    "classpath:/META-INF/resources/webjars/");
        }
        registry.addResourceHandler("/docs/**").addResourceLocations("classpath:/META-INF/resources/");
    }


    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addRedirectViewController("/docs/v2/api-docs", "/v2/api-docs").setKeepQueryParams(true);
        registry.addRedirectViewController("/docs/swagger-resources/configuration/ui","/swagger-resources/configuration/ui");
        registry.addRedirectViewController("/docs/swagger-resources/configuration/security","/swagger-resources/configuration/security");
        registry.addRedirectViewController("/docs/swagger-resources", "/swagger-resources");
    }


}
