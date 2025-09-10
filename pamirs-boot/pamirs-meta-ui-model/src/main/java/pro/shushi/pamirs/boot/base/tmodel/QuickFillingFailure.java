package pro.shushi.pamirs.boot.base.tmodel;

import pro.shushi.pamirs.meta.annotation.Field;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.base.TransientModel;

import java.util.List;

/**
 * @author Gesi at 17:15 on 2025/9/10
 */
@Model(displayName = "快速填报失败")
@Model.model(QuickFillingFailure.MODEL_MODEL)
public class QuickFillingFailure extends TransientModel {

    public static final String MODEL_MODEL = "base.QuickFillingFailure";

    @Field(displayName = "行号")
    private Integer rowNumber;

    @Field(displayName = "失败详情")
    private List<QuickFillingFailureDetail> detailList;

}
