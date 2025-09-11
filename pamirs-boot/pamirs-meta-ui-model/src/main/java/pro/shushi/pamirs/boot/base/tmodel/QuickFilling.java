package pro.shushi.pamirs.boot.base.tmodel;

import pro.shushi.pamirs.meta.annotation.Field;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.api.dto.config.ModelConfig;
import pro.shushi.pamirs.meta.base.TransientModel;

import java.util.List;
import java.util.Map;

/**
 * @author Gesi at 17:15 on 2025/9/10
 */
@Model(displayName = "快速填报")
@Model.model(QuickFilling.MODEL_MODEL)
public class QuickFilling extends TransientModel {

    public static final String MODEL_MODEL = "base.QuickFilling";

    @Field(displayName = "模型")
    private String model;

    @Field(displayName = "快速填报字段表头")
    private List<QuickFillingField> fieldHeaders;

    @Field(displayName = "快速填报内容")
    private String valuesStr;

    @Field(displayName = "快速填报失败")
    private List<QuickFillingFailure> failures;

    private ModelConfig modelConfig;

    /**
     * 快速填报表头
     */
    private Map<String, QuickFillingField> fields;

    /**
     * 快速填报数据
     */
    private List<Map<String, String>> values;

    private List<Object> resultValues;

}
