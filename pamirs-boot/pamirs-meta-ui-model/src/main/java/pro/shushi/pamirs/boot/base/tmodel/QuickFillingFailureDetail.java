package pro.shushi.pamirs.boot.base.tmodel;

import pro.shushi.pamirs.boot.base.enmu.QuickFillingFailCodeEnum;
import pro.shushi.pamirs.meta.annotation.Field;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.base.TransientModel;

/**
 * @author Gesi at 17:15 on 2025/9/10
 */
@Model(displayName = "快速填报失败详情")
@Model.model(QuickFillingFailureDetail.MODEL_MODEL)
public class QuickFillingFailureDetail extends TransientModel {

    public static final String MODEL_MODEL = "base.QuickFillingFailureDetail";

    @Field(displayName = "失败字段")
    private String field;

    @Field(displayName = "源填写值")
    private String originValue;

    @Field(displayName = "失败原因编码")
    private QuickFillingFailCodeEnum code;

    @Field(displayName = "失败原因")
    private String msg;

    private boolean failed;

    public void fail(QuickFillingFailCodeEnum code, String value) {
        setFailed(true);
        setOriginValue(value);
        if (QuickFillingFailCodeEnum.TYPE_INCOMPATIBLE.equals(code)) {
            fail(QuickFillingFailCodeEnum.TYPE_INCOMPATIBLE, value, "数据不符合规则，请修改后继续");
            return;
        }
    }

    public void fail(QuickFillingFailCodeEnum code, String value, String msg) {
        setFailed(true);
        setOriginValue(value);
        setCode(code);
        setMsg(msg);
    }

}
