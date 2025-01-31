package com.homework.rest_security_final.rest_security_final.config;

import com.homework.rest_security_final.rest_security_final.converter.CsvHttpMessageConverter;
import com.homework.rest_security_final.rest_security_final.resolver.PageableResolver;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.ContentNegotiationConfigurer;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void configureContentNegotiation(ContentNegotiationConfigurer configurer) {
        configurer.parameterName("format")
                .favorParameter(true).defaultContentType(MediaType.APPLICATION_JSON)
                .mediaType("json", MediaType.APPLICATION_JSON)
                .mediaType("xml", MediaType.APPLICATION_XML);
    }

    @Override
    public void extendMessageConverters(List<HttpMessageConverter<?>> converters) {
        converters.add(new CsvHttpMessageConverter());
    }

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(new PageableResolver());
    }

    // 단순 view return controller는 여기에 등록해서 사용함.
    @Override
    public void addViewControllers(ViewControllerRegistry viewControllerRegistry) {
        viewControllerRegistry.addViewController("/loginForm").setViewName("loginForm");
        viewControllerRegistry.addViewController("/403").setViewName("403");
        viewControllerRegistry.addViewController("/admin/home").setViewName("admin");
        viewControllerRegistry.addViewController("/member/home").setViewName("member");
        viewControllerRegistry.addViewController("/google/home").setViewName("google");
    }

    // UserDetailsService에서 사용할 PasswordEncoder를 bean으로 등록.
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

}
