package pro.shushi.pamirs.meta.api.dto.config;

import pro.shushi.pamirs.meta.annotation.fun.Data;
import pro.shushi.pamirs.meta.api.core.configure.yaml.data.model.PamirsTableInfo;
import pro.shushi.pamirs.meta.api.dto.fun.Function;
import pro.shushi.pamirs.meta.domain.model.SequenceConfig;
import pro.shushi.pamirs.meta.enumclass.ModelTypeEnumCls;

import java.util.List;

/**
 *
 * 模型配置
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/1/15 2:45 下午
 *
 */
@Data
public class ModelConfig {

    private String module;

    private String name;

    private String model;

    private String lname;

    private String shardingName;

    private String database;

    private String table;

    private ModelTypeEnumCls type;

    private SequenceConfig sequenceConfig;

    private Boolean managed;

    private List<String> inherited;

    private List<String/*field*/> pk;

    private List<String> unInheritedField;

    private List<String> superModels;

    private List<ModelFieldConfig> modelFieldConfigList;

    private List<Function> functionList;

    private PamirsTableInfo pamirsTableInfo;

}
