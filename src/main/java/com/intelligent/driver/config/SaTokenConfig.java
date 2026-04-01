package com.intelligent.driver.config;

import cn.dev33.satoken.interceptor.SaInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class SaTokenConfig implements WebMvcConfigurer {
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        String[] excludePatterns = new String[]{
                "/doc.html",
                "/doc.html/**",
                "/swagger-ui/**",
                "/swagger-ui.html",
                "/v3/api-docs/**",
                "/swagger-resources/**",
                "/webjars/**",
                "/api-docs/**",
                "/adminsystem/doc.html",
                "/adminsystem/doc.html/**",
                "/adminsystem/swagger-ui/**",
                "/adminsystem/swagger-ui.html",
                "/adminsystem/v3/api-docs/**",
                "/adminsystem/swagger-resources/**",
                "/adminsystem/webjars/**",
                "/adminsystem/api-docs/**",
                "/adminsystem/api/**",
                "/api/**",
                "/test/**",
                "/sayHello"
        };

        registry.addInterceptor(new SaInterceptor())
                .addPathPatterns("/**")
                .excludePathPatterns(excludePatterns);
    }
}
