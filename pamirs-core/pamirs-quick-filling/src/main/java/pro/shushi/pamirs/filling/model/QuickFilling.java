package pro.shushi.pamirs.filling.model;

import pro.shushi.pamirs.meta.annotation.Field;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.annotation.sys.Base;
import pro.shushi.pamirs.meta.base.TransientModel;

import java.util.List;

/**
 * @author Gesi at 17:15 on 2025/9/10
 */
@Base
@Model(displayName = "快速填报")
@Model.model(QuickFilling.MODEL_MODEL)
public class QuickFilling extends TransientModel {

    private static final long serialVersionUID = -3010517917431476030L;

    public static final String MODEL_MODEL = "quick.filling.QuickFilling";

    @Field(displayName = "模型")
    private String model;

    @Field(displayName = "字段")
    private List<QuickFillingField> fields;

    @Field(displayName = "解析结果")
    private String values;

    @Field(displayName = "失败信息")
    private List<QuickFillingFailure> failures;

}
