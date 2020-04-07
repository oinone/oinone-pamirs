package pro.shushi.pamirs.meta.api.dto.config;

import pro.shushi.pamirs.meta.annotation.fun.Data;
import pro.shushi.pamirs.meta.domain.fun.CheckExpression;
import pro.shushi.pamirs.meta.domain.model.SequenceConfig;

import java.util.List;

/**
 *
 * 字段配置
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/1/15 2:45 下午
 *
 */
@Data
public class ModelFieldConfig {

    private String model;

    private String modelName;

    private String name;

    private String field;

    private String column;

    private String ttype;

    private String relatedTtype;

    private String lname;

    private String ltype;

    private String ltypeT;

    private Integer size;

    private Short decimal;

    private Boolean natived = Boolean.TRUE;

    private Boolean multi;

    private Boolean store;

    private String dictionary;

    private Boolean relationStore;

    private String format;

    private String defaultValue;

    private String compute;

    private String inverse;

    private String serialize;

    private SequenceConfig sequenceConfig;

    private List<String> checks;

    private List<CheckExpression> rules;

    private List<String> related;

    private List<String> relationFields;

    private String references;

    private String through;

    private List<String> referenceFields;

}
