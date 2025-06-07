package pro.shushi.pamirs.framework.compute.sequence;

import org.springframework.stereotype.Component;
import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;
import pro.shushi.pamirs.meta.api.core.compute.systems.type.gen.VersionGenerator;

/**
 * 版本号生成默认实现
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/3/4 2:48 上午
 */
@Slf4j
@Component
public class DefaultVersionGenerator implements VersionGenerator {

    @Override
    public String generate() {
        return "1.0.0";
    }

    @Override
    public String platformVersion(String version) {
        return null;
    }

    @Override
    public String littleVersion(String version) {
        return null;
    }

}
