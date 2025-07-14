package pro.shushi.pamirs.framework.connectors.data.autoconfigure.pamirs;

import com.baomidou.mybatisplus.core.MybatisConfiguration;
import com.baomidou.mybatisplus.core.MybatisMapperRegistry;
import com.baomidou.mybatisplus.core.MybatisXMLLanguageDriver;
import com.baomidou.mybatisplus.core.config.GlobalConfig;
import com.baomidou.mybatisplus.core.executor.MybatisBatchExecutor;
import com.baomidou.mybatisplus.core.executor.MybatisCachingExecutor;
import com.baomidou.mybatisplus.core.executor.MybatisReuseExecutor;
import com.baomidou.mybatisplus.core.executor.MybatisSimpleExecutor;
import com.baomidou.mybatisplus.core.toolkit.GlobalConfigUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.binding.MapperMethod;
import org.apache.ibatis.binding.MapperRegistry;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.logging.Log;
import org.apache.ibatis.logging.LogFactory;
import org.apache.ibatis.mapping.Environment;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.reflection.wrapper.ObjectWrapper;
import org.apache.ibatis.scripting.LanguageDriver;
import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.transaction.Transaction;
import pro.shushi.pamirs.framework.connectors.data.autoconfigure.pamirs.extend.PamirsModelBeanWrapper;
import pro.shushi.pamirs.framework.connectors.data.autoconfigure.pamirs.extend.PamirsModelMapWrapper;
import pro.shushi.pamirs.framework.connectors.data.autoconfigure.pamirs.extend.PamirsMybatisXMLLanguageDriver;
import pro.shushi.pamirs.meta.api.Models;
import pro.shushi.pamirs.meta.api.dto.config.ModelConfig;
import pro.shushi.pamirs.meta.api.dto.entity.DataMap;
import pro.shushi.pamirs.meta.api.session.PamirsSession;
import pro.shushi.pamirs.meta.base.D;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * 修改plus逻辑
 * <p>
 * 2020/7/1 4:00 下午
 *
 * @author d@shushi.pro
 * @version 1.0.0
 */
public class PamirsMybatisConfiguration extends MybatisConfiguration {

    private static final Log logger = LogFactory.getLog(PamirsMybatisConfiguration.class);
    /**
     * Mapper 注册
     */
    protected final MybatisMapperRegistry mybatisMapperRegistry = new PamirsMybatisMapperRegistry(this);

    public PamirsMybatisConfiguration(Environment environment) {
        this();
        this.environment = environment;
    }

    private List<String> businessEnumPackages;

    private Boolean usingModelAsProperty = false;

    private GlobalConfig globalConfig = GlobalConfigUtils.defaults().setIdentifierGenerator(new PamirsIdentifierGenerator());

    @Override
    public GlobalConfig getGlobalConfig() {
        return globalConfig;
    }

    @Override
    public void setGlobalConfig(GlobalConfig globalConfig) {
        this.globalConfig = globalConfig;
    }

    /**
     * 初始化调用
     */
    public PamirsMybatisConfiguration() {
        super();
        this.mapUnderscoreToCamelCase = false;
        languageRegistry.setDefaultDriverClass(PamirsMybatisXMLLanguageDriver.class);
    }

    /**
     * MybatisPlus 加载 SQL 顺序：
     * <p>1、加载XML中的SQL</p>
     * <p>2、加载sqlProvider中的SQL</p>
     * <p>3、xmlSql 与 sqlProvider不能包含相同的SQL</p>
     * <p>调整后的SQL优先级：xmlSql > sqlProvider > curdSql</p>
     */
    @Override
    public void addMappedStatement(MappedStatement ms) {
        logger.debug("addMappedStatement: " + ms.getId());
        if (mappedStatements.containsKey(ms.getId())) {
            /*
             * 说明已加载了xml中的节点； 忽略mapper中的SqlProvider数据
             */
            logger.warn("mapper[" + ms.getId() + "] is ignored, because it exists, maybe from xml file");
            return;
        }
        super.addMappedStatement(ms);
    }

    /**
     * 使用自己的 MybatisMapperRegistry
     */
    @Override
    public MapperRegistry getMapperRegistry() {
        return mybatisMapperRegistry;
    }

    /**
     * 使用自己的 MybatisMapperRegistry
     */
    @Override
    public <T> void addMapper(Class<T> type) {
        mybatisMapperRegistry.addMapper(type);
    }

