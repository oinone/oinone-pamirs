package pro.shushi.pamirs.boot.base.model;

import pro.shushi.pamirs.meta.annotation.Field;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.annotation.sys.Base;
import pro.shushi.pamirs.meta.enmu.ModelTypeEnum;

/**
 * 分享页面动作
 *
 * @author Adamancy Zhang at 15:37 on 2024-04-12
 */
@Base
@Model.Advanced(type = ModelTypeEnum.PROXY)
@Model.model(SharedPageViewAction.MODEL_MODEL)
@Model(displayName = "分享页面动作")
public class SharedPageViewAction extends ViewAction {

    private static final long serialVersionUID = 7051827783154251143L;

    public static final String MODEL_MODEL = "base.SharedPageViewAction";

    @Field(displayName = "分享码")
    private String sharedCode;

    @Field(displayName = "授权码")
    private String authorizationCode;

    @Field(displayName = "原分享参数")
    private String sharedParameters;

    @Field(displayName = "浏览器标题")
    private String browserTitle;

    @Field(displayName = "当前语言")
    private String language;

    @Field(displayName = "当前语言ISO编码")
    private String languageIsoCode;
}
