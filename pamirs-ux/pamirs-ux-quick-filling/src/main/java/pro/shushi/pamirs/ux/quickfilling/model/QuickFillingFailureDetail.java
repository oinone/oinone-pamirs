package pro.shushi.pamirs.ux.quickfilling.model;

import pro.shushi.pamirs.meta.annotation.Field;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.annotation.sys.Base;
import pro.shushi.pamirs.meta.base.TransientModel;

/**
 * @author Gesi at 17:15 on 2025/9/10
 */
@Base
@Model(displayName = "快速填报失败详情")
@Model.model(QuickFillingFailureDetail.MODEL_MODEL)
public class QuickFillingFailureDetail extends TransientModel {

    private static final long serialVersionUID = -6955053639158877696L;

    public static final String MODEL_MODEL = "quick.filling.QuickFillingFailureDetail";

    @Field(displayName = "失败字段")
    private String field;

    @Field(displayName = "失败原因")
    private String msg;

}
