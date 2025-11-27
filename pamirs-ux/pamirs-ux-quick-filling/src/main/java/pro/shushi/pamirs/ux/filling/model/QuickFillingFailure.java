package pro.shushi.pamirs.ux.filling.model;

import pro.shushi.pamirs.meta.annotation.Field;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.annotation.sys.Base;
import pro.shushi.pamirs.meta.base.TransientModel;

import java.util.List;

/**
 * @author Gesi at 17:15 on 2025/9/10
 */
@Base
@Model(displayName = "快速填报失败")
@Model.model(QuickFillingFailure.MODEL_MODEL)
public class QuickFillingFailure extends TransientModel {

    private static final long serialVersionUID = 6302008675851251424L;

    public static final String MODEL_MODEL = "quick.filling.QuickFillingFailure";

    @Field(displayName = "行号")
    private Integer rowNumber;

    @Field(displayName = "失败详情")
    private List<QuickFillingFailureDetail> detailList;

}
