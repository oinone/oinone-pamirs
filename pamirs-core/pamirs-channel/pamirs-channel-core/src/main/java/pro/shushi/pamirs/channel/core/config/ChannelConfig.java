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
 * ChannelConfig
 *
 * @author yakir on 2022/09/06 20:15.
 */
@Configuration
@ConfigurationProperties(prefix = "pamirs.channel")
public class ChannelConfig {

    private List<String> packages;

    private int threadSize = 0;

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

    public ChannelConfig setPackages(List<String> packages) {
        this.packages = packages;
        return this;
    }

    public int getThreadSize() {
        return threadSize;
    }

    public ChannelConfig setThreadSize(int threadSize) {
        this.threadSize = threadSize;
        return this;
    }
}
