package pro.shushi.pamirs.resource.api.model;

import org.apache.commons.collections4.CollectionUtils;
import pro.shushi.pamirs.framework.connectors.data.sql.Pops;
import pro.shushi.pamirs.meta.annotation.Field;
import pro.shushi.pamirs.meta.annotation.Function;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.api.Models;
import pro.shushi.pamirs.meta.base.IdModel;
import pro.shushi.pamirs.meta.domain.module.ModuleDefinition;
import pro.shushi.pamirs.meta.enmu.TtypeEnum;

import java.util.List;
import java.util.stream.Collectors;

@Model.model(ResourceConfig.MODEL_MODEL)
@Model.Advanced(name = "resourceConfig")
@Model(displayName = "配置表")
public class ResourceConfig extends IdModel {

    public static final String MODEL_MODEL = "resource.ResourceConfig";

    @Field.String
    @Field(unique = true, displayName = "键")
    private String key;

    @Field.Text
    @Field(displayName = "值")
    private String value;

    @Field.Enum
    @Field(displayName = "字段类型")
    private TtypeEnum ttype;

    @Field(displayName = "属于哪个应用")
    private ModuleDefinition module;

    @Function
    public static ResourceConfig fetchConfigValue(String key) {
        //根据key查value
        ResourceConfig config = new ResourceConfig();
        config.setKey(key);
        config = config.queryOne();
//        PageCondition<ResourceConfig> pageCondition = new PageCondition<>(ResourceConfig.class);
//        pageCondition.setSize(Integer.MAX_VALUE);
//        List<ResourceConfig> configs = config.queryList(pageCondition);
//        if (CollectionUtils.isNotEmpty(configs)) {
//            return configs.get(0);
//        }
        return config;
    }

    @Function
    public static ResourceConfig pushConfigValue(String key, String value) throws Exception {
        //根据key查value
        List<ResourceConfig> configs = Models.data().queryListByWrapper(Pops.<ResourceConfig>lambdaQuery().eq(ResourceConfig::getKey, key));
        ResourceConfig config = new ResourceConfig()
                .setKey(key)
                .setValue(value);
        if (CollectionUtils.isNotEmpty(configs)) {
            if (configs.size() > 1) {
                List<Long> idList = configs.stream().map(IdModel::getId).collect(Collectors.toList());
                config.deleteByWrapper(Pops.<ResourceConfig>lambdaQuery().in(ResourceConfig::getId, idList));
                config.create();
            } else {
                config.setId(configs.get(0).getId());
                config.updateById();
            }
        } else {
            config.create();
        }
        return config;
    }
}
