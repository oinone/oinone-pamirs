package pro.shushi.pamirs.boot.base.model;

import pro.shushi.pamirs.meta.annotation.Field;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.annotation.sys.Base;
import pro.shushi.pamirs.meta.base.IdModel;
import pro.shushi.pamirs.meta.enmu.NullableBoolEnum;

/**
 * 草稿
 *
 * @author Gesi at 10:19 on 2025/9/1
 */
@Base
@Model.model(Draft.MODEL_MODEL)
@Model(displayName = "草稿")
@Model.Advanced(unique = {"viewIdentifier,userId"})
public class Draft extends IdModel {

    public static final String MODEL_MODEL = "base.Draft";

    @Base
    @Field(displayName = "页面唯一标识", summary = "一般是ui页面名+数据唯一键", required = true)
    private String viewIdentifier;

    @Base
    @Field(displayName = "草稿用户id", required = true)
    private Long userId;

    @Base
    @Field(displayName = "草稿内容")
    @Field.Advanced(columnDefinition = "LONGTEXT")
    private String draftContent;

    @Base
    @Field(displayName = "是否查询到草稿", store = NullableBoolEnum.FALSE)
    private Boolean hasDraft;

}
