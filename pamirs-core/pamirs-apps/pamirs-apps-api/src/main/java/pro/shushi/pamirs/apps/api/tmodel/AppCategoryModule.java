package pro.shushi.pamirs.apps.api.tmodel;

import pro.shushi.pamirs.meta.annotation.Field;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.annotation.sys.Base;
import pro.shushi.pamirs.meta.base.TransientModel;
import pro.shushi.pamirs.meta.enmu.ModelTypeEnum;

/**
 * AppCategoryModule
 *
 * @author yakir on 2022/11/28 19:51.
 */
@Base
@Model(displayName = "初始化租户应用")
@Model.model(AppCategoryModule.MODEL_MODEL)
@Model.Advanced(type = ModelTypeEnum.TRANSIENT)
public class AppCategoryModule extends TransientModel {

    private static final long serialVersionUID = -373366156952362175L;

    public final static String MODEL_MODEL = "apps.AppCategoryModule";

    @Field(displayName = "进度", summary = "0-100")
    private Integer progress;

    @Field.String
    @Field(displayName = "模块跳转url")
    private String redirectUrl;

    @Field.String
    @Field(displayName = "模块名称", summary = "B2C(在线商城)、DMS(分销协同)、OMS(全渠道零售)")
    private String name;

    @Field.String(size = 512)
    @Field(displayName = "模块图标")
    private String icon;

    @Field.String
    @Field(displayName = "层级")
    private String level;

    @Field.String
    @Field(displayName = "模块module")
    private String module;

    @Field.Text
    @Field(displayName = "描述", summary = "描述详情")
    private String description;

}
