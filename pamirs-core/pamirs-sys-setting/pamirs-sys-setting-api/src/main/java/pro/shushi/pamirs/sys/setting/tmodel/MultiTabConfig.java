package pro.shushi.pamirs.sys.setting.tmodel;

import pro.shushi.pamirs.meta.annotation.Field;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.annotation.sys.Base;
import pro.shushi.pamirs.meta.base.TransientModel;

/**
 * @author Wuxin
 * @Date 2024/6/26
 * @since 1.0
 */
@Base
@Model(displayName = "多tab栏样式")
@Model.model(MultiTabConfig.MODEL_MODEL)
public class MultiTabConfig extends TransientModel {
    private static final long serialVersionUID = -7347127626990783381L;

    public static final String MODEL_MODEL = "sysSetting.multiTabConfig";


    @Field.Boolean
    @Field(displayName = "内联", index = true)
    private Boolean inline;

    @Base
    @Field(displayName = "样式")
    private String theme;

    @Field.Boolean
    @Field(displayName = "多标签页")
    private Boolean enabled;

    @Field.Boolean
    @Field(displayName = "拖拽排序")
    private Boolean draggable;

    @Field.Boolean
    @Field(displayName = "显示模块Logo")
    private Boolean showModuleLogo;

    @Field(displayName = "应用主标签页")
    private MultiTabsApplicationHomepageConfig homepage;

    @Field.Boolean
    @Field(displayName = "最多标签页数量")
    private Integer maxCount;

}
