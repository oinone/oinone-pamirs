package pro.shushi.pamirs;

import com.alibaba.fastjson.TypeReference;
import com.google.common.collect.Lists;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.context.WebApplicationContext;
import pro.shushi.pamirs.meta.api.core.session.Sessions;
import pro.shushi.pamirs.meta.api.dto.fun.Function;
import pro.shushi.pamirs.meta.api.dto.meta.Meta;
import pro.shushi.pamirs.meta.api.dto.meta.MetaData;
import pro.shushi.pamirs.meta.api.session.PamirsSession;
import pro.shushi.pamirs.meta.common.constants.ModuleConstants;
import pro.shushi.pamirs.meta.common.util.FileUtils;
import pro.shushi.pamirs.meta.domain.model.ModelDefinition;
import pro.shushi.pamirs.meta.domain.module.ModuleDefinition;
import pro.shushi.pamirs.meta.util.JsonUtils;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.TimeZone;

/**
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020-01-09 00:22
 */
@DisplayName("基础服务测试")
//@WebAppConfiguration
@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = {PamirsFrameworkApplication.class})
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
abstract
public class AbstractBaseTest {

    @Autowired
    protected WebApplicationContext context;

    @BeforeAll
    @Order(0)
    @DisplayName("全部开始之前")
    public static void lifecycle() {

    }

    @BeforeEach
    @DisplayName("单个测试方法开始之前")
    public void beforeEach() {
        // 准备数据
        MetaData base = fetchMetaData(ModuleConstants.MODULE_BASE);
        Sessions.fillSession(Lists.newArrayList(base));
    }

    @PostConstruct
    void setDefaultTimezone() {
        TimeZone.setDefault(TimeZone.getTimeZone("Asia/Shanghai"));
    }

    protected Meta fetchMeta(@SuppressWarnings("SameParameterValue") String moduleName) {
        Meta meta = new Meta();
        MetaData metaData = fetchMetaData(moduleName);

        meta.setModule(moduleName);
        //noinspection unchecked,rawtypes
        meta.setData(new HashMap() {
            private static final long serialVersionUID = -7595381457277605768L;

            {   //noinspection unchecked
                put(moduleName, metaData);
            }
        });
        return meta;
    }

    protected MetaData fetchMetaData(String moduleName) {
        MetaData metaData = new MetaData();

        String pathPrefix = moduleName + "/" + moduleName;

        String path = "Model.json";
        String fullDictPath = "classpath:meta/" + pathPrefix + path;
        String json = FileUtils.read(fullDictPath);
        Map<String, ModelDefinition> modelDefinitionMap
                = JsonUtils.parseObject(json, new TypeReference<Map<String, ModelDefinition>>() {
        }.getType());
        if (null != modelDefinitionMap) modelDefinitionMap.values().forEach(metaData::addData);

        path = "Module.json";
        fullDictPath = "classpath:meta/" + pathPrefix + path;
        json = FileUtils.read(fullDictPath);
        Map<String, ModuleDefinition> moduleDefinitionMap
                = JsonUtils.parseObject(json, new TypeReference<Map<String, ModuleDefinition>>() {
        }.getType());
        if (null != moduleDefinitionMap) moduleDefinitionMap.values().forEach(metaData::addData);

        return metaData;
    }

    protected void fixFunction(String namespace, String fun, Class<?> clazz) {
        Function function = Objects.requireNonNull(PamirsSession.getContext()).getFunction(namespace, fun);
        function.setClazz(clazz.getName());
        function.getFunctionDefinition().getArgumentList().forEach(v -> {
            if (namespace.equals(v.getModel())) {
                v.setLtype(clazz.getName());
            }
        });
        if (namespace.equals(function.getReturnType().getModel())) {
            function.getFunctionDefinition().getReturnType().setLtype(clazz.getName());
            function.getFunctionDefinition().getReturnType().setLtypeT(clazz.getName());
        }
    }

    protected void fixFunction(String namespace, String fun, Class<?> clazz, Class<?> functionClass) {
        Function function = Objects.requireNonNull(PamirsSession.getContext()).getFunction(namespace, fun);
        function.setClazz(functionClass.getName());
        function.getFunctionDefinition().getArgumentList().forEach(v -> {
            if (namespace.equals(v.getModel())) {
                v.setLtype(clazz.getName());
            }
        });
        if (namespace.equals(function.getReturnType().getModel())) {
            function.getFunctionDefinition().getReturnType().setLtype(clazz.getName());
            function.getFunctionDefinition().getReturnType().setLtypeT(clazz.getName());
        }
    }

}
