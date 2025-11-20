package pro.shushi.pamirs.sso.client.autoconfigure;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import pro.shushi.pamirs.sso.client.config.PamirsSsoProperties;
import pro.shushi.pamirs.sso.client.interceptor.PamirsSsoInterceptor;

@Configuration
@EnableConfigurationProperties(PamirsSsoProperties.class)
@ConditionalOnProperty(prefix = "pamirs.sso", name = "server-url")
public class PamirsSsoAutoConfiguration {

    private final PamirsSsoProperties properties;

    public PamirsSsoAutoConfiguration(PamirsSsoProperties properties) {
        this.properties = properties;
    }

    @Bean
    public PamirsSsoInterceptor pamirsSsoInterceptor() {
        return new PamirsSsoInterceptor(properties);
    }

    @Bean
    public WebMvcConfigurer pamirsSsoWebMvcConfigurer(PamirsSsoInterceptor interceptor) {
        return new WebMvcConfigurer() {
            @Override
            public void addInterceptors(InterceptorRegistry registry) {
                registry.addInterceptor(interceptor).addPathPatterns("/**");
            }
        };
    }
}