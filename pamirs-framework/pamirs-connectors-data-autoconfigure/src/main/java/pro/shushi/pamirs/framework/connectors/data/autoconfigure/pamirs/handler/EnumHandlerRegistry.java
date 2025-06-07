package pro.shushi.pamirs.framework.connectors.data.autoconfigure.pamirs.handler;

import com.google.common.collect.Lists;
import org.apache.ibatis.session.Configuration;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.framework.connectors.data.autoconfigure.ConfigurationCustomizer;
import pro.shushi.pamirs.framework.connectors.data.autoconfigure.pamirs.PamirsMybatisConfiguration;
import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;
import pro.shushi.pamirs.meta.common.constants.PackageConstants;
import pro.shushi.pamirs.meta.common.enmu.IEnum;
import pro.shushi.pamirs.meta.util.ClassUtils;

import java.util.List;

/**
 * 静态代码枚举处理注册
 * 2020/9/7 4:00 下午
 *
 * @author d@shushi.pro
 * @version 1.0.0
 */
@Slf4j
@Component
public class EnumHandlerRegistry implements ConfigurationCustomizer {

    @Override
    public void customize(Configuration configuration) {
        PamirsMybatisConfiguration mybatisConfiguration = (PamirsMybatisConfiguration) configuration;
        List<String> enumPackages = mybatisConfiguration.getBusinessEnumPackages();
        if (enumPackages == null) {
            enumPackages = Lists.newArrayList(PackageConstants.PACKAGE_PAMIRS);
        }
        if (enumPackages.isEmpty()) {
            return;
        }
        if (log.isDebugEnabled()) {
            long start = System.currentTimeMillis();
            register(mybatisConfiguration, enumPackages);
            log.debug("Enumeration handler register cost time: {}ms", System.currentTimeMillis() - start);
        } else {
            register(mybatisConfiguration, enumPackages);
        }
    }

    private void register(PamirsMybatisConfiguration mybatisConfiguration, List<String> enumPackages) {
        List<Class<?>> enumClazzSet = ClassUtils.getAllClassByPacksAndInterface(enumPackages, IEnum.class);
        for (Class<?> enumClazz : enumClazzSet) {
            mybatisConfiguration.getTypeHandlerRegistry().register(enumClazz, CommonEnumTypeHandler.class);
        }
    }
}
