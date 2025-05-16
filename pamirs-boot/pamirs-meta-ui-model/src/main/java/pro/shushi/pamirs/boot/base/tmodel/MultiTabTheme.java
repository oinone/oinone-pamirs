package pro.shushi.pamirs.boot.base.tmodel;

import pro.shushi.pamirs.meta.annotation.Field;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.annotation.sys.Base;
import pro.shushi.pamirs.meta.base.TransientModel;

/**
 * @author wangxian
 * date 2023/12/13
 */
@Base
@Model(displayName = "多tab栏样式")
@Model.model(MultiTabTheme.MODEL_MODEL)
public class MultiTabTheme extends TransientModel {

    public static final String MODEL_MODEL = "base.config.MultiTabTheme";

    @Field.Boolean
    @Field(displayName = "内联", index = true)
    private Boolean inline;

    @Base
    @Field(displayName = "样式")
    private String theme;
}
