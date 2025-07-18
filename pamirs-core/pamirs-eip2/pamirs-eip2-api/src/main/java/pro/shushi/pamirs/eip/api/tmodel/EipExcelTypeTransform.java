package pro.shushi.pamirs.eip.api.tmodel;

import pro.shushi.pamirs.meta.annotation.Field;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.base.TransientModel;
import pro.shushi.pamirs.meta.enmu.ModelTypeEnum;

/**
 * @author Gesi at 13:42 on 2025/7/18
 */
@Model(displayName = "集成接口Excel类型转换")
@Model.model(EipExcelTypeTransform.MODEL_MODEL)
@Model.Advanced(type = ModelTypeEnum.TRANSIENT)
public class EipExcelTypeTransform extends TransientModel {

    public static final String MODEL_MODEL = "pamirs.eip.tmodel.EipTypeTransform";

    @Field(displayName = "下标")
    private Integer index;

    @Field(displayName = "字段名")
    private String name;

    @Field(displayName = "原始类型")
    private String originType;

    @Field(displayName = "格式")
    private String format;

    @Field(displayName = "目标类型")
    private String targetType;

}
