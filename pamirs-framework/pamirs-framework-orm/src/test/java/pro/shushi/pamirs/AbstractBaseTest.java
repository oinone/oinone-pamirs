package pro.shushi.pamirs;

import com.alibaba.fastjson.TypeReference;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.context.WebApplicationContext;
import pro.shushi.pamirs.framework.connectors.data.mapper.GenericMapper;
import pro.shushi.pamirs.meta.api.dto.meta.Meta;
import pro.shushi.pamirs.meta.api.dto.meta.MetaData;
import pro.shushi.pamirs.meta.domain.model.ModelDefinition;
import pro.shushi.pamirs.meta.domain.module.ModuleDefinition;
import pro.shushi.pamirs.meta.common.util.FileUtils;
import pro.shushi.pamirs.meta.util.JsonUtils;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;
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

    @MockBean
    private GenericMapper genericMapper;

    @BeforeAll
    @Order(0)
    @DisplayName("全部开始之前")
    public static void lifecycle() {

    }

    @BeforeEach
    @DisplayName("单个测试方法开始之前")
    public void beforeEach() {

    }

    @PostConstruct
    void setDefaultTimezone() {
        TimeZone.setDefault(TimeZone.getTimeZone("Asia/Shanghai"));
    }

    protected Meta fetchMeta(@SuppressWarnings("SameParameterValue") String module){
        Meta meta = new Meta();
        MetaData metaData = fetchMetaData(module);

        meta.setModule(module);
        //noinspection unchecked,rawtypes
        meta.setData(new HashMap(){
            private static final long serialVersionUID = -7595381457277605768L;

            {   //noinspection unchecked
                put(module, metaData);
            }
        });
        return meta;
    }

    protected MetaData fetchMetaData(String module){
        MetaData metaData = new MetaData();

        String pathPrefix = module + "/" + module;

        String path = "Model.json";
        String fullDictPath = "classpath:meta/" + pathPrefix + path;
        String json = FileUtils.read(fullDictPath);
        Map<String, ModelDefinition> modelDefinitionMap
                = JsonUtils.parseObject(json, new TypeReference<Map<String, ModelDefinition>>(){}.getType());
        if(null != modelDefinitionMap)modelDefinitionMap.values().forEach(metaData::addData);

        path = "Module.json";
        fullDictPath = "classpath:meta/" + pathPrefix + path;
        json = FileUtils.read(fullDictPath);
        Map<String, ModuleDefinition> moduleDefinitionMap
                = JsonUtils.parseObject(json, new TypeReference<Map<String, ModuleDefinition>>(){}.getType());
        if(null != moduleDefinitionMap)moduleDefinitionMap.values().forEach(metaData::addData);

        return metaData;
    }

}
