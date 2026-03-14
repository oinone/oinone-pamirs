package pro.shushi.pamirs.locale.configure;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.AutoConfigureOrder;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.context.MessageSourceProperties;
import org.springframework.boot.autoconfigure.web.WebProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.DispatcherServlet;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.i18n.AcceptHeaderLocaleResolver;
import org.springframework.web.servlet.i18n.FixedLocaleResolver;
import pro.shushi.pamirs.locale.bundle.PamirsMessageSource;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Locale;
import java.util.Set;

/**
 * Pamirs MessageSource Configure
 *
 * @author Adamancy Zhang at 14:14 on 2026-03-13
 * @see org.springframework.boot.autoconfigure.context.MessageSourceAutoConfiguration
 */
@Configuration(proxyBeanMethods = false)
@AutoConfigureOrder(Ordered.HIGHEST_PRECEDENCE)
@EnableConfigurationProperties
public class PamirsMessageSourceConfigure {

    public static final String I18N_NORMAL_BASENAME = "i18n/messages";

    public static final String I18N_METADATA_BASENAME = "pamirs/i18n";

    @Bean
    @ConditionalOnMissingBean(name = DispatcherServlet.LOCALE_RESOLVER_BEAN_NAME)
    public LocaleResolver localeResolver(
            @Value("${spring.mvc.locale-resolver:}") WebProperties.LocaleResolver localeResolver,
            @Value("${spring.mvc.locale:}") Locale locale
    ) {
        if (locale == null) {
            locale = Locale.getDefault();
        }
        if (localeResolver == WebProperties.LocaleResolver.FIXED) {
            return new FixedLocaleResolver(locale);
        }
        AcceptHeaderLocaleResolver webLocaleResolver = new AcceptHeaderLocaleResolver();
        webLocaleResolver.setDefaultLocale(locale);
        return webLocaleResolver;
    }

    @Bean
    @ConfigurationProperties(prefix = "spring.messages")
    public MessageSourceProperties messageSourceProperties() {
        MessageSourceProperties properties = new MessageSourceProperties();
        properties.setBasename(StringUtils.arrayToCommaDelimitedString(new String[]{I18N_NORMAL_BASENAME, I18N_METADATA_BASENAME}));
        properties.setEncoding(StandardCharsets.UTF_8);
        properties.setAlwaysUseMessageFormat(false);
        properties.setUseCodeAsDefaultMessage(false);
        return properties;
    }

    @Bean
    public MessageSource messageSource(MessageSourceProperties properties) {
        PamirsMessageSource messageSource = new PamirsMessageSource();

        Set<String> basenameSet = new LinkedHashSet<>(Arrays.asList(StringUtils.commaDelimitedListToStringArray(StringUtils.trimAllWhitespace(properties.getBasename()))));
        basenameSet.add(I18N_NORMAL_BASENAME);
        basenameSet.add(I18N_METADATA_BASENAME);
        messageSource.setBasenames(basenameSet.toArray(new String[0]));

        if (properties.getEncoding() != null) {
            messageSource.setDefaultEncoding(properties.getEncoding().name());
        }
        messageSource.setFallbackToSystemLocale(properties.isFallbackToSystemLocale());
        Duration cacheDuration = properties.getCacheDuration();
        if (cacheDuration != null) {
            messageSource.setCacheSeconds((int) cacheDuration.getSeconds());
        }
        messageSource.setAlwaysUseMessageFormat(properties.isAlwaysUseMessageFormat());
        messageSource.setUseCodeAsDefaultMessage(properties.isUseCodeAsDefaultMessage());

        return messageSource;
    }
}
