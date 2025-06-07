package pro.shushi.pamirs.apps.api.tmodel;

import pro.shushi.pamirs.meta.annotation.Field;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.annotation.sys.Base;
import pro.shushi.pamirs.meta.base.TransientModel;

/**
 * fixme pro.shushi.pamirs.welcome.api.tmodel.WelcomeDataModel
 */
@Base
@Model.model(TechniqueViValue.MODEL_MODEL)
@Model(displayName = "数据信息")
public class TechniqueViValue extends TransientModel {

    public static final String MODEL_MODEL = "apps.TechniqueViValue";

    /**
     * 数据常量名统一维护成常量
     *
     * @see pro.shushi.pamirs.apps.api.constant.TechniqueViConstants
     */
    @Field.String
    @Field(displayName = "数据名称", translate = true)
    private String name;

    @Field.String
    @Field(displayName = "编码")
    private String code;

    @Field.String
    @Field(displayName = "值")
    private String value;

    @Field.Text
    @Field(displayName = "定位")
    private String position;
}
