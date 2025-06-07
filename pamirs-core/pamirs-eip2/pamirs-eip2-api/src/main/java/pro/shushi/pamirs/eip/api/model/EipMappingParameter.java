package pro.shushi.pamirs.eip.api.model;

import pro.shushi.pamirs.meta.annotation.Field;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.annotation.sys.Base;
import pro.shushi.pamirs.meta.base.TransientModel;

/**
 * 映射参数定义
 *
 * @author Adamancy Zhang at 19:20 on 2021-06-09
 */
@Base
@Model.model(EipMappingParameter.MODEL_MODEL)
@Model(displayName = "映射参数定义", labelFields = "from")
public class EipMappingParameter extends TransientModel {

    private static final long serialVersionUID = 7592407189452876410L;

    public static final String MODEL_MODEL = "pamirs.eip.EipMappingParameter";

    public EipMappingParameter() {
    }

    public EipMappingParameter(String from, String to) {
        this.setFrom(from);
        this.setTo(to);
    }

    @Field.String
    @Field(displayName = "查找值", required = true)
    private String from;

    @Field.String
    @Field(displayName = "目标值", required = true)
    private String to;

    @SuppressWarnings({"CloneDoesntDeclareCloneNotSupportedException", "MethodDoesntCallSuperMethod"})
    @Override
    protected EipMappingParameter clone() {
        return new EipMappingParameter()
                .setFrom(getFrom())
                .setTo(getTo());
    }
}
