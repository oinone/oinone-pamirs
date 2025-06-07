package pro.shushi.pamirs.eip.api.model;

import pro.shushi.pamirs.meta.annotation.Field;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.annotation.sys.Base;
import pro.shushi.pamirs.meta.base.common.CodeModel;

/**
 * EipConnGroup
 *
 * @author yakir on 2023/03/29 15:04.
 */
@Base
@Model(displayName = "连接器分组", labelFields = "name")
@Model.model(EipConnGroup.MODEL_MODEL)
@Model.Advanced(unique = {"name"})
@Model.Code(sequence = "ORDERLY_SEQ", prefix = "DOM", size = 5, initial = 1)
public class EipConnGroup extends CodeModel {

    private static final long serialVersionUID = -3876744931960332754L;

    public final static String MODEL_MODEL = "eip.EipConnGroup";

    @Field(displayName = "名称", translate = true)
    @Field.String
    private String name;

    @Field.Text
    @Field(displayName = "描述", translate = true)
    private String description;
}
