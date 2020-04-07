package pro.shushi.pamirs.meta.api;

import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.Configuration;
import pro.shushi.pamirs.meta.api.core.systems.type.gen.IdGenerator;
import pro.shushi.pamirs.meta.api.core.systems.type.gen.SequenceGenerator;
import pro.shushi.pamirs.meta.api.core.systems.type.gen.SequenceProcessor;
import pro.shushi.pamirs.meta.api.core.systems.type.gen.VersionGenerator;
import pro.shushi.pamirs.meta.api.core.systems.constraint.CheckProcessor;
import pro.shushi.pamirs.meta.api.core.systems.enmu.EnumProcessor;
import pro.shushi.pamirs.meta.api.core.systems.type.TypeProcessor;
import pro.shushi.pamirs.meta.common.constants.BeanNameConstants;
import pro.shushi.pamirs.meta.common.constants.CharacterConstants;
import pro.shushi.pamirs.meta.common.spring.BeanDefinitionUtils;
import pro.shushi.pamirs.meta.common.util.PStringUtils;

/**
 * 元模型Common api工厂
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/2/23 5:49 下午
 */
@Configuration
public class MetaApiFactory {

    public static <API> API getApi(Class<API> interfaceClass){
        if(null != interfaceClass){
            MetaApiConfigure configure = BeanDefinitionUtils.getBean(BeanNameConstants.META_API_CONFIGURE, MetaApiConfigure.class);
            String beanName = configure.getApiMap().get(interfaceClass.getName());
            if(StringUtils.isBlank(beanName)){
                beanName = BeanNameConstants.DEFAULT + interfaceClass.getSimpleName();
            }
            return (API) BeanDefinitionUtils.getBean(beanName);
        }
        return null;
    }

    public static <API> API getApi(Class<API> interfaceClass, String implClassName){
        if(StringUtils.isNotBlank(implClassName)){
            return (API) BeanDefinitionUtils.getBean(PStringUtils.camelCaseFromModel(implClassName));
        }
        return null;
    }

    // 类型系统
    public static TypeProcessor getTypeProcessor(){
        return getApi(TypeProcessor.class);
    }

    // 约束校验系统
    public static CheckProcessor getCheckProcessor(){
        return getApi(CheckProcessor.class);
    }

    // 枚举系统
    public static <T,D> EnumProcessor<T, D> getEnumProcessor(){
        return getApi(EnumProcessor.class);
    }

    // ID 生成器
    public static IdGenerator getIdGenerator() {
        return getApi(IdGenerator.class);
    }

    // 序列 生成器
    public static SequenceProcessor getSequenceProcessor() {
        return getApi(SequenceProcessor.class);
    }

    // 版本生成器
    public static VersionGenerator getVersionGenerator() {
        return getApi(VersionGenerator.class);
    }

}
