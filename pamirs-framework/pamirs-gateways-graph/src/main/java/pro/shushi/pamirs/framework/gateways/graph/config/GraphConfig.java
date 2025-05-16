package pro.shushi.pamirs.framework.gateways.graph.config;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import pro.shushi.pamirs.framework.gateways.graph.filter.HttpServletRequestReplacedFilter;

@Configuration
public class GraphConfig {

    // DeferredResult 的是用会导致Stream Closed 错误 原因是处理@RequestBody 的流只是用一次就关闭 这个filter 是保存这个流保证以后的使用
    @Bean
    public FilterRegistrationBean httpServletRequestReplacedRegistration() {
        FilterRegistrationBean registration = new FilterRegistrationBean();
        registration.setFilter(new HttpServletRequestReplacedFilter());
        registration.addUrlPatterns("/pamirs/longpolling/*");
        registration.addInitParameter("paramName", "paramValue");
        registration.setName("httpServletRequestReplacedFilter");
        registration.setOrder(1);
        return registration;
    }
}