    /**
     * 使用自己的 MybatisMapperRegistry
     */
    @Override
    public void addMappers(String packageName, Class<?> superType) {
        mybatisMapperRegistry.addMappers(packageName, superType);
    }

    /**
     * 使用自己的 MybatisMapperRegistry
     */
    @Override
    public void addMappers(String packageName) {
        mybatisMapperRegistry.addMappers(packageName);
    }

    /**
     * 使用自己的 MybatisMapperRegistry
     */
    @Override
    public <T> T getMapper(Class<T> type, SqlSession sqlSession) {
        return mybatisMapperRegistry.getMapper(type, sqlSession);
    }

    /**
     * 使用自己的 MybatisMapperRegistry
     */
    @Override
    public boolean hasMapper(Class<?> type) {
        return mybatisMapperRegistry.hasMapper(type);
    }

    /**
     * 指定动态SQL生成的默认语言
     *
     * @param driver LanguageDriver
     */
    @Override
    public void setDefaultScriptingLanguage(Class<? extends LanguageDriver> driver) {
        if (driver == null) {
            //todo 替换动态SQL生成的默认语言为自己的。
            driver = MybatisXMLLanguageDriver.class;
        }
        getLanguageRegistry().setDefaultDriverClass(driver);
    }

    @Override
    public Executor newExecutor(Transaction transaction, ExecutorType executorType) {
        executorType = executorType == null ? defaultExecutorType : executorType;
        executorType = executorType == null ? ExecutorType.SIMPLE : executorType;
        Executor executor;
        if (ExecutorType.BATCH == executorType) {
            executor = new MybatisBatchExecutor(this, transaction);
        } else if (ExecutorType.REUSE == executorType) {
            executor = new MybatisReuseExecutor(this, transaction);
        } else {
            executor = new MybatisSimpleExecutor(this, transaction);
        }
        if (cacheEnabled) {
            executor = new MybatisCachingExecutor(executor);
        }
        executor = (Executor) interceptorChain.pluginAll(executor);
        return executor;
    }

    public List<String> getBusinessEnumPackages() {
        return businessEnumPackages;
    }

    public void setBusinessEnumPackages(List<String> businessEnumPackages) {
        this.businessEnumPackages = businessEnumPackages;
    }

    public Boolean getUsingModelAsProperty() {
        return usingModelAsProperty;
    }

    public void setUsingModelAsProperty(Boolean usingModelAsProperty) {
        this.usingModelAsProperty = usingModelAsProperty;
    }

    @SuppressWarnings("unchecked")
    @Override
    public MetaObject newMetaObject(Object object) {
        if (usingModelAsProperty) {
            if (object instanceof MapperMethod.ParamMap || object instanceof ObjectWrapper) {
                return super.newMetaObject(object);
            }
            ModelConfig modelConfig;
            if (object instanceof DataMap) {
                // process GenericMapper
                modelConfig = Optional.ofNullable(PamirsSession.getAsProperty())
                        .filter(StringUtils::isNotBlank)
                        .map(modelModel -> PamirsSession.getContext().getSimpleModelConfig(modelModel))
                        .orElse(null);
            } else {
                // process PamirsMapper
                modelConfig = Optional.ofNullable(Models.api().getModel(object))
                        .filter(StringUtils::isNotBlank)
                        .map(modelModel -> PamirsSession.getContext().getSimpleModelConfig(modelModel))
                        .orElse(null);
            }
            if (modelConfig == null) {
                return super.newMetaObject(object);
            }
            if (object instanceof D) {
                PamirsModelBeanWrapper beanWrapper = new PamirsModelBeanWrapper(modelConfig);
                MetaObject metaObject = MetaObject.forObject(beanWrapper, objectFactory, objectWrapperFactory, reflectorFactory);
                beanWrapper.apply(metaObject, object);
                return metaObject;
            } else if (object instanceof Map) {
                PamirsModelMapWrapper mapWrapper = new PamirsModelMapWrapper(modelConfig, (Map<String, Object>) object);
                MetaObject metaObject = MetaObject.forObject(mapWrapper, objectFactory, objectWrapperFactory, reflectorFactory);
                mapWrapper.apply(metaObject);
                return metaObject;
            }
            return super.newMetaObject(object);
        }
        return super.newMetaObject(object);
    }
}
