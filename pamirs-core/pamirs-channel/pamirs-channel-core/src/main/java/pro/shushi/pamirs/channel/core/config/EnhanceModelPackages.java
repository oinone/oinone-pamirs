package pro.shushi.pamirs.channel.core.config;

import com.google.common.collect.Lists;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * EnhanceModelPackages
 *
 * @author yakir on 2022/09/06 20:15.
 */
@Configuration
@ConfigurationProperties(prefix = "pamirs.channel")
public class EnhanceModelPackages {

    private List<String> packages;

    public List<String> basePackages() {
        List<String> basePackages = Optional.ofNullable(packages)
                .map(List::stream)
                .orElse(Stream.empty())
                .filter(StringUtils::isNotBlank)
                .distinct()
                .collect(Collectors.toList());

        if (basePackages.isEmpty()) {
            basePackages = Lists.newArrayList("pro.shushi");
        }

        return basePackages;
    }

    public List<String> getPackages() {
        return packages;
    }

    public EnhanceModelPackages setPackages(List<String> packages) {
        this.packages = packages;
        return this;
    }
}
