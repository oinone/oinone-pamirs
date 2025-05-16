package pro.shushi.pamirs.apps.api.tmodel;

import pro.shushi.pamirs.meta.annotation.Field;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.annotation.sys.Base;
import pro.shushi.pamirs.meta.base.TransientModel;

import java.util.List;

/**
 * 内页 试用引导
 *
 * <p> 内页 试用引导 </p>
 *
 * @author syj@shushi.pro
 * @version 1.0.0
 * date 2021-04-28 21:22
 */
@Base
@Model.model(AppTrialGuidance.MODEL_MODEL)
@Model(displayName = "内页 试用引导")
public class AppTrialGuidance extends TransientModel {

    public static final String MODEL_MODEL = "app.AppTrialGuidance";

    @Field.String
    @Field(displayName = "对应模块", summary = "中文名")
    private String module;

    @Field.String
    @Field(displayName = "对应模块名称", summary = "英文名")
    private String moduleName;

    @Field.String
    @Field(displayName = "模块Logo")
    private String moduleLogo;

    @Field.String
    @Field(displayName = "域名设置")
    private String tenantName;

    @Field.String
    @Field(displayName = "访问token")
    private String accessToken;

    @Field.one2many
    @Field(displayName = "试用引导")
    private List<AppTrialGuidanceProcess> processList;

    @Field.Integer
    @Field(displayName = "有效天数")
    private Integer trialDays;


}
