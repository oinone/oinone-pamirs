package pro.shushi.pamirs.boot.base.ux.service;

import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.rpc.RpcException;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.boot.base.ux.entity.UxModelEntity;
import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;
import pro.shushi.pamirs.meta.api.Fun;
import pro.shushi.pamirs.meta.api.dto.config.ModelConfig;
import pro.shushi.pamirs.meta.api.dto.fun.Function;
import pro.shushi.pamirs.meta.api.session.PamirsSession;
import pro.shushi.pamirs.meta.common.exception.PamirsException;
import pro.shushi.pamirs.meta.common.spi.HoldKeeper;
import pro.shushi.pamirs.meta.common.spring.BeanDefinitionUtils;
import pro.shushi.pamirs.meta.common.util.AppClassLoader;
import pro.shushi.pamirs.meta.enmu.MetaExpEnumerate;
import pro.shushi.pamirs.meta.util.ClassUtils;

import java.util.HashMap;
import java.util.Optional;

import static pro.shushi.pamirs.meta.constant.ModuleFunctionConstants.ClassMetadataFunction.BEAN_NAME;
import static pro.shushi.pamirs.meta.constant.ModuleFunctionConstants.ClassMetadataFunction.GET_CLASS_METADATA_FUN;

/**
 * 获取 UxModel 元数据远程服务
 *
 * @author Adamancy Zhang at 15:10 on 2025-06-16
 */
@Slf4j
@Component(BEAN_NAME)
public class UxClassMetadataFetcher {

    private static final HoldKeeper<UxClassMetadataFetcher> UX_CLASS_METADATA_FETCHER_HOLDER = new HoldKeeper<>();

    public static UxClassMetadataFetcher getApi() {
        return UX_CLASS_METADATA_FETCHER_HOLDER.supply(() -> BeanDefinitionUtils.getBean(UxClassMetadataFetcher.class));
    }

    public UxModelEntity getClassMetadataByModel(ModelConfig modelConfig) {
        String className = Optional.of(modelConfig)
                .map(ModelConfig::getLname)
                .orElse(null);
        if (StringUtils.isBlank(className) || HashMap.class.getName().equals(className)) {
            return UxModelEntity.wrap(modelConfig);
        }
        String module = Optional.of(modelConfig)
                .map(ModelConfig::getModule)
                .orElse(null);
        if (StringUtils.isBlank(module)) {
            return getClassMetadata(className);
        }
        Function function = PamirsSession.getContext().getFunctionAllowNull(module, GET_CLASS_METADATA_FUN);
        if (function == null) {
            return getClassMetadata(className);
        }
        try {
            return Fun.run(function, className);
        } catch (RpcException e) {
            log.warn("fetch remote class metadata error, using metadata cache. {}", className);
            return UxModelEntity.wrap(modelConfig);
        }
    }

    public UxModelEntity getClassMetadata(String className) {
        try {
            return UxModelEntity.wrap(Class.forName(className, false, AppClassLoader.getClassLoader(ClassUtils.class)));
        } catch (ClassNotFoundException e) {
            throw PamirsException.construct(MetaExpEnumerate.BASE_CLASS_NOT_FOUNT_ERROR, e).errThrow();
        }
    }
}
